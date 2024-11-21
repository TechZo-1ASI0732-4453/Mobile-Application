package com.techzo.cambiazo.presentation.articles


import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.HorizontalDivider
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.techzo.cambiazo.common.components.ArticlesOwn
import com.techzo.cambiazo.common.components.DialogApp
import com.techzo.cambiazo.common.components.EmptyStateMessage
import com.techzo.cambiazo.common.components.LoadingMessage
import com.techzo.cambiazo.common.components.MainScaffoldApp
import com.techzo.cambiazo.common.components.TextTitleHeaderApp
import com.techzo.cambiazo.domain.Product
import com.techzo.cambiazo.presentation.exchanges.ExchangeBox

@Composable
fun ArticlesScreen(
    viewModel: ArticlesViewModel = hiltViewModel(),
    bottomBar: Pair<@Composable () -> Unit, () -> Unit>,
    editProduct: (Product?) -> Unit = {},
    onProductClick: (Int, Int) -> Unit,
) {
    val state = viewModel.products.collectAsState().value

    MainScaffoldApp(
        bottomBar = bottomBar,
        paddingCard = PaddingValues(start = 15.dp, end = 15.dp, top = 25.dp),
        contentsHeader = {
            Spacer(modifier = Modifier.height(30.dp))
            TextTitleHeaderApp(text = "Artículos")
            Spacer(modifier = Modifier.height(30.dp))
        }
    ) {
        Box {
            if (state.isLoading) {
                LoadingMessage()
            } else if (state.data.isNullOrEmpty()) {
                EmptyStateMessage(
                    icon = Icons.Default.Info,
                    message = "Sin artículos por ahora",
                    subMessage = "¡Comparte algo que ya no uses!",
                    modifier = Modifier.padding(20.dp)
                )
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                ) {
                    items(state.data!!.chunked(2)) { rowItems ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(Color.White),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            rowItems.forEach {product->
                                ArticlesOwn(
                                    product = product,
                                    Modifier.weight(1f),
                                    iconActions = true,
                                    deleteProduct = { productId ->
                                        viewModel.deleteProduct( productId, product.image)
                                    },
                                    editProduct = {editProduct(it)},
                                    onClick = onProductClick
                                )
                            }
                            if (rowItems.size == 1) {
                                Spacer(
                                    modifier = Modifier
                                        .weight(1f)
                                        .fillMaxWidth()
                                )
                            }
                        }
                    }
                    item { Spacer(modifier = Modifier.height(85.dp)) }
                }
            }

        }
    }
}
