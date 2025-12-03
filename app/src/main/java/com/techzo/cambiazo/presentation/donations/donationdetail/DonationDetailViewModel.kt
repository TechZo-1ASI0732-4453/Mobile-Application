package com.techzo.cambiazo.presentation.donations.donationdetail

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.techzo.cambiazo.common.Resource
import com.techzo.cambiazo.common.UIState
import com.techzo.cambiazo.data.repository.DonationsRepository
import com.techzo.cambiazo.domain.OngDetail
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class DonationDetailViewModel @Inject constructor(
    private val savedStateHandle: SavedStateHandle,
    private val repository: DonationsRepository,
) : ViewModel() {


    private val _ong = mutableStateOf(UIState<OngDetail>())
    val ong: State<UIState<OngDetail>> = _ong

    init {
        val ongIdString: String? = savedStateHandle["ongId"]
        val ongId = ongIdString?.toIntOrNull()


        if (ongId != null) {
            loadOngDetail(ongId)
        } else {
            _ong.value = UIState(message = "Invalid product ID or user ID")
        }
    }


    private fun loadOngDetail(ongId: Int){
        _ong.value = UIState(isLoading = true)
        viewModelScope.launch {
            try {
                val ongDeferred = async { repository.getOngById(ongId) }

                val ongResult = ongDeferred.await()

                if (ongResult is Resource.Success && ongResult.data == null) {
                    _ong.value = UIState(message = "No tienes productos")
                } else {
                    when (ongResult) {
                        is Resource.Success -> _ong.value = UIState(data = ongResult.data)
                        is Resource.Error -> _ong.value = UIState(message = ongResult.message ?: "Error al cargar detalles del producto")
                    }
                }

            } catch (e: Exception) {
                _ong.value = UIState(message = "Error inesperado: ${e.message}")

            }
        }

    }

}