package com.techzo.cambiazo.presentation.articles

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.dp
import com.techzo.cambiazo.common.components.MainScaffoldApp
import com.techzo.cambiazo.common.components.TextTitleHeaderApp

@Composable
fun ArticlesScreen(
    bottomBar : @Composable () -> Unit = {}
){


    MainScaffoldApp(
        bottomBar = bottomBar,
        paddingCard = PaddingValues(start=20.dp,end=20.dp,top=25.dp),
        contentsHeader = {
            TextTitleHeaderApp(text ="Artículos")
        }
    ){
        Text(text = "Contenido de la pantalla de artículos")
    }
}