package com.techzo.cambiazo.presentation.explorer.offer

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.SavedStateHandle
import com.techzo.cambiazo.domain.Product
import com.techzo.cambiazo.data.remote.exchanges.ExchangeRequestDto
import com.techzo.cambiazo.data.repository.ExchangeRepository
import com.techzo.cambiazo.common.Resource
import com.techzo.cambiazo.data.repository.ProductRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ConfirmationOfferViewModel @Inject constructor(
    private val savedStateHandle: SavedStateHandle,
    private val productRepository: ProductRepository,
    private val exchangeRepository: ExchangeRepository
) : ViewModel() {

    private val _desiredProduct = MutableStateFlow<Product?>(null)
    val desiredProduct: StateFlow<Product?> get() = _desiredProduct

    private val _offeredProduct = MutableStateFlow<Product?>(null)
    val offeredProduct: StateFlow<Product?> get() = _offeredProduct

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> get() = _error

    private val _offerSuccess = MutableStateFlow<Boolean>(false)
    val offerSuccess: StateFlow<Boolean> get() = _offerSuccess

    init {
        val desiredProductIdString: String? = savedStateHandle["desiredProductId"]
        val offeredProductIdString: String? = savedStateHandle["offeredProductId"]

        val desiredProductId = desiredProductIdString?.toIntOrNull()
        val offeredProductId = offeredProductIdString?.toIntOrNull()

        if (desiredProductId != null && offeredProductId != null) {
            viewModelScope.launch {
                try {
                    val desiredProdResource = productRepository.getProductById(desiredProductId)
                    val offeredProdResource = productRepository.getProductById(offeredProductId)

                    if (desiredProdResource is Resource.Success && offeredProdResource is Resource.Success) {
                        _desiredProduct.value = desiredProdResource.data
                        _offeredProduct.value = offeredProdResource.data
                    } else {
                        _error.value = "Failed to load products"
                    }
                } catch (e: Exception) {
                    _error.value = e.message
                }
            }
        } else {
            _error.value = "Invalid product IDs"
        }
    }

    fun makeOffer() {
        val desiredProduct = _desiredProduct.value
        val offeredProduct = _offeredProduct.value

        if (desiredProduct != null && offeredProduct != null) {
            viewModelScope.launch {
                try {
                    val newExchangeRequest = ExchangeRequestDto(
                        productOwnId = offeredProduct.id,
                        productChangeId = desiredProduct.id,
                        status = "Pendiente"
                    )

                    val result = exchangeRepository.createExchange(newExchangeRequest)

                    if (result is Resource.Success) {
                        _offerSuccess.value = true
                    } else {
                        _error.value = result.message ?: "Error al enviar la oferta"
                    }
                } catch (e: Exception) {
                    _error.value = e.message ?: "Ocurrió un error inesperado"
                }
            }
        } else {
            _error.value = "Productos no disponibles"
        }
    }
}