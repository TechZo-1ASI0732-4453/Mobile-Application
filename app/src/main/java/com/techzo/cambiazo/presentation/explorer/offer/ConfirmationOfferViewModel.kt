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
    savedStateHandle: SavedStateHandle,
    private val productRepository: ProductRepository,
    private val exchangeRepository: ExchangeRepository
) : ViewModel() {

    private val _desiredProduct = MutableStateFlow<Product?>(null)
    val desiredProduct: StateFlow<Product?> get() = _desiredProduct

    private val _offeredProduct = MutableStateFlow<Product?>(null)
    val offeredProduct: StateFlow<Product?> get() = _offeredProduct

    private val _offerSuccess = MutableStateFlow(false)
    val offerSuccess: StateFlow<Boolean> get() = _offerSuccess

    private val _offerFailure = MutableStateFlow(false)
    val offerFailure : StateFlow<Boolean> get() = _offerFailure

    private val _createdExchangeId = MutableStateFlow<Int?>(null)
    val createdExchangeId: StateFlow<Int?> get() = _createdExchangeId

    private val _targetUserEmail = MutableStateFlow<String?>(null)
    val targetUserEmail: StateFlow<String?> get() = _targetUserEmail

    private val _targetUserName = MutableStateFlow<String?>(null)
    val targetUserName: StateFlow<String?> get() = _targetUserName

    private val _offeredProductName = MutableStateFlow<String?>(null)
    val offeredProductName: StateFlow<String?> get() = _offeredProductName

    init {
        viewModelScope.launch {
            val desiredProductId: Int? = savedStateHandle.get<String>("desiredProductId")?.toIntOrNull()
            val offeredProductId: Int? = savedStateHandle.get<String>("offeredProductId")?.toIntOrNull()

            if (desiredProductId != null && offeredProductId != null) {
                val desiredProdResource = productRepository.getProductById(desiredProductId)
                val offeredProdResource = productRepository.getProductById(offeredProductId)

                _desiredProduct.value = (desiredProdResource as? Resource.Success)?.data
                _offeredProduct.value = (offeredProdResource as? Resource.Success)?.data
            }
        }
    }

    fun makeOffer() {
        val desiredProduct = _desiredProduct.value
        val offeredProduct = _offeredProduct.value

        viewModelScope.launch {
            val newExchangeRequest = ExchangeRequestDto(
                productOwnId = offeredProduct!!.id,
                productChangeId = desiredProduct!!.id,
                status = "Pendiente"
            )

            val result = exchangeRepository.createExchange(newExchangeRequest)

            if (result is Resource.Success) {
                val exchangeId = result.data?.id

                val exchangeResult = exchangeId?.let { exchangeRepository.getExchangeById(it) }
                if (exchangeResult is Resource.Success) {
                    val exchange = exchangeResult.data

                    val productTitle = exchange?.productChange?.name
                    val targetUserName = exchange?.userChange?.name
                    val targetUserEmail = exchange?.userChange?.username

                    _offeredProductName.value = productTitle
                    _targetUserName.value = targetUserName
                    _targetUserEmail.value = targetUserEmail

                    if (targetUserName != null) {
                        if (targetUserEmail != null) {
                            if (productTitle != null) {
                                sendOfferEmail(
                                    name = targetUserName,
                                    email = targetUserEmail,
                                    itemTitle = productTitle,
                                    status = "Recibida"
                                )
                            }
                        }
                    }
                }

                _offerSuccess.value = true
            } else {
                _offerFailure.value = true
            }
        }
    }
}