package com.kylecorry.trail_sense.tools.tools.infrastructure

import android.content.Context
import android.os.Bundle
import androidx.core.os.bundleOf
import com.kylecorry.trail_sense.tools.maps.infrastructure.layers.ILayerManager
import com.kylecorry.trail_sense.tools.navigation.ui.layers.ILayer

enum class ToolMapLayerSettingType {
    Switch,
    Slider
}

data class ToolMapLayerSetting(
    val id: String,
    val name: String,
    val type: ToolMapLayerSettingType,
    val args: Bundle = bundleOf()
)

data class ToolMapLayer(
    val id: String,
    val name: String,
    val layer: () -> ILayer,
    val manager: ((context: Context, layer: ILayer) -> ILayerManager)? = null,
    val settings: List<ToolMapLayerSetting> = emptyList()
)
