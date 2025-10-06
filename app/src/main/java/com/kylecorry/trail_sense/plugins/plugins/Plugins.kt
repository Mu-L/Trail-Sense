package com.kylecorry.trail_sense.plugins.plugins

import android.content.Context
import com.kylecorry.andromeda.core.system.Resources
import com.kylecorry.luna.hooks.Hooks
import com.kylecorry.trail_sense.plugin.sample.SamplePluginRegistration

object Plugins {
    private val hooks = Hooks()
    private val registry: List<PluginRegistration> = listOf(
        SamplePluginRegistration
    )

    fun isPluginAvailable(context: Context, pluginId: Long): Boolean {
        return getPlugin(context, pluginId) != null
    }

    fun getPlugin(context: Context, pluginId: Long): Plugin? {
        return getPlugins(context).firstOrNull { it.id == pluginId }
    }

    fun getPackageId(context: Context, pluginId: Long): String? {
        return getPlugin(context, pluginId)?.getInstalledPackageId(context)
    }

    fun getPlugins(context: Context, availableOnly: Boolean = true): List<Plugin> {
        val plugins = hooks.memo("plugins", Resources.getLocale(context).language) {
            registry.map { it.getPlugin(context.applicationContext) }
        }

        return plugins.filter { !availableOnly || it.isAvailable(context) }
    }

    const val PLUGIN_SAMPLE = 1L
}