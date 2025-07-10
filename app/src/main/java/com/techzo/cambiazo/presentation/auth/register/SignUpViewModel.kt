package com.techzo.cambiazo.presentation.auth.register

import android.content.Context
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
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SignUpViewModel @Inject constructor(
    private val authRepository: AuthRepository,
    @ApplicationContext private val context: Context
) : ViewModel() {

    private val _state = mutableStateOf(UIState<UserSignUp>())
    val state: State<UIState<UserSignUp>> get() = _state

    private val _name = mutableStateOf("")
    val name: State<String> get() = _name

    private val _username = mutableStateOf("")
    val username: State<String> get() = _username

    private val _phoneNumber = mutableStateOf("")
    val phoneNumber: State<String> get() = _phoneNumber

    private val _password = mutableStateOf("")
    val password: State<String> get() = _password

    private val _repitePassword = mutableStateOf("")
    val repitePassword: State<String> get() = _repitePassword

    private val _isChecked = mutableStateOf(false)
    val isChecked: State<Boolean> get() = _isChecked

    private val _captchaToken = mutableStateOf<String?>(null)
    val captchaToken: State<String?> get() = _captchaToken

    private val _successDialog = mutableStateOf(false)
    val successDialog: State<Boolean> get() = _successDialog

    private val _nameError = mutableStateOf<String?>(null)
    val nameError: State<String?> get() = _nameError

    private val _emailError = mutableStateOf<String?>(null)
    val emailError: State<String?> get() = _emailError

    private val _phoneError = mutableStateOf<String?>(null)
    val phoneError: State<String?> get() = _phoneError

    private val _passwordError = mutableStateOf<String?>(null)
    val passwordError: State<String?> get() = _passwordError

    private val _repeatPasswordError = mutableStateOf<String?>(null)
    val repeatPasswordError: State<String?> get() = _repeatPasswordError

    private val profilePicture = Constants.DEFAULT_PROFILE_PICTURE
    private val roles = listOf(Constants.DEFAULT_ROLE)

    fun setCaptchaVerified(verified: Boolean) {
        _captchaToken.value = if (verified) "verified" else null
    }

    fun resetCaptcha() {
        _captchaToken.value = null
    }

    private fun clearErrors() {
        _nameError.value = null
        _emailError.value = null
        _phoneError.value = null
        _passwordError.value = null
        _repeatPasswordError.value = null
    }

    private fun validate(): Boolean {
        clearErrors()
        var valid = true
        if (_name.value.isBlank()) {
            _nameError.value = "Nombre es requerido"
            valid = false
        }
        if (_username.value.isBlank()) {
            _emailError.value = "Correo es requerido"
            valid = false
        } else if (!_username.value.matches(Regex("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,6}$"))) {
            _emailError.value = "Correo inválido"
            valid = false
        }
        if (_phoneNumber.value.isBlank()) {
            _phoneError.value = "Teléfono es requerido"
            valid = false
        } else if (!_phoneNumber.value.matches(Regex("^\\d{9}$"))) {
            _phoneError.value = "Teléfono debe tener 9 dígitos"
            valid = false
        }
        if (_password.value.isBlank()) {
            _passwordError.value = "Contraseña es requerida"
            valid = false
        } else if (_password.value.length < 8) {
            _passwordError.value = "Mínimo 8 caracteres"
            valid = false
        }
        if (_repitePassword.value.isBlank()) {
            _repeatPasswordError.value = "Confirma tu contraseña"
            valid = false
        } else if (_password.value != _repitePassword.value) {
            _repeatPasswordError.value = "No coincide con la contraseña"
            valid = false
        }
        if (!_isChecked.value) {
            _state.value = UIState(message = "Acepta los términos y condiciones")
            valid = false
        }
        if (_captchaToken.value.isNullOrBlank()) {
            _state.value = UIState(message = "Completa la verificación humana")
            valid = false
        }
        return valid
    }

    fun signUp(isGoogle: Boolean = false) {
        if (!validate()) return
        _state.value = UIState(isLoading = true)
        viewModelScope.launch {
            val result = authRepository.signUp(
                username = _username.value,
                password = _password.value,
                name = _name.value,
                phoneNumber = _phoneNumber.value,
                profilePicture = profilePicture,
                roles = roles,
                isGoogleAccount = isGoogle
            )
            _state.value = if (result is Resource.Success) {
                _successDialog.value = true
                UIState(data = result.data)
            } else {
                UIState(message = result.message ?: "Error desconocido")
            }
        }
    }

    fun onNameChange(value: String) { _name.value = value }
    fun onUsernameChange(value: String) { _username.value = value }
    fun onPhoneNumberChange(value: String) { _phoneNumber.value = value }
    fun onPasswordChange(value: String) { _password.value = value }
    fun onRepitePasswordChange(value: String) { _repitePassword.value = value }
    fun onCheckedChange(value: Boolean) { _isChecked.value = value }
    fun hideSuccessDialog() { _successDialog.value = false }
}