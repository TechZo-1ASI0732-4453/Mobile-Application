package com.techzo.cambiazo.presentation.exchanges

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.techzo.cambiazo.common.components.MainScaffoldApp

@Composable
fun ExchangeScreen(
    bottomBar: @Composable () -> Unit = {},
    viewModel: ExchangeViewModel = hiltViewModel()
) {

    val state = viewModel.state.value


    MainScaffoldApp(
        bottomBar = bottomBar,
        paddingCard = PaddingValues(top = 10.dp),
        contentsHeader = {
            Text(text = "Mis intercambios")
        }
    ){
        LazyColumn{
            items(state.data ?: emptyList()) { exchange ->
                Text(text = exchange.productOwn.name)
                Text(text = exchange.productChange.name)
                Text(text = exchange.userOwn.name)
                Text(text = exchange.userChange.name)
            }
        }
    }
}