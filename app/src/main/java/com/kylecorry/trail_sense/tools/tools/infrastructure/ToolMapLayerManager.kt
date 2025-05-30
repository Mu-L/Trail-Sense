package com.kylecorry.trail_sense.tools.tools.infrastructure

import android.content.Context
import com.kylecorry.andromeda.core.cache.AppServiceRegistry
import com.kylecorry.sol.science.geology.CoordinateBounds
import com.kylecorry.sol.units.Coordinate
import com.kylecorry.trail_sense.shared.preferences.PreferencesSubsystem
import com.kylecorry.trail_sense.tools.maps.infrastructure.layers.ILayerManager
import com.kylecorry.trail_sense.tools.maps.infrastructure.layers.MultiLayerManager
import com.kylecorry.trail_sense.tools.navigation.ui.layers.IMapView

class ToolMapLayerManager(
    context: Context,
    private val managerId: String,
    availableLayers: List<String>,
    private val settingsOverrides: Map<String, Map<String, Any?>> = emptyMap()
) {
    private val allToolLayers = Tools.getTools(context).flatMap { it.mapLayers }
    private val toolLayers = availableLayers.mapNotNull { layerId ->
        allToolLayers.firstOrNull { it.id == layerId }
    }
    private val layers = toolLayers.associate { it.id to it.layer() }

    private val prefs = AppServiceRegistry.get<PreferencesSubsystem>().preferences
    private var layerManager: ILayerManager? = null

    var key = 0
        private set

    fun resume(context: Context, view: IMapView) {

        val enabledMap = toolLayers.associate {
            it.id to ((prefs.getBoolean(getPreferenceKey(it.id, "enabled"))
                ?: (settingsOverrides[it.id]?.get("enabled") as Boolean?)) != false)
        }

        layers.forEach { layer ->
            val toolLayer = toolLayers.firstOrNull { it.id == layer.key } ?: return@forEach
            val overrides = settingsOverrides[layer.key] ?: emptyMap()
            overrides.forEach { settingOverride ->
                layer.value.setValue(
                    settingOverride.key,
                    settingOverride.value
                )
            }
            toolLayer.settings.forEach { setting ->
                // TODO: Load from preferences and call setValue if not in overrides
            }
        }

        view.setLayers(layers.filter { enabledMap[it.key] == true }.map { it.value })

        layerManager = MultiLayerManager(
            toolLayers.filter { enabledMap[it.id] == true }
                .mapNotNull {
                    it.manager?.invoke(
                        context,
                        layers[it.id] ?: return@mapNotNull null
                    )
                }
        )

        key += 1

        layerManager?.start()
    }

    private fun getPreferenceKey(layerId: String, settingId: String): String {
        return "pref_${managerId}_${layerId}_${settingId}"
    }

    fun pause(context: Context, view: IMapView) {
        layerManager?.stop()
        layerManager = null
    }

    fun onBearingChanged(bearing: Float) {
        layerManager?.onBearingChanged(bearing)
    }

    fun onLocationChanged(location: Coordinate, accuracy: Float?) {
        layerManager?.onLocationChanged(location, accuracy)
    }

    fun onBoundsChanged(bounds: CoordinateBounds) {
        layerManager?.onBoundsChanged(bounds)
    }
}