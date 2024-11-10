package com.techzo.cambiazo.presentation.auth.changepassword

import android.util.Log
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.techzo.cambiazo.common.Resource
import com.techzo.cambiazo.common.UIState
import com.techzo.cambiazo.data.repository.UserRepository
import com.techzo.cambiazo.domain.UserUsername
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ChangePasswordViewModel @Inject constructor(
    private val userRepository: UserRepository
) : ViewModel() {


    private val _state = mutableStateOf(UIState<UserUsername>())
    val state: State<UIState<UserUsername>> get() = _state

    private val emailExists = mutableStateOf(false)

    private val _email = mutableStateOf("")
    val email: State<String> get() = _email

    private val _name = mutableStateOf("")
    val name: State<String> get() = _name

    private val _code = mutableStateOf("")
    val code: State<String> get() = _code

    fun onEmailChange(email: String) {
        _email.value = email
    }

    private fun generateCode():Int{
        return (1000..9999).random()
    }

    fun sendEmail() {
        viewModelScope.launch {
            val response = userRepository.getUserByEmail(email.value)
            if(response is Resource.Success){
                emailExists.value = true
                _code.value = generateCode().toString()
                _name.value = response.data!!.name
                Log.d("CODE", _code.value)
                Log.d("EMAIL", email.value)
                Log.d("RESPONSE", response.data.toString())
            }else{
                emailExists.value = false
            }

        }
    }
}
