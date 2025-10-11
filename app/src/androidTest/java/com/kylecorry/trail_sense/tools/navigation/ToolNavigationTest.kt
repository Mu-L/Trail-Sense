package com.kylecorry.trail_sense.tools.navigation

import com.kylecorry.sol.units.Coordinate
import com.kylecorry.sol.units.Distance
import com.kylecorry.trail_sense.R
import com.kylecorry.trail_sense.test_utils.AutomationLibrary.any
import com.kylecorry.trail_sense.test_utils.AutomationLibrary.click
import com.kylecorry.trail_sense.test_utils.AutomationLibrary.clickOk
import com.kylecorry.trail_sense.test_utils.AutomationLibrary.hasText
import com.kylecorry.trail_sense.test_utils.AutomationLibrary.isVisible
import com.kylecorry.trail_sense.test_utils.AutomationLibrary.longClick
import com.kylecorry.trail_sense.test_utils.AutomationLibrary.not
import com.kylecorry.trail_sense.test_utils.AutomationLibrary.optional
import com.kylecorry.trail_sense.test_utils.AutomationLibrary.scrollUntil
import com.kylecorry.trail_sense.test_utils.AutomationLibrary.string
import com.kylecorry.trail_sense.test_utils.TestUtils.back
import com.kylecorry.trail_sense.test_utils.TestUtils.context
import com.kylecorry.trail_sense.test_utils.ToolTestBase
import com.kylecorry.trail_sense.test_utils.views.Side
import com.kylecorry.trail_sense.test_utils.views.toolbarButton
import com.kylecorry.trail_sense.tools.beacons.domain.Beacon
import com.kylecorry.trail_sense.tools.beacons.infrastructure.persistence.BeaconService
import com.kylecorry.trail_sense.tools.tools.infrastructure.Tools
import kotlinx.coroutines.runBlocking
import org.junit.Test

class ToolNavigationTest : ToolTestBase(Tools.NAVIGATION) {
    @Test
    fun verifyBasicFunctionality() {
        // Bearing
        hasText(Regex("\\s*\\d+°\\s+[NSEW]+"))
        // Location
        hasText(Regex("-?\\d+\\.\\d+°,\\s+-?\\d+\\.\\d+°"))
        // Elevation
        hasText(Regex("-?\\d+ ft"))
        // Speed
        hasText(Regex("\\d+\\.\\d+ mph"))

        canDisplaySensorStatus()

        // Compass
        canAdjustLayers()
        canSetDestinationBearing()
        canNavigate()
    }

    private fun canAdjustLayers() {
        optional {
            longClick(R.id.radar_compass)
            scrollUntil { hasText("Elevation") }
            scrollUntil { hasText("Hillshade") }
            scrollUntil { hasText("Photo Maps") }
            scrollUntil { hasText("Contours") }
            scrollUntil { hasText("Cell towers") }
            scrollUntil { hasText("Paths") }
            scrollUntil { hasText("Beacons") }
            scrollUntil { hasText("Tides") }
            scrollUntil { hasText("My location") }
            click(toolbarButton(R.id.title, Side.Right))
        }
    }

    private fun canSetDestinationBearing() {
        repeat(2) {
            any(
                { click(R.id.round_compass, waitForTime = 0) },
                { click(R.id.radar_compass, waitForTime = 0) },
                { click(R.id.linear_compass, waitForTime = 0) }
            )
        }
    }

    private fun canNavigate() {
        // Create a beacon
        runBlocking {
            BeaconService(context).add(
                Beacon(
                    0,
                    "Test Beacon",
                    Coordinate(1.0, -1.0),
                    comment = "Test Comment",
                    elevation = Distance.feet(100f).meters().value
                )
            )
        }

        click(R.id.beaconBtn)
        click("Test Beacon")
        click(string(R.string.navigate))

        hasText(string(R.string.calibrate_compass_dialog_title))
        clickOk()

        hasText("Test Beacon")
        hasText(Regex("1.000000°,\\s+-1.000000°"))
        hasText("100 ft")
        hasText(R.id.navigation_distance, Regex("\\d+(\\.\\d+)? (ft|mi)"))
        hasText(R.id.navigation_distance, Regex("\\d+° [NSEW]+"))
        hasText(R.id.navigation_eta, Regex("(\\d+h)?\\s?(\\d+m)?\\s?(\\d+s)?"))
        hasText(R.id.navigation_eta, Regex("\\d+:\\d+?\\s(AM|PM)"))
        hasText(R.id.navigation_elevation, Regex("[-+]?\\d+ ft"))
        click(toolbarButton(R.id.navigation_sheet_title, Side.Left))
        hasText("Test Comment")
        clickOk()

        click("Test Beacon")
        hasText(R.id.beacon_title, "Test Beacon")
        back()

        click(toolbarButton(R.id.navigation_sheet_title, Side.Right))
        click("Yes")
        not { hasText("Test Beacon", waitForTime = 0) }

        hasWorkingTrueNorthIndicator()

        hasWorkingQuickActions()

        canCreateBeacon()
    }

    private fun canDisplaySensorStatus() {
        click(Regex("(Poor|Moderate|Good|Stale|Unavailable)"))
        hasText(string(R.string.accuracy_info_title))
        hasText("GPS location accuracy")
        optional {
            hasText(Regex("GPS location accuracy: ± \\d+ ft"), waitForTime = 0)
        }
        optional {
            hasText(Regex("GPS elevation accuracy: ± \\d+ ft"), waitForTime = 0)
        }
        optional {
            hasText(Regex("GPS satellites: \\d+"), waitForTime = 0)
        }
        hasText(string(R.string.calibrate_compass_dialog_content, string(android.R.string.ok)))
        hasText(string(R.string.gps_accuracy_tip))
        clickOk()
    }

    private fun hasWorkingQuickActions() {
        click(toolbarButton(R.id.navigation_title, Side.Left))
        isVisible(R.id.paths_title)
        back()

        click(toolbarButton(R.id.navigation_title, Side.Right))
        clickOk()
        isVisible(R.id.map_list_title)
        back()
    }

    private fun hasWorkingTrueNorthIndicator() {
        click(R.id.north_reference_indicator)
        hasText(string(R.string.true_north))
        hasText(string(R.string.true_north_description))
        click(string(R.string.settings))
        hasText(string(R.string.pref_compass_sensor_title))
        back()
    }

    private fun canCreateBeacon() {
        longClick(R.id.beaconBtn)
        hasText(string(R.string.create_beacon))
        hasText(Regex("-?\\d+\\.\\d+°,\\s+-?\\d+\\.\\d+°"))
        back()
        click(string(R.string.dialog_leave))
        back()
    }
}