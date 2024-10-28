package com.techzo.cambiazo.presentation.explorer.offer

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.SavedStateHandle
import com.techzo.cambiazo.domain.Product
import com.techzo.cambiazo.common.Constants
import com.techzo.cambiazo.common.Resource
import com.techzo.cambiazo.data.repository.ExchangeRepository
import com.techzo.cambiazo.data.repository.ProductRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MakeOfferViewModel @Inject constructor(
    private val savedStateHandle: SavedStateHandle,
    private val productRepository: ProductRepository,
    private val exchangeRepository: ExchangeRepository
) : ViewModel() {

    private val _desiredProduct = MutableStateFlow<Product?>(null)
    val desiredProduct: StateFlow<Product?> get() = _desiredProduct

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> get() = _error

    init {
        val desiredProductIdString: String? = savedStateHandle["desiredProductId"]
        val desiredProductId = desiredProductIdString?.toIntOrNull()

        if (desiredProductId != null) {
            viewModelScope.launch {
                try {
                    val desiredProdResource = productRepository.getProductById(desiredProductId)
                    if (desiredProdResource is Resource.Success && desiredProdResource.data != null) {
                        _desiredProduct.value = desiredProdResource.data
                    } else {
                        _error.value = "No se pudo cargar el producto"
                    }
                } catch (e: Exception) {
                    _error.value = e.message
                }
            }
        } else {
            _error.value = "ID de producto inválido"
        }
    }

    suspend fun checkIfExchangeExists(productOwnId: Int): Boolean {
        val desiredProductId = _desiredProduct.value?.id
        return if (desiredProductId != null) {
            val result = exchangeRepository.checkIfExchangeExists(
                userId = Constants.user!!.id,
                productOwnId = productOwnId,
                productChangeId = desiredProductId
            )
            if (result is Resource.Success) {
                result.data ?: false
            } else {
                _error.value = result.message ?: "Error al comprobar la existencia de la oferta"
                false
            }
        } else {
            _error.value = "Producto deseado no cargado"
            false
        }
    }

}