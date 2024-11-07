package com.techzo.cambiazo.presentation.auth.login

import android.util.Log
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.techzo.cambiazo.common.Constants
import com.techzo.cambiazo.common.Resource
import com.techzo.cambiazo.common.UIState
import com.techzo.cambiazo.common.UserPreferences
import com.techzo.cambiazo.data.repository.AuthRepository
import com.techzo.cambiazo.data.repository.SubscriptionRepository
import com.techzo.cambiazo.domain.Subscription
import com.techzo.cambiazo.domain.User
import com.techzo.cambiazo.domain.UserSignIn
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import java.util.Date
import javax.inject.Inject


@HiltViewModel
class SignInViewModel @Inject constructor(
    private val authRepository: AuthRepository,
    private val userPreferences: UserPreferences,
    private val subscriptionRepository: SubscriptionRepository
): ViewModel() {


    private val _state = mutableStateOf(UIState<UserSignIn>())
    val state: State<UIState<UserSignIn>> get() = _state

    private val _username = mutableStateOf("")
    val username: State<String> get() = _username

    private val _password = mutableStateOf("")
    val password: State<String> get() = _password

    private val _errorUsername =  mutableStateOf(UIState<Boolean>(data = false))
    val errorUsername: State<UIState<Boolean>> get() = _errorUsername

    private val _errorPassword =  mutableStateOf(UIState<Boolean>(data = false))
    val errorPassword: State<UIState<Boolean>> get() = _errorPassword

    private val _isChecked = mutableStateOf(false)
    val isChecked: State<Boolean> get() = _isChecked


    private val _subscription = mutableStateOf(UIState<Subscription>())
    val subscription: State<UIState<Subscription>> get() = _subscription



    fun validateUser():Boolean{
        _errorUsername.value = UIState(message = "Usuario requerido", data =_username.value.isEmpty() )
        _errorPassword.value = UIState(message = "Contraseña requerida", data =_password.value.isEmpty() )
        return !_errorUsername.value.data!! && !_errorPassword.value.data!!
    }

    init{
        viewModelScope.launch {
            _state.value = UIState(isLoading = true) // Indicar carga inicial
            val token = userPreferences.getToken.first()
            if (token != null) {
                Constants.token = token
                Constants.user = UserSignIn(
                    id = userPreferences.getId.first() ?: 0,
                    username = userPreferences.getUsername.first() ?: "",
                    name = userPreferences.getName.first() ?: "",
                    phoneNumber = userPreferences.getPhoneNumber.first() ?: "",
                    profilePicture = userPreferences.getProfilePicture.first() ?: "",
                    token = token
                )
                Constants.userSubscription = getUserSubscription(userPreferences.getId.first() ?: 0)
                _state.value = UIState(data = Constants.user)
            } else {
                _state.value = UIState() // Usuario no autenticado
            }
        }
    }


    fun signIn() {
        if (!validateUser()) return
        _state.value = UIState(isLoading = true)
        viewModelScope.launch {
            val result = authRepository.signIn(_username.value, _password.value)

            if (result is Resource.Success) {
                _state.value = UIState(data = result.data)
                result.data?.let{
                    if(_isChecked.value){
                        userPreferences.saveUserSession(it.id, it.username, it.name, it.phoneNumber, it.profilePicture, it.token)
                    }
                    Constants.token = it.token
                    Constants.user = it
                    Constants.userSubscription = getUserSubscription(it.id)
                }
            } else {
                _state.value = UIState(message = "Datos de usuario incorrectos")
            }
        }
    }

    private suspend fun getUserSubscription(userId: Int): Subscription? {
        _subscription.value = UIState(isLoading = true)
        val result = subscriptionRepository.getSubscriptionByUserId(userId)
        return if (result is Resource.Success) {
            _subscription.value = UIState(data = result.data)
            result.data
        } else {
            _subscription.value = UIState(message = "Datos del usuario incorrectos")
            null
        }
    }

    fun onUsernameChange(username: String) {
        _errorUsername.value = UIState(data = false)
        _state.value = UIState()
        _username.value = username
    }

    fun onPasswordChange(password: String) {
        _errorPassword.value = UIState(data = false)
        _state.value = UIState()
        _password.value = password
    }

    fun onCheckedChange(checked: Boolean) {
        _isChecked.value = checked
    }

}