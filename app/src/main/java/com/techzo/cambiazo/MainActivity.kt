package com.techzo.cambiazo

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import com.techzo.cambiazo.presentation.navigate.NavScreen
import com.techzo.cambiazo.ui.theme.CambiazoTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            CambiazoTheme {
                    NavScreen()
            }
        }
    }
}


@Preview(showBackground = true, widthDp = 393, heightDp = 851)
@Composable
fun AppPreview() {
    CambiazoTheme {
        NavScreen()
    }
}