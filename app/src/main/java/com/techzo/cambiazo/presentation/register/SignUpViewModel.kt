package com.techzo.cambiazo.presentation.register

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.techzo.cambiazo.common.Constants
import com.techzo.cambiazo.common.Resource
import com.techzo.cambiazo.common.UIState
import com.techzo.cambiazo.data.repository.AuthRepository
import com.techzo.cambiazo.domain.UserSignUp
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject


@HiltViewModel
class SignUpViewModel @Inject constructor (private val authRepository: AuthRepository): ViewModel() {

    private val _state = mutableStateOf(UIState<UserSignUp>())
    val state: State<UIState<UserSignUp>> get() = _state

    private val _username = mutableStateOf("")
    val username: State<String> get() = _username

    private val _password = mutableStateOf("")
    val password: State<String> get() = _password

    private val _repitePassword = mutableStateOf("")
    val repitePassword: State<String> get() = _repitePassword

    private val _showPassword = mutableStateOf(false)
    val showPassword: State<Boolean> get() = _showPassword

    private val _showPasswordRepeat = mutableStateOf(false)
    val showPasswordRepeat: State<Boolean> get() = _showPasswordRepeat

    private val _name = mutableStateOf("")
    val name: State<String> get() = _name

    private val _phoneNumber = mutableStateOf("")
    val phoneNumber: State<String> get() = _phoneNumber

    private val profilePicture = Constants.DEFAULT_PROFILE_PICTURE

    private val roles = listOf(Constants.DEFAULT_ROLE)

    fun signUp() {
        _state.value = UIState(isLoading = true)
        if(_password.value != _repitePassword.value){
            _state.value = UIState(message = "Las contraseñas no coinciden")
            return
        }
        if(_username.value.isEmpty() || _password.value.isEmpty() || _name.value.isEmpty() || _phoneNumber.value.isEmpty()){
            _state.value = UIState(message = "Por favor llene todos los campos")
            return
        }
        viewModelScope.launch {
            val result = authRepository.signUp(_username.value, _password.value, _name.value, _phoneNumber.value, profilePicture, roles)
            if (result is Resource.Success) {
                _state.value = UIState(data = result.data)
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

    fun onNameChange(name: String) {
        _name.value = name
    }

    fun onPhoneNumberChange(phoneNumber: String) {
        _phoneNumber.value = phoneNumber
    }


    fun onRepitePasswordChange(repitePassword: String) {
        _repitePassword.value = repitePassword
    }

    fun onShowPasswordChange(showPassword: Boolean) {
        _showPassword.value = showPassword
    }

    fun onShowPasswordRepeatChange(showPasswordRepeat: Boolean) {
        _showPasswordRepeat.value = showPasswordRepeat
    }
}