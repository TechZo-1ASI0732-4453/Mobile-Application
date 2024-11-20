package com.techzo.cambiazo.presentation.auth.register

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
class SignUpViewModel @Inject constructor(private val authRepository: AuthRepository) : ViewModel() {

    private val _state = mutableStateOf(UIState<UserSignUp>())
    val state: State<UIState<UserSignUp>> get() = _state

    private val _username = mutableStateOf("")
    val username: State<String> get() = _username

    private val _password = mutableStateOf("")
    val password: State<String> get() = _password

    private val _isChecked = mutableStateOf(false)
    val isChecked: State<Boolean> get() = _isChecked

    private val _repitePassword = mutableStateOf("")
    val repitePassword: State<String> get() = _repitePassword

    private val _showPassword = mutableStateOf(false)
    val showPassword: State<Boolean> get() = _showPassword

    private val _showPasswordRepeat = mutableStateOf(false)
    val showPasswordRepeat: State<Boolean> get() = _showPasswordRepeat

    private val _name = mutableStateOf("")
    val name: State<String> get() = _name

    private val _successDialog = mutableStateOf(false)
    val successDialog: State<Boolean> get() = _successDialog

    private val _phoneNumber = mutableStateOf("")
    val phoneNumber: State<String> get() = _phoneNumber

    private val _isGoogleAccount = mutableStateOf(false) // Nuevo estado para isGoogleAccount
    val isGoogleAccount: State<Boolean> get() = _isGoogleAccount

    private val profilePicture = Constants.DEFAULT_PROFILE_PICTURE
    private val roles = listOf(Constants.DEFAULT_ROLE)

    // Registro normal
    fun signUp(isGoogle: Boolean = false) {
        _state.value = UIState(isLoading = true)

        // Validaciones solo para registros normales
        if (!isGoogle) {
            if (_username.value.isEmpty() ||
                _password.value.isEmpty() ||
                _name.value.isEmpty() ||
                _phoneNumber.value.isEmpty()
            ) {
                _state.value = UIState(message = "Por favor llene todos los campos")
                return
            }
            if (_phoneNumber.value.length != 9) {
                _state.value = UIState(message = "Por favor ingrese un numero de telefono valido")
                return
            }

            if (!_username.value.matches(Regex("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,6}$"))) {
                _state.value = UIState(message = "Por favor ingrese un correo valido")
                return
            }
            if (_password.value != _repitePassword.value) {
                _state.value = UIState(message = "Las contraseñas no coinciden")
                return
            }

            if (!isChecked.value) {
                _state.value = UIState(message = "Por favor acepte los terminos y condiciones")
                return
            }
        }

        viewModelScope.launch {
            val result = authRepository.signUp(
                username = _username.value,
                password = _password.value,
                name = _name.value,
                phoneNumber = _phoneNumber.value,
                profilePicture = profilePicture,
                roles = roles,
                isGoogleAccount = _isGoogleAccount.value
            )
            if (result is Resource.Success) {
                _state.value = UIState(data = result.data)
                _successDialog.value = true
            } else {
                _state.value = UIState(message = result.message ?: "Error")
            }
        }
    }

    // Actualizaciones de campos
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

    fun onCheckedChange(isChecked: Boolean) {
        _isChecked.value = isChecked
    }

    fun hideSuccessDialog() {
        _successDialog.value = false
    }
}
