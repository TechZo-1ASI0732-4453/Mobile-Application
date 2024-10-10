package com.techzo.cambiazo.presentation.login

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
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject


@HiltViewModel
class SignInViewModel @Inject constructor(private val authRepository: AuthRepository): ViewModel() {


    private val _state = mutableStateOf(UIState<User>())
    val state: State<UIState<User>> get() = _state

    private val _username = mutableStateOf("")
    val username: State<String> get() = _username

    private val _password = mutableStateOf("")
    val password: State<String> get() = _password

    private val _showPassword = mutableStateOf(false)
    val showPassword: State<Boolean> get() = _showPassword

    fun signIn() {
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
                _state.value = UIState(message =result.message?:"Error")
            }
        }
    }

    fun onUsernameChange(username: String) {
        _username.value = username
    }

    fun onPasswordChange(password: String) {
        _password.value = password
    }

    fun onShowPasswordChange(showPassword: Boolean) {
        _showPassword.value = showPassword
    }
}