package com.techzo.cambiazo.presentation.profile.favorites

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import com.techzo.cambiazo.data.repository.ProductDetailsRepository
import com.techzo.cambiazo.data.repository.ProductRepository
import com.techzo.cambiazo.domain.FavoriteProduct
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import androidx.lifecycle.viewModelScope
import com.techzo.cambiazo.common.Constants
import com.techzo.cambiazo.common.Resource
import com.techzo.cambiazo.common.UIState
import com.techzo.cambiazo.domain.Product
import kotlinx.coroutines.launch
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll

@HiltViewModel
class FavoritesViewModel @Inject constructor(
    private val productDetailsRepository: ProductDetailsRepository,
    private val productRepository: ProductRepository
) : ViewModel() {

    private val _favoriteProducts = mutableStateOf(UIState<List<FavoriteProduct>>())
    val favoriteProducts: State<UIState<List<FavoriteProduct>>> = _favoriteProducts

    private val _allFavoriteProducts = mutableStateOf(UIState<List<Product>>())
    val allFavoriteProducts: State<UIState<List<Product>>> = _allFavoriteProducts

    private val _productToRemove = mutableStateOf<Product?>(null)
    val productToRemove: State<Product?> = _productToRemove


    init {
        getFavoriteProductsByUserId()
    }

    fun getFavoriteProductsByUserId() {
        _favoriteProducts.value = UIState(isLoading = true)
        viewModelScope.launch {
            val result = productDetailsRepository.getFavoriteProductByUserId(Constants.user!!.id)

            if (result is Resource.Success) {
                _favoriteProducts.value = UIState(data = result.data, isLoading = false)
                result.data?.let { fetchProducts(it) }
            } else {
                _favoriteProducts.value = UIState(message = result.message ?: "Ocurrió un error")
            }
        }
    }

    private fun fetchProducts(favoriteProducts: List<FavoriteProduct>) {
        _allFavoriteProducts.value = UIState(isLoading = true)
        viewModelScope.launch {
            val products = favoriteProducts.map { favoriteProduct ->
                async(Dispatchers.IO) {
                    val productResponse = productRepository.getProductById(favoriteProduct.productId)
                    if (productResponse is Resource.Success) {
                        productResponse.data
                    } else {
                        null
                    }
                }
            }.awaitAll().filterNotNull()

            _allFavoriteProducts.value = UIState(data = products, isLoading = false)
        }
    }

    fun removeProductFromFavorites(productId: Int) {
        viewModelScope.launch {
            val result = productDetailsRepository.deleteFavoriteProduct(productId)
            if (result is Resource.Success) {
                _allFavoriteProducts.value = UIState(
                    data = _allFavoriteProducts.value.data?.filter { it.id != productId },
                    isLoading = false
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
