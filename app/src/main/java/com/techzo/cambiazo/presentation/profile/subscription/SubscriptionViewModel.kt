package com.techzo.cambiazo.presentation.profile.subscription

import android.util.Log
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.techzo.cambiazo.common.Constants
import com.techzo.cambiazo.common.Resource
import com.techzo.cambiazo.common.UIState
import com.techzo.cambiazo.data.remote.subscriptions.SubscriptionRequestDto
import com.techzo.cambiazo.data.repository.SubscriptionRepository
import com.techzo.cambiazo.domain.Plan
import com.techzo.cambiazo.domain.Subscription
import com.techzo.cambiazo.domain.SubscriptionResponse
import com.techzo.cambiazo.domain.UserEdit
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject


@HiltViewModel
class SubscriptionViewModel @Inject constructor(
    private val savedStateHandle: SavedStateHandle,
    private val subscriptionRepository: SubscriptionRepository
): ViewModel() {

    private val _state = mutableStateOf(UIState<List<Plan>>())
    val state: State<UIState<List<Plan>>> = _state

    private val _newSubscription = mutableStateOf(UIState<SubscriptionResponse>())
    val newSubscription: State<UIState<SubscriptionResponse>> = _newSubscription


    private val _subscription = mutableStateOf(Constants.userSubscription!!)
    val subscription: State<Subscription> get() = _subscription


    private fun updateSubscription(updatedSubscription: Subscription) {
        _subscription.value = updatedSubscription
        Constants.userSubscription = updatedSubscription
    }

    private val _selectedPlan = mutableStateOf<Int?>(null)
    val selectedPlan: State<Int?> get() = _selectedPlan

    init {
        getPlans()
        val planIdString: String? = savedStateHandle["planId"]
        val planId = planIdString?.toIntOrNull()
        if (planId != null) {
            _selectedPlan.value = planId
        }
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


    fun createSubscription(planId: Int) {
        viewModelScope.launch {
            val subscriptionRequest = SubscriptionRequestDto(state = "Activo", userId = Constants.user!!.id, planId = planId)

            val result = subscriptionRepository.createSubscription(subscriptionRequest)

            if (result is Resource.Success) {

                val newPlan = state.value.data?.find { it.id == planId }

                val newSubscription = newPlan?.let {
                    Subscription(
                        id = result.data?.id ?: 0,
                        startDate = result.data?.startDate ?: "",
                        endDate = result.data?.endDate ?: "",
                        state = result.data?.state ?: "",
                        userId = result.data?.userId ?: 0,
                        plan = it,
                    )
                }

                if (newSubscription != null) {
                    updateSubscription(newSubscription)
                }
                _newSubscription.value = UIState(data = result.data)
            } else {
                _newSubscription.value = UIState(message = result.message ?: "Error al crear la suscripción")
            }
        }
    }

    fun cancelSubscription() {
        createSubscription(1)
    }



}


