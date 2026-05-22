package com.lumixa.app.data.preferences

import android.content.Context

class ThemePreferences(private val context: Context) {
    private val prefs = context.getSharedPreferences("lumixa_theme_preferences", Context.MODE_PRIVATE)

    fun isDarkModeEnabled(): Boolean {
        return prefs.getBoolean(KEY_DARK_MODE, false)
    }

    fun setDarkModeEnabled(enabled: Boolean) {
        prefs.edit().putBoolean(KEY_DARK_MODE, enabled).apply()
    }

    private companion object {
        const val KEY_DARK_MODE = "dark_mode_enabled"
    }
}
