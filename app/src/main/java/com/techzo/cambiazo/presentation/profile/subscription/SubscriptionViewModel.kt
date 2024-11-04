package com.techzo.cambiazo.presentation.profile.subscription

import android.util.Log
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.techzo.cambiazo.common.Constants
import com.techzo.cambiazo.common.Resource
import com.techzo.cambiazo.common.UIState
import com.techzo.cambiazo.data.remote.subscriptions.SubscriptionRequestDto
import com.techzo.cambiazo.data.repository.SubscriptionRepository
import com.techzo.cambiazo.domain.Plan
import com.techzo.cambiazo.domain.Subscription
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject


@HiltViewModel
class SubscriptionViewModel @Inject constructor(
    private val subscriptionRepository: SubscriptionRepository
): ViewModel() {

    private val _state = mutableStateOf(UIState<List<Plan>>())
    val state: State<UIState<List<Plan>>> = _state

    private val _newSubscription = mutableStateOf(UIState<Any>())
    val newSubscription: State<UIState<Any>> = _newSubscription

    private val _subscription = mutableStateOf(Constants.userSubscription!!)
    val subscription: State<Subscription> get() = _subscription

    private fun updateSubscription(updatedSubscription: Subscription) {
        _subscription.value = updatedSubscription
        Constants.userSubscription = updatedSubscription
    }

    init {
        getPlans()
    }
    

    private fun getPlans() {
        _state.value = UIState(isLoading = true)
        viewModelScope.launch {
            val result = subscriptionRepository.getPlans()
            if (result is Resource.Success) {
                _state.value = UIState(data = result.data ?: emptyList(), isLoading = false)
            } else {
                _state.value = UIState(message = result.message ?: "Ocurrió un error")
            }
        }
    }


    private fun createSubscription(planId : Int) {
        viewModelScope.launch {
            val subscriptionRequest = SubscriptionRequestDto(state = "Activo", userId = Constants.user!!.id, planId = planId)

            Log.d("SignUpViewModel", "Creando suscripción para el usuario: ${subscriptionRequest.userId}")

            val result = subscriptionRepository.createSubscription(subscriptionRequest)

            if (result is Resource.Success) {
                Log.d("SignUpViewModel", "Suscripción creada exitosamente")
                _newSubscription.value = UIState(data = result.data)
            } else {
                Log.e("SignUpViewModel", "Error al crear la suscripción: ${result.message}")
                _newSubscription.value = UIState(message = result.message ?: "Error")
            }
        }
    }


}