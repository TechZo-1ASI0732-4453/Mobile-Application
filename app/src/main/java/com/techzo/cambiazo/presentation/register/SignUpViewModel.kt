package com.techzo.cambiazo.presentation.register

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.techzo.cambiazo.common.Resource
import com.techzo.cambiazo.common.UIState
import com.techzo.cambiazo.data.repository.AuthRepository
import com.techzo.cambiazo.domain.model.User
import com.techzo.cambiazo.domain.model.UserSignUp
import kotlinx.coroutines.launch

class SignUpViewModel(private val authRepository: AuthRepository): ViewModel() {

    private val _state = mutableStateOf(UIState<UserSignUp>())
    val state: State<UIState<UserSignUp>> get() = _state

    private val _username = mutableStateOf("")
    val username: State<String> get() = _username

    private val _password = mutableStateOf("")
    val password: State<String> get() = _password

    private val _repitePassword = mutableStateOf("")
    val repitePassword: State<String> get() = _repitePassword

    private val _name = mutableStateOf("")
    val name: State<String> get() = _name

    private val _phoneNumber = mutableStateOf("")
    val phoneNumber: State<String> get() = _phoneNumber

    private val profilePicture = "https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcR6lqpQj3oAmc1gtyM78oJCbTaDrD7Fj9NRlceOPDZiHA&s"


    private val roles = listOf("ROLE_USER")

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
}