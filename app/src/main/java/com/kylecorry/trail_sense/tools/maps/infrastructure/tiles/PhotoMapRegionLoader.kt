package com.kylecorry.trail_sense.tools.maps.infrastructure.tiles

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Rect
import android.util.Size
import com.kylecorry.andromeda.core.cache.AppServiceRegistry
import com.kylecorry.luna.coroutines.onIO
import com.kylecorry.sol.math.Vector2
import com.kylecorry.sol.science.geology.CoordinateBounds
import com.kylecorry.trail_sense.shared.andromeda_temp.ImageRegionLoader
import com.kylecorry.trail_sense.shared.extensions.toAndroidSize
import com.kylecorry.trail_sense.shared.io.FileSubsystem
import com.kylecorry.trail_sense.tools.maps.domain.PhotoMap
import kotlin.math.max
import kotlin.math.min

class PhotoMapRegionLoader(private val map: PhotoMap) {

    private val optionsCache = mutableMapOf<Pair<Int, Int>, BitmapFactory.Options>()

    suspend fun load(tile: Tile, maxSize: Size? = null): Bitmap? {
        return load(tile.getBounds(), maxSize)
    }

    suspend fun load(bounds: CoordinateBounds, maxSize: Size? = null): Bitmap? = onIO {
        // TODO: Map rotation (get the rotated area and crop?)
        val fileSystem = AppServiceRegistry.get<FileSubsystem>()
        val projection = map.projection

        val center = Vector2(map.metadata.size.width / 2f, map.metadata.size.height / 2f)
        val northWest =
            projection.toPixels(bounds.northWest)//.rotate(-map.calibration.rotation, center)
        val southEast =
            projection.toPixels(bounds.southEast)//.rotate(-map.calibration.rotation, center)

        val size = map.metadata.unscaledPdfSize ?: map.metadata.size

        val region = Rect(
            min(northWest.x.toInt(), southEast.x.toInt()),
            min(northWest.y.toInt(), southEast.y.toInt()),
            max(northWest.x.toInt(), southEast.x.toInt()),
            max(northWest.y.toInt(), southEast.y.toInt())
        )

        // Early return for invalid regions
        if (region.width() <= 0 || region.height() <= 0) {
            return@onIO null
        }

        // TODO: Load PDF region
        fileSystem.streamLocal(map.filename).use { stream ->
            val options = getBitmapOptions(region, maxSize)

            ImageRegionLoader.decodeBitmapRegionWrapped(
                stream,
                region,
                size.toAndroidSize(),
                options = options,
                enforceBounds = false
            )
        }
    }

    private fun getBitmapOptions(region: Rect, maxSize: Size?): BitmapFactory.Options {
        val key = Pair(region.width(), region.height())

        return optionsCache.getOrPut(key) {
            BitmapFactory.Options().apply {
                if (maxSize != null) {
                    inSampleSize = calculateInSampleSize(
                        region.width(),
                        region.height(),
                        maxSize.width,
                        maxSize.height
                    )
                    inScaled = true
                    // Use RGB_565 for better memory efficiency with slight quality trade-off
                    inPreferredConfig = Bitmap.Config.RGB_565
                    // Enable bitmap reuse for better memory management
                    inMutable = false
                    // Optimize for faster decoding
                    inTempStorage = ByteArray(16 * 1024)
                }
            }
        }
    }

    private fun calculateInSampleSize(
        sourceWidth: Int,
        sourceHeight: Int,
        reqWidth: Int,
        reqHeight: Int
    ): Int {
        // Raw height and width of image
        var inSampleSize = 1

        if (sourceHeight > reqHeight || sourceWidth > reqWidth) {

            val halfHeight: Int = sourceHeight / 2
            val halfWidth: Int = sourceWidth / 2

            // Calculate the largest inSampleSize value that is a power of 2 and keeps both
            // height and width larger than the requested height and width.
            while (halfHeight / inSampleSize >= reqHeight && halfWidth / inSampleSize >= reqWidth) {
                inSampleSize *= 2
            }
        }

        return inSampleSize
    }

}