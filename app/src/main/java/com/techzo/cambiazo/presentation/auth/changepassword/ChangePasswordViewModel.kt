package com.techzo.cambiazo.presentation.auth.changepassword

import android.util.Log
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.techzo.cambiazo.common.Resource
import com.techzo.cambiazo.common.UIState
import com.techzo.cambiazo.data.remote.auth.NewPassword
import com.techzo.cambiazo.data.repository.UserRepository
import com.techzo.cambiazo.domain.UserUsername
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ChangePasswordViewModel @Inject constructor(
    private val userRepository: UserRepository
) : ViewModel() {

    private val _state = mutableStateOf(UIState<UserUsername>())
    val state: State<UIState<UserUsername>> get() = _state

    private val _emailNotExists = mutableStateOf(false)
    val emailNotExists: State<Boolean> get() = _emailNotExists

    private val _errorEmail = mutableStateOf(UIState<Boolean>(data = false, message = ""))
    val errorEmail: State<UIState<Boolean>> get() = _errorEmail

    private val _isEmailSent = mutableStateOf(false)
    val isEmailSent: State<Boolean> = _isEmailSent

    private val _email = MutableStateFlow("")
    val email: StateFlow<String> get() = _email

    private val _name = mutableStateOf("")
    val name: State<String> get() = _name

    private val _code = mutableStateOf("")
    val code: State<String> get() = _code

    private val _errorCode = mutableStateOf(UIState<Boolean>(data = false, message = ""))
    val errorCode: State<UIState<Boolean>> get() = _errorCode


    fun onEmailChange(email: String) {
        _email.value = email
    }

    fun resetEmailState() {
        _isEmailSent.value = false
    }
    fun resetEmailError() {
        _errorEmail.value = UIState(data = false, message = "")
    }

    fun emailIsEmpty() {
        _errorEmail.value = UIState(data = true, message = "El campo de correo no puede estar vacío")
    }

    private fun generateCode():Int{
        return (1000..9999).random()
    }

    fun sendEmail(email: String) {
        viewModelScope.launch {
            try {
                val response = userRepository.getUserByEmail(email)

                if (response is Resource.Success) {
                    _emailNotExists.value = false
                    if(response.data!!.isGoogleAccount){
                        _errorEmail.value = UIState(data = true, message = "Correo inválido: es una cuenta de Google")
                        _isEmailSent.value = false
                        Log.e("EMAIL_VERIFICATION", "No se puede cambiar la contraseña de una cuenta de Google")
                        return@launch
                    }
                    _errorEmail.value = UIState(data = false, message = "")
                    _code.value = generateCode().toString()
                    _name.value = response.data!!.name

                    sendEmail(_name.value, email, _code.value)
                    _isEmailSent.value = true
                } else {
                    _emailNotExists.value = true
                    _errorEmail.value = UIState(data = true, message = "El correo no está registrado")
                    _isEmailSent.value = false
                    Log.e("EMAIL_VERIFICATION", "El correo no está registrado: ${email}")
                }
            } catch (e: Exception) {
                _emailNotExists.value = true
                _errorEmail.value = UIState(data = true, message = "Error al obtener los datos del usuario o al enviar el correo")
                _isEmailSent.value = false
                Log.e("EMAIL_VERIFICATION", "Error al obtener los datos del usuario o al enviar el correo: ${e.message}")
            }
        }
    }

    fun validateCode(inputCode: String, codeGenerated : String): Boolean {
        if(inputCode.isBlank()){
            _errorCode.value = UIState(data = true, message = "El campo de código no puede estar vacío")
            return false
        }
        if(inputCode!=codeGenerated){
            _errorCode.value = UIState(data = true, message = "Código incorrecto")
            return false
        }
        _errorCode.value = UIState(data = false, message = "")
        return true
    }

    fun changePassword(username: String, password: String) {
        viewModelScope.launch {
            try {
                val newPassword = NewPassword(password)

                val response = userRepository.updateUserPassword(username, newPassword)

                if (response is Resource.Success) {
                    Log.d("PASSWORD_CHANGE", "Contraseña cambiada con éxito")
                } else {
                    Log.e("PASSWORD_CHANGE", "Error al cambiar la contraseña: ${response.message}")
                }
            } catch (e: Exception) {
                Log.e("PASSWORD_CHANGE", "Error al cambiar la contraseña: ${e.message}")
            }
        }
    }


}
