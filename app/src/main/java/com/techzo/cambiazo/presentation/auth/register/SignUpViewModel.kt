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

    private val _name            = mutableStateOf("")
    val name: State<String>      get() = _name

    private val _username        = mutableStateOf("")
    val username: State<String>  get() = _username

    private val _phoneNumber     = mutableStateOf("")
    val phoneNumber: State<String> get() = _phoneNumber

    private val _password        = mutableStateOf("")
    val password: State<String>  get() = _password

    private val _repitePassword  = mutableStateOf("")
    val repitePassword: State<String> get() = _repitePassword

    private val _isChecked       = mutableStateOf(false)
    val isChecked: State<Boolean> get() = _isChecked

    private val _captchaToken    = mutableStateOf<String?>(null)
    val captchaToken: State<String?> get() = _captchaToken

    private val _successDialog   = mutableStateOf(false)
    val successDialog: State<Boolean> get() = _successDialog

    private val profilePicture = Constants.DEFAULT_PROFILE_PICTURE
    private val roles          = listOf(Constants.DEFAULT_ROLE)

    fun setCaptchaVerified(verified: Boolean) {
        _captchaToken.value = if (verified) "verified" else null
    }

    fun resetCaptcha() {
        _captchaToken.value = null
    }

    fun signUp(isGoogle: Boolean = false) {
        // 1) Captcha
        if (_captchaToken.value.isNullOrBlank()) {
            _state.value = UIState(message = "Por favor completa la verificación humana")
            return
        }

        _state.value = UIState(isLoading = true)

        if (!isGoogle) {
            // 2) Nombre
            if (_name.value.isBlank()) {
                _state.value = UIState(message = "Por favor ingresa tu nombre")
                return
            }
            // 3) Correo
            if (_username.value.isBlank()) {
                _state.value = UIState(message = "Por favor ingresa tu correo")
                return
            }
            if (!_username.value.matches(Regex("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,6}$"))) {
                _state.value = UIState(message = "Por favor ingresa un correo válido")
                return
            }
            // 4) Teléfono
            if (_phoneNumber.value.isBlank()) {
                _state.value = UIState(message = "Por favor ingresa tu número de teléfono")
                return
            }
            if (!_phoneNumber.value.matches(Regex("^\\d{9}$"))) {
                _state.value = UIState(message = "El teléfono debe tener 9 dígitos numéricos")
                return
            }
            // 5) Contraseña
            if (_password.value.isBlank()) {
                _state.value = UIState(message = "Por favor ingresa tu contraseña")
                return
            }
            if (_password.value.length < 8) {
                _state.value = UIState(message = "La contraseña debe tener al menos 8 caracteres")
                return
            }
            // 6) Confirmar contraseña
            if (_repitePassword.value.isBlank()) {
                _state.value = UIState(message = "Por favor confirma tu contraseña")
                return
            }
            if (_password.value != _repitePassword.value) {
                _state.value = UIState(message = "Las contraseñas no coinciden")
                return
            }
            if (!_isChecked.value) {
                _state.value = UIState(message = "Por favor acepta los términos y condiciones")
                return
            }
        }

        viewModelScope.launch {
            val result = authRepository.signUp(
                username       = _username.value,
                password       = _password.value,
                name           = _name.value,
                phoneNumber    = _phoneNumber.value,
                profilePicture = profilePicture,
                roles          = roles,
                isGoogleAccount= _isChecked.value
            )
            _state.value = if (result is Resource.Success) {
                _successDialog.value = true
                UIState(data = result.data)
            } else {
                UIState(message = result.message ?: "Error desconocido")
            }
        }
    }

    fun onNameChange(value: String)           { _name.value            = value }
    fun onUsernameChange(value: String)       { _username.value        = value }
    fun onPhoneNumberChange(value: String)    { _phoneNumber.value     = value }
    fun onPasswordChange(value: String)       { _password.value        = value }
    fun onRepitePasswordChange(value: String) { _repitePassword.value  = value }
    fun onCheckedChange(value: Boolean)       { _isChecked.value       = value }
    fun hideSuccessDialog()                   { _successDialog.value   = false }
}
