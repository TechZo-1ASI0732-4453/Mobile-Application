package com.techzo.cambiazo

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.ui.graphics.toArgb
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsControllerCompat
import com.techzo.cambiazo.common.Constants
import com.techzo.cambiazo.data.remote.auth.AuthService
import com.techzo.cambiazo.data.remote.products.ProductCategoryService
import com.techzo.cambiazo.data.remote.products.ProductService
import com.techzo.cambiazo.data.repository.AuthRepository
import com.techzo.cambiazo.data.repository.ProductCategoryRepository
import com.techzo.cambiazo.data.repository.ProductRepository
import com.techzo.cambiazo.presentation.explorer.ExplorerListViewModel
import com.techzo.cambiazo.presentation.login.SignInViewModel
import com.techzo.cambiazo.presentation.navigate.NavScreen
import com.techzo.cambiazo.presentation.register.SignUpViewModel
import com.techzo.cambiazo.ui.theme.CambiazoTheme
import com.techzo.cambiazo.ui.theme.ScreenBackground
import dagger.hilt.android.AndroidEntryPoint
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory


@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        WindowCompat.setDecorFitsSystemWindows(window, false)
        enableEdgeToEdge()
        setContent {
            CambiazoTheme {
                val backgroundColor = ScreenBackground
                window.statusBarColor = backgroundColor.toArgb()
                WindowInsetsControllerCompat(window, window.decorView).isAppearanceLightStatusBars = true
                NavScreen()
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