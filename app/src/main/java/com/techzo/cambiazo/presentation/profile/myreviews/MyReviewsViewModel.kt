package com.techzo.cambiazo.presentation.profile.myreviews

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.techzo.cambiazo.common.Constants
import com.techzo.cambiazo.common.Resource
import com.techzo.cambiazo.common.UIState
import com.techzo.cambiazo.data.repository.ReviewRepository
import com.techzo.cambiazo.data.repository.UserRepository
import com.techzo.cambiazo.domain.Review
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MyReviewsViewModel@Inject constructor(private val reviewRepository: ReviewRepository , private val userRepository: UserRepository) : ViewModel() {

    private val _state = mutableStateOf(UIState<List<Review>>())
    val state: State<UIState<List<Review>>> = _state

    private val _allReviews = mutableStateOf<List<Review>>(emptyList())

    init {
        getReviews()
    }

    fun getReviews() {
        _state.value = UIState(isLoading = true)
        viewModelScope.launch {
            val result = reviewRepository.getReviewsByUserId(Constants.user!!.id)

            if (result is Resource.Success) {
                val reviews = result.data?: emptyList()
                _allReviews.value = reviews
                _state.value = UIState(data = reviews, isLoading = false)
            } else {
                _state.value = UIState(message = result.message ?: "Ocurrió un error")
            }
        }
    }

}