package com.techzo.cambiazo

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.ui.graphics.toArgb
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsControllerCompat
import androidx.lifecycle.ViewModelProvider
import com.techzo.cambiazo.presentation.navigate.NavScreen
import com.techzo.cambiazo.presentation.navigate.NavigationViewModel
import com.techzo.cambiazo.presentation.profile.subscription.SubscriptionViewModel
import com.techzo.cambiazo.ui.theme.CambiazoTheme
import com.techzo.cambiazo.ui.theme.ScreenBackground
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        installSplashScreen()
        super.onCreate(savedInstanceState)
        WindowCompat.setDecorFitsSystemWindows(window, false)
        enableEdgeToEdge()

        val navViewModel = ViewModelProvider(this)[NavigationViewModel::class.java]

        setContent {
            CambiazoTheme {
                val backgroundColor = ScreenBackground
                window.statusBarColor = backgroundColor.toArgb()
                WindowInsetsControllerCompat(window, window.decorView).isAppearanceLightStatusBars = true

                NavScreen(
                    activity = this,
                    navViewModel = navViewModel
                )
            }
        }
    }

    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)
        intent?.data?.let { uri ->
            if (uri.scheme == "com.techzo.cambiazo" && uri.host == "paypalpay") {
                val token = uri.getQueryParameter("token")
                if (token != null) {
                    val viewModel = ViewModelProvider(this)[SubscriptionViewModel::class.java]
                    val navViewModel = ViewModelProvider(this)[NavigationViewModel::class.java]

                    viewModel.captureOrder(token) {
                        navViewModel.triggerRedirectToSubscription()
                    }
                }
            }
        }
    }
}
