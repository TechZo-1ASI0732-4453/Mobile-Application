package com.techzo.cambiazo.presentation.articles

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.techzo.cambiazo.common.Constants
import com.techzo.cambiazo.common.Resource
import com.techzo.cambiazo.common.UIState
import com.techzo.cambiazo.common.deleteImageFromFirebase
import com.techzo.cambiazo.data.repository.ProductRepository
import com.techzo.cambiazo.domain.Product
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ArticlesViewModel @Inject constructor(
    private val productRepository: ProductRepository
) : ViewModel() {

    private val _products = MutableStateFlow(UIState<List<Product>>())
    val products: StateFlow<UIState<List<Product>>> get() = _products

    init {
        fetchProducts()
    }

    private fun fetchProducts() {
        _products.value = UIState(isLoading = true)
        viewModelScope.launch(Dispatchers.IO) {
            val result = productRepository.getProductsByUserId(Constants.user!!.id)
            _products.value = if (result is Resource.Success) {
                UIState(data = result.data)
            } else {
                UIState(message = result.message ?: "Ocurrió un error")
            }
        }
    }

    fun deleteProduct(productId: Int, imageUrl: String) {
        viewModelScope.launch(Dispatchers.IO) {
            val result = productRepository.deleteProduct(productId)
            if (result is Resource.Success) {
                _products.value = UIState(data = _products.value.data?.filter { it.id != productId })
                deleteImageFromFirebase(imageUrl = imageUrl, onSuccess = {}, onFailure = {})
            }
        }
    }
}