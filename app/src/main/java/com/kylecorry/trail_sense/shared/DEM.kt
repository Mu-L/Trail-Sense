package com.kylecorry.trail_sense.shared

import android.content.Context
import android.util.Size
import com.kylecorry.andromeda.core.cache.LRUCache
import com.kylecorry.andromeda.core.coroutines.onIO
import com.kylecorry.andromeda.core.units.PixelCoordinate
import com.kylecorry.sol.units.Coordinate
import com.kylecorry.sol.units.Distance
import com.kylecorry.trail_sense.shared.data.GeographicImageSource
import kotlin.math.abs

object DEM {

    // Cache
    private var cache = LRUCache<PixelCoordinate, Distance>(size = 10)

    // Image data source
    private const val a = 1.0
    private const val b = 0.0

    private const val layer1A = 0.052238043397665024
    private const val layer1B = 4881.5
    private const val layer2A = 0.3187499940395355
    private const val layer2B = 0.0
    private const val layer3A = 0.03582246974110603
    private const val layer3B = -800.0

    private const val file = "dem.webp"
    private val source = GeographicImageSource(
        Size(360 * 40, 180 * 40),
        precision = 4,
        decoder = GeographicImageSource.scaledDecoder(a, b),
    )

    suspend fun getElevation(context: Context, location: Coordinate): Distance = onIO {
        val pixel = source.getPixel(location)
        cache.getOrPut(pixel) {
            val layers = source.read(context, file, location)

            val layerValues = listOf(
                Triple(layer1A, layer1B, layers[0]),
                Triple(layer2A, layer2B, layers[1]),
                Triple(layer3A, layer3B, layers[2])
            )

            val closestTo127 = layerValues.minBy { abs(it.third - 127) }

            Distance.meters((closestTo127.third / closestTo127.first - closestTo127.second).toFloat())
        }
    }
}