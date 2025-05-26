package com.kylecorry.trail_sense.tools.maps.infrastructure.tiles

import android.graphics.Bitmap
import android.util.Size
import com.kylecorry.luna.coroutines.onIO
import com.kylecorry.sol.science.geology.CoordinateBounds
import com.kylecorry.trail_sense.shared.ParallelCoroutineRunner
import com.kylecorry.trail_sense.tools.maps.domain.PhotoMap
import java.util.concurrent.ConcurrentHashMap
import kotlin.math.hypot

class TileLoader {

    var tileCache: Map<Tile, List<Bitmap>> = emptyMap()
        private set

    var lock = Any()

    fun clearCache() {
        synchronized(lock) {
            tileCache.forEach { (_, bitmaps) ->
                bitmaps.forEach { it.recycle() }
            }
            tileCache = emptyMap()
        }
    }

    suspend fun loadTiles(maps: List<PhotoMap>, bounds: CoordinateBounds, metersPerPixel: Float) =
        onIO {
            val tiles = TileMath.getTiles(bounds, metersPerPixel.toDouble())
            val tileSources = mutableMapOf<Tile, List<PhotoMap>>()
            val sourceSelector = MercatorTileSourceSelector(maps)
            for (tile in tiles) {
                val sources = sourceSelector.getSources(tile.getBounds())
                if (sources.isNotEmpty()) {
                    tileSources[tile] = sources.take(2)
                }
            }

            val newTiles = ConcurrentHashMap<Tile, List<Bitmap>>()
            synchronized(lock) {
                tileCache.keys.forEach { key ->
                    newTiles[key] = tileCache[key] ?: return@forEach
                }
                tileCache = newTiles
            }

            val parallel = ParallelCoroutineRunner()

            val middleX = tileSources.keys.map { it.x }.average()
            val middleY = tileSources.keys.map { it.y }.average()

            val sortedEntries = tileSources.entries
                .sortedBy { hypot(it.key.x - middleX, it.key.y - middleY) }

            parallel.run(sortedEntries) { source ->
                val tile = source.key

                if (newTiles.containsKey(tile)) {
                    return@run
                }

                // Load tiles from the bitmap
                val entries = mutableListOf<Bitmap>()

                source.value.forEach { photoMap ->
                    val loader = PhotoMapRegionLoader(photoMap)
                    val image = loader.load(
                        tile,
                        Size(TileMath.WORLD_TILE_SIZE, TileMath.WORLD_TILE_SIZE)
                    )

                    if (image != null && !image.isRecycled) {
                        entries.add(image)
                    }
                }

                if (entries.isNotEmpty()) {
                    newTiles[tile] = entries
                }
            }

            synchronized(lock) {
                val keysToRemove = mutableListOf<Tile>()
                newTiles.forEach { (tile, bitmaps) ->
                    if (!tileSources.containsKey(tile)) {
                        bitmaps.forEach { it.recycle() }
                        keysToRemove.add(tile)
                    }
                }
                keysToRemove.forEach {
                    newTiles.remove(it)
                }

            }
        }
}