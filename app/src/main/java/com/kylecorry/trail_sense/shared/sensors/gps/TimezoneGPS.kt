package com.kylecorry.trail_sense.shared.sensors.gps

import android.os.SystemClock
import com.kylecorry.andromeda.sense.location.ISatelliteGPS
import com.kylecorry.andromeda.sense.location.Satellite
import com.kylecorry.luna.hooks.Hooks
import com.kylecorry.sol.time.Time
import com.kylecorry.sol.units.Bearing
import com.kylecorry.sol.units.Coordinate
import com.kylecorry.sol.units.DistanceUnits
import com.kylecorry.sol.units.Speed
import com.kylecorry.sol.units.TimeUnits
import com.kylecorry.trail_sense.shared.sensors.IntervalSensor
import java.time.Duration
import java.time.Instant
import java.time.ZoneId

class TimezoneGPS(updateFrequency: Long = 20L) :
    IntervalSensor(Duration.ofMillis(updateFrequency)),
    ISatelliteGPS, InactiveGPS, MockedGPS {

    private val hooks = Hooks()

    override val location: Coordinate
        get() = hooks.memo("location") {
            Time.getLocationFromTimeZone(ZoneId.systemDefault())
        }
    override val speed: Speed
        get() = Speed.from(0f, DistanceUnits.Meters, TimeUnits.Seconds)
    override val speedAccuracy: Float?
        get() = null
    override val time: Instant
        get() = Instant.now()
    override val verticalAccuracy: Float?
        get() = null
    override val horizontalAccuracy: Float?
        get() = null
    override val satellites: Int
        get() = 0
    override val hasValidReading: Boolean
        get() = true
    override val altitude: Float
        get() = 0f
    override val bearing: Bearing?
        get() = null
    override val bearingAccuracy: Float?
        get() = null
    override val fixTimeElapsedNanos: Long
        get() = SystemClock.elapsedRealtimeNanos()
    override val mslAltitude: Float
        get() = altitude
    override val rawBearing: Float?
        get() = null
    override val satelliteDetails: List<Satellite>?
        get() = null
}