package com.techzo.cambiazo

import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.ui.graphics.toArgb
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsControllerCompat
import com.techzo.cambiazo.presentation.navigate.NavScreen
import com.techzo.cambiazo.ui.theme.CambiazoTheme
import com.techzo.cambiazo.ui.theme.ScreenBackground
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        WindowCompat.setDecorFitsSystemWindows(window, false)
        enableEdgeToEdge()
        setContent {
            CambiazoTheme {
                val backgroundColor = ScreenBackground
                window.statusBarColor = backgroundColor.toArgb()
                WindowInsetsControllerCompat(window, window.decorView).isAppearanceLightStatusBars = true
                NavScreen(this@MainActivity)
            }
        }
    }
}