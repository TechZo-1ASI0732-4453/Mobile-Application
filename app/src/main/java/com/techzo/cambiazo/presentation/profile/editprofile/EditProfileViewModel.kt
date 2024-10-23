package com.techzo.cambiazo.presentation.profile.editprofile

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.techzo.cambiazo.common.Constants
import com.techzo.cambiazo.common.Resource
import com.techzo.cambiazo.common.UIState
import com.techzo.cambiazo.data.repository.UserRepository
import com.techzo.cambiazo.domain.User
import com.techzo.cambiazo.domain.UserEdit
import com.techzo.cambiazo.domain.UserSignIn
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class EditProfileViewModel @Inject constructor(private val userRepository: UserRepository) : ViewModel() {

    private val _username = mutableStateOf("")
    val username: State<String> get() = _username

    private val _name = mutableStateOf("")
    val name: State<String> get() = _name

    private val _phoneNumber = mutableStateOf("")
    val phoneNumber: State<String> get() = _phoneNumber

    private val _profilePicture = mutableStateOf("")
    val profilePicture: State<String> get() = _profilePicture

    private val _state = mutableStateOf(UIState<User>())
    val state: State<UIState<User>> get() = _state



    fun onProfilePictureChanged(newUrl: String) {
        _profilePicture.value = newUrl
    }

    private val _editState = mutableStateOf(UIState<UserSignIn>())
    val editState: State<UIState<UserSignIn>> get() = _editState

    init {
        getUserData()
    }

    private fun getUserData() {
        _state.value = UIState(isLoading = true)
        viewModelScope.launch {
            val user = userRepository.getUserById(Constants.user!!.id)
            if (user is Resource.Success) {
                _state.value = UIState(data = user.data)
            } else {
                _state.value = UIState(message = user.message ?: "Error")
            }
        }
    }

    fun onUsernameChange(username: String) {
        _username.value = username
    }

    fun onNameChange(name: String) {
        _name.value = name
    }

    fun onPhoneNumberChange(phoneNumber: String) {
        _phoneNumber.value = phoneNumber
    }

    fun onProfilePicture(profilePicture: String) {
        _profilePicture.value = profilePicture
    }


    fun saveProfile() {
        _editState.value = UIState(isLoading = true)
        viewModelScope.launch {
            val updatedUser = UserEdit(
                username = _username.value,
                name = _name.value,
                phoneNumber = _phoneNumber.value,
                profilePicture = _profilePicture.value
            )

            val result = userRepository.updateUserById(Constants.user!!.id, updatedUser)
            if (result is Resource.Success) {
                _editState.value = UIState(data = result.data)
                result.data?.let{
                    Constants.user = it
                }
            } else {
                _editState.value = UIState(message = result.message ?: "Error")
            }
        }
    }

}