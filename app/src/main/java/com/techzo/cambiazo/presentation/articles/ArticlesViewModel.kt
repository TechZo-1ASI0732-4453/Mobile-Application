package com.techzo.cambiazo.presentation.articles

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.techzo.cambiazo.common.Constants
import com.techzo.cambiazo.common.Resource
import com.techzo.cambiazo.common.UIState
import com.techzo.cambiazo.data.repository.ProductRepository
import com.techzo.cambiazo.domain.Product
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ArticlesViewModel  @Inject constructor(
    private val productRepository: ProductRepository
) : ViewModel() {

    private val _products = mutableStateOf(UIState<List<Product>>())
    val products: State<UIState<List<Product>>> = _products

    init {
        fetchProducts()
    }

    private fun fetchProducts() {
        val userId = Constants.user?.id
        if (userId != null) {

            _products.value = UIState(isLoading = true)

            viewModelScope.launch {
                val result = productRepository.getProductsByUserId(userId)

                if (result is Resource.Success) {
                    _products.value = UIState(data = result.data)
                } else {
                    _products.value = UIState(message = result.message ?: "Ocurrió un error")
                }

            }
        }
    }



}