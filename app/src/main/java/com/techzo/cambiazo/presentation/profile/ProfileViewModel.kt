package com.techzo.cambiazo.presentation.profile

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.techzo.cambiazo.common.Constants
import com.techzo.cambiazo.common.Resource
import com.techzo.cambiazo.common.UIState
import com.techzo.cambiazo.data.repository.ReviewRepository
import com.techzo.cambiazo.data.repository.UserRepository
import com.techzo.cambiazo.domain.ReviewAverageUser
import com.techzo.cambiazo.domain.User
import com.techzo.cambiazo.domain.UserSignIn
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject


@HiltViewModel
class ProfileViewModel@Inject constructor(private val reviewRepository: ReviewRepository, private val userRepository: UserRepository) : ViewModel() {

    private val _averageRating =mutableStateOf<Double?>(null)
    val averageRating: State<Double?> get() = _averageRating

    private val _countReviews = mutableStateOf<Int?>(null)
    val countReviews: State<Int?> get() = _countReviews

    private val _state = mutableStateOf(UIState<ReviewAverageUser>())
    val state: State<UIState<ReviewAverageUser>> get() = _state


    private val _userState = mutableStateOf(UIState<User>())
    val userState: State<UIState<User>> get() = _userState


    private val _user = mutableStateOf<User?>(null)
    val user: State<User?> get() = _user

    private fun loadUserData() {
        viewModelScope.launch {
            val user = userRepository.getUserById(Constants.user!!.id)
            if (user is Resource.Success) {
                _user.value = user.data
            }
        }
    }

    fun refreshUserData() {
        loadUserData()
    }

    init {
        getReviewData()
        getUserData()
    }



    private fun getUserData() {
        _userState.value = UIState(isLoading = true)
        viewModelScope.launch {
            val user = userRepository.getUserById(Constants.user!!.id)
            if (user is Resource.Success) {
                _userState.value = UIState(data = user.data)
            } else {
                _userState.value = UIState(message = user.message ?: "Error")
            }
        }
    }


    fun getReviewData() {
        _state.value = UIState(isLoading = true)
        viewModelScope.launch {
            val result = reviewRepository.getAverageRatingByUserId(Constants.user!!.id)
            if (result is Resource.Success) {
                _averageRating.value = UIState(data = result.data).data?.averageRating
                _countReviews.value = UIState(data = result.data).data?.countReviews
                _state.value = UIState(data = result.data)
            }else{
                _state.value = UIState(message = result.message?:"Ocurrió un error")
            }
        }
    }

    fun onLogout() {
        viewModelScope.launch {
            Constants.user = null
            Constants.token = ""
        }
    }
}