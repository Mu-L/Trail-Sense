package com.kylecorry.trail_sense.tools.climate.infrastructure.vegetation

import android.content.Context
import android.util.Size
import androidx.core.graphics.green
import androidx.core.graphics.red
import com.kylecorry.andromeda.core.cache.LRUCache
import com.kylecorry.andromeda.core.coroutines.onIO
import com.kylecorry.andromeda.core.units.PixelCoordinate
import com.kylecorry.sol.math.SolMath.roundPlaces
import com.kylecorry.sol.units.Coordinate
import com.kylecorry.trail_sense.shared.andromeda_temp.GeographicImageUtils
import com.kylecorry.trail_sense.shared.data.GeographicImageSource
import com.kylecorry.trail_sense.tools.climate.domain.VegetationType

object VegetationModel {

    // Cache
    private var cache = LRUCache<PixelCoordinate, List<VegetationType>>(size = 5)

    // Image data source
    private const val a = 1.0
    private const val b = 0.0
    private const val file = "vegetation.webp"
    private val source = GeographicImageSource(
        Size(576, 361),
        decoder = GeographicImageSource.scaledDecoder(a, b),
        interpolate = false
    )
    private val locationToPixelCache = LRUCache<Coordinate, PixelCoordinate?>(size = 20)

    suspend fun getVegetationTypes(context: Context, location: Coordinate): List<VegetationType> =
        onIO {
            val pixel = locationToPixelCache.getOrPut(
                Coordinate(
                    location.latitude.roundPlaces(1),
                    location.longitude.roundPlaces(1)
                )
            ) {
                GeographicImageUtils.getNearestPixelOfAsset(
                    source,
                    context,
                    location,
                    "vegetation.webp",
                    20,
                    hasValue = { it.red > 0 || it.green > 0 },
                    hasMappedValue = { it[0] > 0f || it[1] > 0f }
                )
            } ?: return@onIO emptyList()
            cache.getOrPut(pixel) {
                val values = source.read(context, file, pixel).map { it.toInt() }
                VegetationType.entries.filter {
                    values.contains(it.id.toInt())
                }
            }
        }
}