package com.techzo.cambiazo.presentation.profile

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.techzo.cambiazo.common.Constants
import com.techzo.cambiazo.common.Resource
import com.techzo.cambiazo.common.UIState
import com.techzo.cambiazo.data.repository.ReviewRepository
import com.techzo.cambiazo.domain.ReviewAverageUser
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject


@HiltViewModel
class ProfileViewModel@Inject constructor(private val reviewRepository: ReviewRepository) : ViewModel() {

    private val _averageRating =mutableStateOf<Double?>(null)
    val averageRating: State<Double?> get() = _averageRating

    private val _countReviews = mutableStateOf<Int?>(null)
    val countReviews: State<Int?> get() = _countReviews

    private val _state = mutableStateOf(UIState<ReviewAverageUser>())
    val state: State<UIState<ReviewAverageUser>> get() = _state


    init {
        getReviewData()
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