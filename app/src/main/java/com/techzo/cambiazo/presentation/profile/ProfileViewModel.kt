package com.techzo.cambiazo.presentation.profile

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.techzo.cambiazo.common.Constants
import com.techzo.cambiazo.common.Resource
import com.techzo.cambiazo.common.UIState
import com.techzo.cambiazo.common.UserPreferences
import com.techzo.cambiazo.data.repository.ReviewRepository
import com.techzo.cambiazo.domain.Review
import com.techzo.cambiazo.data.repository.UserRepository
import com.techzo.cambiazo.domain.ReviewAverageUser
import com.techzo.cambiazo.domain.User
import com.techzo.cambiazo.domain.UserSignIn
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject


@HiltViewModel
class ProfileViewModel@Inject constructor(
    private val reviewRepository: ReviewRepository,
    private val userRepository: UserRepository,
    private val userPreferences: UserPreferences
) : ViewModel() {

    private val _isLoggedOut = mutableStateOf(false)
    val isLoggedOut: State<Boolean> get() = _isLoggedOut

    private val _averageRating = mutableStateOf<Double?>(null)
    val averageRating: State<Double?> get() = _averageRating

    private val _countReviews = mutableStateOf<Int?>(null)
    val countReviews: State<Int?> get() = _countReviews

    private val _state = mutableStateOf(UIState<Pair<ReviewAverageUser, List<Review>>>())
    val state: State<UIState<Pair<ReviewAverageUser, List<Review>>>> get() = _state


    init {
        getReviewData()
    }


    fun getReviewData() {
        _state.value = UIState(isLoading = true)
        viewModelScope.launch {
            val userId = Constants.user?.id
            if (userId != null) {
                val result = reviewRepository.getAverageRatingAndReviewsByUserId(userId)
                if (result is Resource.Success) {
                    _averageRating.value = result.data?.first?.averageRating
                    _countReviews.value = result.data?.first?.countReviews
                    _state.value = UIState(data = result.data)
                } else {
                    _state.value = UIState(message = result.message ?: "Ocurrió un error")
                }
            } else {
                _state.value = UIState(message = "Usuario no encontrado")
            }
        }
    }


    fun onLogout() {
        viewModelScope.launch {
            Constants.user = null
            Constants.token = ""
            Constants.userSubscription = null
            userPreferences.clearSession()
            _isLoggedOut.value = true
        }
    }

    fun deleteAccount() {
        viewModelScope.launch(Dispatchers.IO) {
            val userId = Constants.user!!.id
            val result = userRepository.deleteUser(userId)
            if (result is Resource.Success) {
                Constants.user = null
                Constants.token = ""
                Constants.userSubscription = null
                userPreferences.clearSession()
                _isLoggedOut.value = true
            }
        }
    }

}