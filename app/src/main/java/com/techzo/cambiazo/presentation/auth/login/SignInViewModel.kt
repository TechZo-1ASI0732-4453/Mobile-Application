package com.techzo.cambiazo.presentation.auth.login

import android.util.Log
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.techzo.cambiazo.common.Constants
import com.techzo.cambiazo.common.Resource
import com.techzo.cambiazo.common.UIState
import com.techzo.cambiazo.data.repository.AuthRepository
import com.techzo.cambiazo.domain.User
import com.techzo.cambiazo.domain.UserSignIn
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject


@HiltViewModel
class SignInViewModel @Inject constructor(private val authRepository: AuthRepository): ViewModel() {


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

    fun validateUser():Boolean{
        _errorUsername.value = UIState(message = "Usuario requerido", data =_username.value.isEmpty() )
        _errorPassword.value = UIState(message = "Contraseña requerida", data =_password.value.isEmpty() )
        return !_errorUsername.value.data!! && !_errorPassword.value.data!!
    }
    fun signIn() {
        if (!validateUser()) return
        _state.value = UIState(isLoading = true)
        viewModelScope.launch {
            val result = authRepository.signIn(_username.value, _password.value)
            if (result is Resource.Success) {
                _state.value = UIState(data = result.data)
                result.data?.let{
                    Constants.token = it.token
                    Constants.user = it
                }
            } else {
                _state.value = UIState(message = "Datos de usuario incorrectos")
            }
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

}