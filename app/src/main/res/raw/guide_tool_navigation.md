The Navigation tool can be used to navigate to a beacon, follow a path, or follow a bearing.

## Compass

The compass can be used for both orientation and navigation. Your bearing is displayed at the top of the screen.

This feature is not available if your device does not have a compass.

### Nearby

You can choose to display nearby beacons on the compass. The following settings control the nearby beacons:

- **Settings > Navigation > Show nearby beacons**: Determines if nearby beacons are shown on the compass.
- **Settings > Navigation > Nearby beacon radius**: Determines the maximum distance a beacon can be from your location to be shown on the compass.
- **Settings > Navigation > Nearby beacons**: Limits the number of nearby beacons shown on the compass.

Nearby beacons will be shown as arrows around the compass. The arrows will point to the beacon, and when you are facing the beacon, more information about it will be shown at the bottom of the screen.

If you have the radar compass enabled, the nearby beacons will be shown on the compass itself.

### Radar compass

The radar compass shows nearby beacons, paths, and tides as a map-like radar display. The radar compass is available with nearby beacons enabled. Once nearby beacons are enabled, you can turn on the radar compass using "Settings > Navigation > Show nearby radar compass".

You can pinch to zoom the radar compass, which will change the nearby beacon radius.

If your device does not have a compass, you can still choose to display the compass dial ticks by enabling the Settings > Navigation > 'Show dial ticks on nearby radar' option. Please note, that without a compass sensor, moving your device will not change the direction of the radar compass.

Layers are used to display information on the radar compass. You can find layer settings in Settings > Navigation or by long pressing the radar compass. You can choose to hide and show layers using the switch next to each layer's name. The opacity setting for each layer determines how transparent the layer is. 0 is fully transparent and 100 is fully opaque.

#### Elevation
This layer shows the elevation from the digital elevation model (DEM) as color. You can change the DEM in Settings > Altimeter.

Settings:

- **Color**: The color scale of the pixels. The color will change based on elevation.

#### Hillshade
This layer draws shadows to help see elevation in the terrain from the digital elevation model (DEM). You can change the DEM in Settings > Altimeter.

#### Photo Maps
This layer shows visible Photo Maps, with the most zoomed-in map appearing on top. You can add new maps in the Photo Maps tool.

Settings:

- **Load PDF tiles**: If enabled, PDF tiles will be loaded for maps that have a PDF version available. This is slower but provides higher resolution maps.

#### Contours
This layer shows contour lines generated from the digital elevation model (DEM) and can be used to see the steepness and elevation of map features. You can change the DEM in Settings > Altimeter.

Settings:

- **Show labels**: Determines if contour labels are shown on the map.
- **Color**: The color of the contour lines, some options are color scales which change based on elevation.

### Cell towers
This layer shows nearby cell towers with the accuracy of the tower's location shown as a circle under the tower. These are approximate tower locations from OpenCelliD, Mozilla Location Service, and FCC Antenna Registrations.

#### Paths
This layer shows visible paths. You can add new paths in the Paths tool.

Settings:

- **Background color**: The background color to render behind paths for increased visibility.

#### Beacons
This layer shows visible beacons. You can add new beacons in the Beacons tool.

#### Tides
This layer shows visible tides. You can add new tides in the Tides tool.

#### My location
This layer shows your location, which direction you are facing (if you have a compass), and the accuracy of your GPS.

Settings:

- **Show GPS accuracy**: Determines if the GPS accuracy circle is visible.

### Linear compass

The linear compass is displayed when you hold your phone vertically. You can also use the sighting compass feature with the linear compass by tapping the camera icon on the right side of the screen. This will display the compass overlaid on a camera viewfinder. With the sighting compass active, you can pinch to zoom the camera viewfinder.

You can enable or disable the linear compass under Settings > Navigation > Show linear compass.

### North reference

