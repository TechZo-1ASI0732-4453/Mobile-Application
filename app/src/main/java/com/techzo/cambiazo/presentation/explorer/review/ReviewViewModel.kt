package com.techzo.cambiazo.presentation.explorer.review

import android.util.Log
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.techzo.cambiazo.common.Resource
import com.techzo.cambiazo.common.UIState
import com.techzo.cambiazo.data.repository.ProductRepository
import com.techzo.cambiazo.data.repository.ReviewRepository
import com.techzo.cambiazo.data.repository.UserRepository
import com.techzo.cambiazo.domain.Product
import com.techzo.cambiazo.domain.Review
import com.techzo.cambiazo.domain.ReviewAverageUser
import com.techzo.cambiazo.domain.User
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ReviewViewModel @Inject constructor(
    private val reviewRepository: ReviewRepository,
    private val productRepository: ProductRepository,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val _reviewAverageUser = mutableStateOf<ReviewAverageUser?>(null)
    val reviewAverageUser: State<ReviewAverageUser?> = _reviewAverageUser

    private val _reviews = mutableStateOf(UIState<List<Review>>())
    val reviews: State<UIState<List<Review>>> = _reviews

    private val _articles = mutableStateOf(UIState<List<Product>>())
    val articles: State<UIState<List<Product>>> = _articles

    private val _errorMessage = mutableStateOf<String?>(null)
    val errorMessage: State<String?> = _errorMessage

    private val _userId = mutableStateOf<Int?>(null)
    val userId: State<Int?> get() = _userId

    init {
        val userIdString: String? = savedStateHandle["userId"]
        val userId = userIdString?.toIntOrNull()
        if (userId != null) {
            _userId.value = userId
            getReviewAverageByUserId(userId)
        } else {
            _errorMessage.value = "Invalid user ID"
        }
    }

    fun getReviewAverageByUserId(userId: Int) {
        viewModelScope.launch {
            val reviewAverageDeferred =
                async { reviewRepository.getAverageRatingAndReviewsByUserId(userId) }
            val articlesDeferred = async { productRepository.getProductsByUserId(userId) }

            val reviewAverageResult = reviewAverageDeferred.await()
            val articlesResult = articlesDeferred.await()

            when (reviewAverageResult) {
                is Resource.Success -> {
                    _reviewAverageUser.value = reviewAverageResult.data?.first
                    _reviews.value = UIState(data = reviewAverageResult.data?.second)
                    _errorMessage.value = null
                }
                is Resource.Error -> {
                    _errorMessage.value = reviewAverageResult.message
                }
            }

            when (articlesResult) {
                is Resource.Success -> {
                    _articles.value = UIState(data = articlesResult.data)
                }
                is Resource.Error -> {
                    _articles.value = UIState(message = articlesResult.message ?: "Unknown error")
                }
            }
        }
    }

    fun addReview(message: String, rating: Int, state:String, userAuthorId:Int, userReceptorId: Int, exchangeId: Int) {
        viewModelScope.launch {
            val result = reviewRepository.addReview(message, rating, state, userAuthorId, userReceptorId, exchangeId)
            if (result is Resource.Success){
                Log.d("ReviewViewModel", "Review added successfully")
            }else{
                Log.e("ReviewViewModel", "Error adding review: ${result.message}")
            }
        }
    }
}