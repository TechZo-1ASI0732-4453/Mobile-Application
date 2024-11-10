package com.techzo.cambiazo.presentation.profile.favorites

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import com.techzo.cambiazo.data.repository.ProductDetailsRepository
import com.techzo.cambiazo.data.repository.ProductRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import androidx.lifecycle.viewModelScope
import com.techzo.cambiazo.common.Constants
import com.techzo.cambiazo.common.Resource
import com.techzo.cambiazo.common.UIState
import com.techzo.cambiazo.domain.Product
import kotlinx.coroutines.launch
import kotlinx.coroutines.Dispatchers


@HiltViewModel
class FavoritesViewModel @Inject constructor(
    private val productDetailsRepository: ProductDetailsRepository,
    private val productRepository: ProductRepository
) : ViewModel() {

    private val _favoriteProducts = mutableStateOf(UIState<List<Product>>())
    val favoriteProducts: State<UIState<List<Product>>> = _favoriteProducts

    private val _productToRemove = mutableStateOf<Product?>(null)
    val productToRemove: State<Product?> = _productToRemove

    private val _favoriteProductsIds = mutableStateOf<Set<Int>>(emptySet())
    val favoriteProductsIds: State<Set<Int>> = _favoriteProductsIds

    init {
        getFavoriteProductsByUserId()
    }

    fun getFavoriteProductsByUserId() {
        viewModelScope.launch {
            val result = productDetailsRepository.getFavoriteProductByUserId(Constants.user!!.id)
            if (result is Resource.Success) {
                val favoriteProducts = result.data ?: emptyList()
                _favoriteProducts.value = UIState(data = favoriteProducts.map { it.product })
            } else {
                _favoriteProducts.value = UIState(message = "Error al cargar favoritos.")
            }
        }
    }


    fun removeProductFromFavorites(productId: Int) {
        viewModelScope.launch {
            val result = productDetailsRepository.deleteFavoriteProduct(productId)
            if (result is Resource.Success) {
                _favoriteProducts.value = UIState(
                    data = _favoriteProducts.value.data?.filter { it.id != productId }
                )
            } else {
                _favoriteProducts.value = UIState(
                    data = _favoriteProducts.value.data,
                    message = result.message ?: "Error al eliminar de favoritos"
                )
            }
        }
    }

    fun confirmRemoveProduct(product: Product) {
        _productToRemove.value = product
    }

    fun cancelRemoveProduct() {
        _productToRemove.value = null
    }
}
