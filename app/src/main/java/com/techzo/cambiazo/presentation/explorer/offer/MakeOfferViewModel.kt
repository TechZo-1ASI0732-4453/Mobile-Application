// MakeOfferViewModel.kt
package com.techzo.cambiazo.presentation.explorer.offer

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.SavedStateHandle
import com.techzo.cambiazo.domain.Product
import com.techzo.cambiazo.common.Constants
import com.techzo.cambiazo.common.Resource
import com.techzo.cambiazo.data.repository.ProductRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MakeOfferViewModel @Inject constructor(
    private val savedStateHandle: SavedStateHandle,
    private val productRepository: ProductRepository
) : ViewModel() {

    private val _desiredProduct = MutableStateFlow<Product?>(null)
    val desiredProduct: StateFlow<Product?> get() = _desiredProduct

    private val _userProducts = MutableStateFlow<List<Product>>(emptyList())
    val userProducts: StateFlow<List<Product>> get() = _userProducts

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> get() = _error

    init {
        val desiredProductIdString: String? = savedStateHandle["desiredProductId"]

        val desiredProductId = desiredProductIdString?.toIntOrNull()

        if (desiredProductId != null) {
            viewModelScope.launch {
                try {
                    val desiredProdResource = productRepository.getProductById(desiredProductId)
                    val userProdsResource = productRepository.getProductsByUserId(Constants.user!!.id)
                    if (desiredProdResource is Resource.Success && desiredProdResource.data != null &&
                        userProdsResource is Resource.Success && userProdsResource.data != null) {
                        _desiredProduct.value = desiredProdResource.data
                        _userProducts.value = userProdsResource.data.filter { it.available }
                    } else {
                        _error.value = "Failed to load products"
                    }
                } catch (e: Exception) {
                    _error.value = e.message
                }
            }
        } else {
            _error.value = "Invalid product ID"
        }
    }
}