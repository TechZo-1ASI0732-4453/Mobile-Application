package com.techzo.cambiazo.presentation.exchanges

import android.util.Log
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.techzo.cambiazo.common.Constants
import com.techzo.cambiazo.common.Resource
import com.techzo.cambiazo.common.UIState
import com.techzo.cambiazo.data.repository.ExchangeRepository
import com.techzo.cambiazo.domain.Exchange
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject



@HiltViewModel
class ExchangeViewModel @Inject constructor(private val exchangeRepository: ExchangeRepository) : ViewModel() {

    private val _exchangesSend = mutableStateOf(UIState<List<Exchange>>())

    private val _exchangesReceived = mutableStateOf(UIState<List<Exchange>>())

    private val _state = mutableStateOf(UIState<List<Exchange>>())
    val state: State<UIState<List<Exchange>>> get() = _state

    init{
        getExchangesByUserOwnId()
        getExchangesByUserChangeId()
    }

    fun getExchangesByUserOwnId() {
        _state.value = UIState(isLoading = true)
        viewModelScope.launch {
            val result = exchangeRepository.getExchangesByUserOwnId(Constants.user!!.id)
            Log.d("id", Constants.user?.id.toString())
            if(result is Resource.Success){
                _exchangesSend.value = UIState(data = result.data)
                _state.value = UIState(data = result.data)
            }else{
                _state.value = UIState(message = result.message?:"Ocurrió un error")
            }
            Log.d("ExchangeViewModel", "getExchangesByUserOwnId: ${result.data}")
        }
    }

    fun getExchangesByUserChangeId() {
        _state.value = UIState(isLoading = true)
        viewModelScope.launch {
            val result = exchangeRepository.getExchangesByUserChangeId(Constants.user!!.id)
            if(result is Resource.Success){
                _exchangesReceived.value = UIState(data = result.data)
                _state.value = UIState(data = result.data)
            }else{
                _state.value = UIState(message = result.message?:"Ocurrió un error")
            }
        }
    }

}