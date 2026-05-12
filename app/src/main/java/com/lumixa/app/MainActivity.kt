package com.lumixa.app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.lumixa.app.navigation.AppNavigation
import com.lumixa.app.ui.theme.LUMIXATheme

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        enableEdgeToEdge()

        setContent {
            LUMIXATheme {
                AppNavigation()
            }
        }
    }
}