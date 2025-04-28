package com.kylecorry.trail_sense.shared

import androidx.test.platform.app.InstrumentationRegistry
import com.kylecorry.sol.units.Coordinate
import com.kylecorry.sol.units.DistanceUnits
import kotlinx.coroutines.runBlocking
import org.junit.Test

class DEMTest {

    @Test
    fun getElevation() = runBlocking {
        val context = InstrumentationRegistry.getInstrumentation().targetContext

        println(
            DEM.getElevation(context, Coordinate(44.2691622567, -71.3020887916))
                .convertTo(DistanceUnits.Feet)
        )
    }

}