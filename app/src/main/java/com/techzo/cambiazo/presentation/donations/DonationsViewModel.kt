package com.techzo.cambiazo.presentation.donations

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.techzo.cambiazo.common.Resource
import com.techzo.cambiazo.common.UIState
import com.techzo.cambiazo.data.remote.donations.OngDto
import com.techzo.cambiazo.data.repository.DonationsRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class DonationsViewModel @Inject constructor(
    private val donationsRepository: DonationsRepository
) : ViewModel() {

    private val _ongs = mutableStateOf(UIState<List<OngDto>>())
    val ongs: State<UIState<List<OngDto>>> get() = _ongs

    private val _searchQuery = mutableStateOf("")
    val searchQuery: State<String> get() = _searchQuery

    init {
        getOngs()
    }

    fun getOngs() {
        viewModelScope.launch {
            _ongs.value = UIState(isLoading = true)
            when (val result = donationsRepository.getAllOngs()) {
                is Resource.Success<*> -> {
                    val data = result.data as? List<OngDto> ?: emptyList()
                    _ongs.value = UIState(data = data)
                }
                is Resource.Error<*> -> {
                    _ongs.value = UIState(message = result.message ?: "Error desconocido")
                }
            }
        }
    }

    fun onSearchQueryChange(query: String) {
        _searchQuery.value = query
    }
}