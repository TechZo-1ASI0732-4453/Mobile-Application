package com.techzo.cambiazo

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.ui.graphics.toArgb
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsControllerCompat
import com.techzo.cambiazo.common.Constants
import com.techzo.cambiazo.data.remote.AuthService
import com.techzo.cambiazo.data.repository.AuthRepository
import com.techzo.cambiazo.presentation.login.SignInViewModel
import com.techzo.cambiazo.presentation.navigate.NavScreen
import com.techzo.cambiazo.ui.theme.CambiazoTheme
import com.techzo.cambiazo.ui.theme.ScreenBackground
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory


class MainActivity : ComponentActivity() {

    private val service = Retrofit.Builder().baseUrl(Constants.BASE_URL)
        .addConverterFactory(GsonConverterFactory.create())
        .build().create(AuthService::class.java)


    private val viewModel = SignInViewModel(AuthRepository(service))

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        WindowCompat.setDecorFitsSystemWindows(window, false)
        enableEdgeToEdge()
        setContent {
            CambiazoTheme {
                val backgroundColor = ScreenBackground
                window.statusBarColor = backgroundColor.toArgb()
                WindowInsetsControllerCompat(window, window.decorView).isAppearanceLightStatusBars = true
                NavScreen(viewModel)
            }
        }
    }
}

/*
@Preview(showBackground = true, widthDp = 393, heightDp = 851)
@Composable
fun AppPreview() {
    CambiazoTheme {
        NavScreen()
    }
}
 */