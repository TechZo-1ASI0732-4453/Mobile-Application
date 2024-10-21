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
import com.techzo.cambiazo.domain.ReviewWithAuthorDetails
import com.techzo.cambiazo.domain.User
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MyReviewsViewModel@Inject constructor(private val reviewRepository: ReviewRepository , private val userRepository: UserRepository) : ViewModel() {

    private val _state = mutableStateOf(UIState<List<ReviewWithAuthorDetails>>())
    val state: State<UIState<List<ReviewWithAuthorDetails>>> = _state

    private val _user = mutableStateOf(UIState<User>())
    val user: State<UIState<User>> = _user

    private val _allReviews = mutableStateOf<List<ReviewWithAuthorDetails>>(emptyList())

    init {
        getReviews()
    }

    fun getReviews() {
        _state.value = UIState(isLoading = true)
        viewModelScope.launch {
            val result = reviewRepository.getReviewsByUserId(Constants.user!!.id)

            if (result is Resource.Success) {
                val reviewsWithAuthorDetails = result.data?.map { review ->
                    val authorDetails = userRepository.getUserById(review.userAuthorId)
                    ReviewWithAuthorDetails(
                        message = review.message,
                        rating = review.rating,
                        state = review.state,
                        exchangeId = review.exchangeId,
                        userAuthorId = review.userAuthorId,
                        userReceptorId = review.userReceptorId,
                        userAuthor = authorDetails.data!!
                    )
                } ?: emptyList()
                _allReviews.value = reviewsWithAuthorDetails
                _state.value = UIState(data = reviewsWithAuthorDetails, isLoading = false)
            } else {
                _state.value = UIState(message = result.message ?: "Ocurrió un error")
            }
        }
    }
}