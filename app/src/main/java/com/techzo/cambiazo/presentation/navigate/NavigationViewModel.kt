package com.techzo.cambiazo.presentation.navigate

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class NavigationViewModel @Inject constructor() : ViewModel() {
    private val _redirectToSubscription = mutableStateOf(false)
    val redirectToSubscription: State<Boolean> = _redirectToSubscription

    fun triggerRedirectToSubscription() {
        _redirectToSubscription.value = true
    }

    fun resetRedirect() {
        _redirectToSubscription.value = false
    }
}
