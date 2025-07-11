package com.kylecorry.trail_sense.shared.map_layers.preferences.repo

import android.content.Context
import com.kylecorry.andromeda.preferences.BooleanPreference
import com.kylecorry.andromeda.preferences.IntPreference
import com.kylecorry.trail_sense.settings.infrastructure.PreferenceRepo

class PhotoMapMapLayerPreferences(
    context: Context,
    mapId: String,
    enabledByDefault: Boolean = true
) : PreferenceRepo(context) {
    var isEnabled by BooleanPreference(
        cache,
        "pref_${mapId}_map_layer_enabled",
        enabledByDefault
    )

    val opacity by IntPreference(
        cache,
        "pref_${mapId}_map_layer_opacity",
        50 // percent
    )
}