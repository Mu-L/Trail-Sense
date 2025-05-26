package com.kylecorry.trail_sense.tools.maps.infrastructure.tiles

import android.graphics.Bitmap
import android.util.Size
import com.kylecorry.sol.science.geology.CoordinateBounds
import com.kylecorry.trail_sense.shared.ParallelCoroutineRunner
import com.kylecorry.trail_sense.tools.maps.domain.PhotoMap
import java.util.concurrent.ConcurrentHashMap

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

    private val baseMapZoomLevels = listOf(
        2.5f,
        5f,
        10f,
        20f,
        40f,
        80f,
        160f,
        320f,
        740f,
        1480f,
        2960f,
    )

    suspend fun loadTiles(maps: List<PhotoMap>, bounds: CoordinateBounds, metersPerPixel: Float) {
        // Step 1: Split the visible area into tiles (geographic)
        val nextZoom = baseMapZoomLevels.firstOrNull { it >= metersPerPixel }
        val tiles = TileMath.getTiles(bounds, metersPerPixel.toDouble()) + if (nextZoom != null) {
            TileMath.getTiles(bounds, nextZoom.toDouble())
        } else {
            emptyList()
        }

        // Step 2: For each tile, determine which map(s) will supply it.
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
//                if (!tileSources.containsKey(key)) {
//                    tileCache[key]?.forEach { bitmap -> bitmap.recycle() }
//                } else {
                // If the tile is still relevant, keep it
                newTiles[key] = tileCache[key]!!
//                }
            }
        }

        synchronized(lock) {
            tileCache = newTiles
        }

        for (source in tileSources) {
            if (newTiles.containsKey(source.key)) {
                continue
            }
            // Load tiles from the bitmap
            val entries = mutableListOf<Bitmap>()
            val parallel = ParallelCoroutineRunner()

            synchronized(lock) {
                newTiles[source.key] = entries
            }

            parallel.run(source.value) {
                val loader = PhotoMapRegionLoader(it)
                val image = loader.load(
                    source.key,
                    Size(TileMath.WORLD_TILE_SIZE, TileMath.WORLD_TILE_SIZE)
                )
                if (image != null) {
                    synchronized(lock) {
                        entries.add(image)
                    }
                }
            }
        }

        // Remove old bitmaps
        synchronized(lock) {
            // Recycle
            tileCache.filter { !tileSources.containsKey(it.key) }
                .forEach { (_, bitmaps) ->
                    bitmaps.forEach { it.recycle() }
                }

            tileCache = tileCache.filter { tileSources.containsKey(it.key) }
        }
    }
}