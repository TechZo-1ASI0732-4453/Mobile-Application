package com.techzo.cambiazo.presentation.profile.subscription

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.techzo.cambiazo.data.remote.paypal.PaypalRemoteDataSource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import java.util.Locale

@HiltViewModel
class PaypalViewModel @Inject constructor(
    private val paypal: PaypalRemoteDataSource
) : ViewModel() {

    private val _orderId = MutableStateFlow<String?>(null)
    val orderId: StateFlow<String?> = _orderId

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error

    fun createOrder(amount: Double) {
        val price = String.format(Locale.US, "%.2f", amount)
        viewModelScope.launch(Dispatchers.IO) {
            runCatching { paypal.createPayPalOrder(price) }
                .onSuccess { _orderId.value = it }
                .onFailure { _error.value = it.message }
        }
    }
}