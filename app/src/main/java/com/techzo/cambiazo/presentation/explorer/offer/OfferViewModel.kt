package com.techzo.cambiazo.presentation.explorer.offer

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.techzo.cambiazo.common.Resource
import com.techzo.cambiazo.data.remote.exchanges.ExchangeRequestDto
import com.techzo.cambiazo.data.repository.ExchangeRepository
import com.techzo.cambiazo.domain.Product
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class OfferViewModel @Inject constructor(
    private val repositoryExchange: ExchangeRepository
) : ViewModel() {

    private val _selectedOfferedProduct = mutableStateOf<Product?>(null)
    val selectedOfferedProduct: State<Product?> = _selectedOfferedProduct

    private val _desiredProduct = mutableStateOf<Product?>(null)
    val desiredProduct: State<Product?> = _desiredProduct

    fun selectOfferedProduct(product: Product) {
        _selectedOfferedProduct.value = product
    }

    fun initProducts(desiredProduct: Product, offeredProduct: Product) {
        _desiredProduct.value = desiredProduct
        _selectedOfferedProduct.value = offeredProduct
    }

    fun makeOffer(
        desiredProduct: Product,
        offeredProduct: Product,
        onSuccess: () -> Unit,
        onFailure: (String) -> Unit
    ) {
        viewModelScope.launch {
            try {
                val newExchangeRequest = ExchangeRequestDto(
                    productOwnId = offeredProduct.id,
                    productChangeId = desiredProduct.id,
                    status = "Pendiente"
                )

                val result = repositoryExchange.createExchange(newExchangeRequest)

                if (result is Resource.Success) {
                    onSuccess()
                } else {
                    onFailure(result.message ?: "Error al enviar la oferta")
                }
            } catch (e: Exception) {
                onFailure(e.message ?: "Ocurrió un error inesperado")
            }
        }
    }
}
