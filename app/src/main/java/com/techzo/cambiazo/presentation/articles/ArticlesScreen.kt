package com.techzo.cambiazo.presentation.articles

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.techzo.cambiazo.common.components.MainScaffoldApp
import com.techzo.cambiazo.common.components.TextTitleHeaderApp

@Composable
fun ArticlesScreen(
    viewModel: ArticlesViewModel = hiltViewModel(),
    bottomBar : @Composable () -> Unit = {}
){

    val products = viewModel.products.value


    MainScaffoldApp(
        bottomBar = bottomBar,
        paddingCard = PaddingValues(start=20.dp,end=20.dp,top=25.dp),
        contentsHeader = {
            TextTitleHeaderApp(text ="Artículos")
        }
    ){
        Text(text = "Contenido de la pantalla de artículos")

        LazyColumn{
            items(products.data ?: emptyList()){ product ->
                Text(text = product.name)
            }
        }
    }
}