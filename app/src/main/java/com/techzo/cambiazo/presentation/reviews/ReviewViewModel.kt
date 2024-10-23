package com.techzo.cambiazo.presentation.reviews

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
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
    private val productRepository: ProductRepository
) : ViewModel() {

    var reviewAverageUser = mutableStateOf<ReviewAverageUser?>(null)
        private set

    private val _reviews = mutableStateOf(UIState<List<Review>>())
    val reviews: State<UIState<List<Review>>> = _reviews

    private val _articles = mutableStateOf(UIState<List<Product>>())
    val articles: State<UIState<List<Product>>> = _articles

    var errorMessage = mutableStateOf<String?>(null)
        private set

    fun getReviewAverageByUserId(userId: Int) {
        viewModelScope.launch {
            val reviewAverageDeferred = async { reviewRepository.getAverageRatingAndReviewsByUserId(userId) }
            val articlesDeferred = async { productRepository.getProductsByUserId(userId) }

            val reviewAverageResult = reviewAverageDeferred.await()
            val articlesResult = articlesDeferred.await()

            // Procesar los resultados de las reseñas
            when (reviewAverageResult) {
                is Resource.Success<Pair<ReviewAverageUser, List<Review>>> -> {
                    reviewAverageUser.value = reviewAverageResult.data?.first
                    _reviews.value = UIState(data = reviewAverageResult.data?.second)
                    errorMessage.value = null
                }

                is Resource.Error -> {
                    errorMessage.value = reviewAverageResult.message
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
}
