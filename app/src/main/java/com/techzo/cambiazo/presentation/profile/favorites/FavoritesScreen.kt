package com.techzo.cambiazo.presentation.profile.favorites

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.techzo.cambiazo.common.components.ButtonIconHeaderApp
import com.techzo.cambiazo.common.components.DialogApp
import com.techzo.cambiazo.common.components.MainScaffoldApp
import com.techzo.cambiazo.common.components.TextTitleHeaderApp
import com.techzo.cambiazo.presentation.explorer.Products

@Composable
fun FavoritesScreen(
    back: () -> Unit = {},
    onProductClick: (String, String) -> Unit,
    favoritesViewModel: FavoritesViewModel = hiltViewModel(),
) {
    val favoriteProductsState = favoritesViewModel.allFavoriteProducts.value
    val productToRemove = favoritesViewModel.productToRemove.value

    LaunchedEffect(Unit) {
        favoritesViewModel.getFavoriteProductsByUserId()
    }

    MainScaffoldApp(
        paddingCard = PaddingValues(top = 15.dp),
        contentsHeader = {
            Column(
                Modifier
                    .padding(bottom = 30.dp)
                    .fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                ButtonIconHeaderApp(Icons.Filled.ArrowBack, onClick = { back() })
                TextTitleHeaderApp("Favoritos")
            }
        },
        content = {
            Column(modifier = Modifier.padding(horizontal = 0.dp)) {
                LazyColumn {
                    items(favoriteProductsState.data ?: emptyList()) { product ->
                        Products(
                            product = product,
                            icon = Icons.Filled.Favorite,
                            onClickIcon = { favoritesViewModel.confirmRemoveProduct(product) },
                            onProductClick = onProductClick
                        )
                    }
                }
            }
        }
    )

    productToRemove?.let { product ->
        DialogApp(
            message = "Confirmación",
            description = "¿Está seguro de que desea eliminar este producto de sus favoritos?",
            labelButton1 = "Aceptar",
            labelButton2 = "Cancelar",
            onDismissRequest = { favoritesViewModel.cancelRemoveProduct() },
            onClickButton1 = {
                favoritesViewModel.removeProductFromFavorites(product.id)
                favoritesViewModel.cancelRemoveProduct()
            },
            onClickButton2 = { favoritesViewModel.cancelRemoveProduct() }
        )
    }
}
