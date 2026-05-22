package com.lumixa.app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import com.lumixa.app.data.preferences.ThemePreferences
import com.lumixa.app.navigation.AppNavigation
import com.lumixa.app.ui.theme.LUMIXATheme

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        enableEdgeToEdge()

        setContent {
            val themePreferences = remember { ThemePreferences(this) }
            var darkModeEnabled by remember { mutableStateOf(themePreferences.isDarkModeEnabled()) }

            LUMIXATheme(darkTheme = darkModeEnabled) {
                AppNavigation(
                    darkModeEnabled = darkModeEnabled,
                    onThemeChange = { enabled ->
                        darkModeEnabled = enabled
                        themePreferences.setDarkModeEnabled(enabled)
                    }
                )
            }
        }
    }
}