By default, it will point to True North, but it can be configured to point to magnetic north in the compass settings. The north reference is displayed at the bottom of the screen.

### Sun and moon

The compass shows the direction of the sun and moon, allowing you to navigate using them and verify the accuracy of the compass. You can configure the sun and moon display in Settings > Navigation > Show sun/moon on compass. The options are:

- **Always**: Always show the sun and moon on the compass.
- **When up**: Only show the sun and moon when they are above the horizon.
- **Never**: Never show the sun and moon on the compass.

### Calibration

Phone compasses are not always accurate, so you should frequently calibrate your compass. You can calibrate your compass by waving your phone in a figure-8 pattern. For more detailed instructions and a visual, click the status icons in the bottom-left of the Navigation tool. This will guide you in calibrating your compass and also show details about location accuracy.

## Beacon navigation

You can navigate to a beacon by tapping the navigate button in the bottom-right. This will open the Beacons tool, where you can select a beacon to navigate to. See the Beacons guide for more information.

While navigating to a beacon, the direction will be displayed on the compass and all other nearby beacons will become transparent. A navigation panel will display at the bottom of your screen with the following information:
- **Name**: The name of the beacon you are navigating to.
- **Distance**: The distance to the beacon.
- **Bearing**: The bearing to the beacon.
- **ETA**: The estimated time of arrival to the beacon.
- **Elevation**: The elevation of the beacon and difference from your current elevation.
- **Notes**: Any notes you have for the beacon. This will show as an icon if there are notes, and you can tap it to see the notes.

You can stop navigating to a beacon by tapping the 'X' button in the bottom-right.

You can quickly create a beacon from your current location by long-pressing the navigation icon at the bottom-right. This will open the Beacons tool with the location and elevation pre-filled.

For more information on beacons, see the Beacons guide.

## Bearing navigation

You can navigate using a bearing by tapping the compass to set a bearing. The set bearing will be displayed on the compass. You can tap the compass again to clear the bearing.

## Path navigation

If you have the radar compass enabled, you can see nearby paths on the compass. You can't currently navigate along a path, but you can use the compass to see where the path is relative to you. If you have Backtrack running, you can see your current position on the path. For more information, see the Paths guide.

## Location

Your current location is shown at the top of the screen. You can tap it to see more details about your location. The details panel shows the following information:

- **Share**: Choose to copy your location to your clipboard, share it as a QR code, open it in a map app, or share it as a text message.
- **Format**: The coordinate format to see your location in. This defaults to the format you have in Settings > Units > Coordinate format. Changes made here are temporary and will not change your default coordinate format.
- **Accuracy**: The accuracy of your location.
- **Satellites**: The number of satellites used to calculate your location.
- **Time since last fix**: The time since your location was last updated.
- **Datum**: The datum of the location. This will always be WGS 84.

You can long-press the location to quickly bring up the share menu.

## Elevation

Your elevation is shown at the top-left of the screen. You can tap it to see a history of your elevation. The history is only available if you have Backtrack or the weather monitor running - see the Paths or Weather guide for details on how to turn this on. You can adjust the length of the history by clicking the "Last 24h" dropdown at the top of the history panel.

## Speed

Your speed is shown at the top-right of the screen. You can change the source of the speed in Settings > Navigation > Speedometer. The options are:

- **Current speed (GPS)**: The speed reported by your GPS.
- **Average speed (Backtrack)**: The average speed calculated by Backtrack. This requires Backtrack to be running. See the Paths guide for more information.
- **Current speed (Pedometer)**: The speed calculated by your phone's pedometer. This requires the pedometer to be running. See the Pedometer guide for more information.
- **Average speed (Pedometer)**: The average speed calculated by your phone's pedometer since you started tracking. This requires the pedometer to be running. See the Pedometer guide for more information.

## Widgets
The following widgets can be placed on your device's homescreen or viewed in-app:

- **Elevation**: Shows your current elevation.
- **Location**: Shows your current location.