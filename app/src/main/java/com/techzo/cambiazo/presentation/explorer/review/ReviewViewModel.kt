package com.techzo.cambiazo.presentation.explorer.review

import android.util.Log
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
    private val userRepository: UserRepository,
    private val productRepository: ProductRepository
) : ViewModel() {

    var reviewAverageUser = mutableStateOf<ReviewAverageUser?>(null)
        private set

    private val _user = mutableStateOf(UIState<User>())
    val user: State<UIState<User>> = _user

    private val _reviews = mutableStateOf(UIState<List<Review>>())
    val reviews: State<UIState<List<Review>>> = _reviews

    private val _articles = mutableStateOf(UIState<List<Product>>())
    val articles: State<UIState<List<Product>>> = _articles

    var errorMessage = mutableStateOf<String?>(null)
        private set

    // Cache para almacenar los flujos de usuarios por userId
    private val userCache = mutableMapOf<Int, StateFlow<UIState<User>>>()

    fun getReviewAverageByUserId(userId: Int) {
        viewModelScope.launch {
            // Ejecutar todas las llamadas en paralelo
            val reviewAverageDeferred =
                async { reviewRepository.getAverageRatingAndReviewsByUserId(userId) }
            val userDeferred = async { userRepository.getUserById(userId) }
            val reviewsDeferred = async { reviewRepository.getReviewsByUserId(userId) }
            val articlesDeferred = async { productRepository.getProductsByUserId(userId) }

            // Esperar a que todas las llamadas terminen
            val reviewAverageResult = reviewAverageDeferred.await()
            val userResult = userDeferred.await()
            val reviewsResult = reviewsDeferred.await()
            val articlesResult = articlesDeferred.await()

            // Procesar los resultados
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

            when (userResult) {
                is Resource.Success -> {
                    _user.value = UIState(data = userResult.data)
                }

                is Resource.Error -> {
                    _user.value = UIState(message = userResult.message ?: "Unknown error")
                }
            }

            when (reviewsResult) {
                is Resource.Success -> {
                    _reviews.value = UIState(data = reviewsResult.data)
                }

                is Resource.Error -> {
                    _reviews.value = UIState(message = reviewsResult.message ?: "Unknown error")
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

    fun getUserById(userId: Int): StateFlow<UIState<User>> {
        return userCache.getOrPut(userId) {
            flow {
                emit(UIState(isLoading = false)) // Eliminamos el estado de carga
                when (val result = userRepository.getUserById(userId)) {
                    is Resource.Success -> {
                        emit(UIState(data = result.data))
                    }

                    is Resource.Error -> {
                        emit(UIState(message = result.message ?: "Unknown error"))
                    }
                }
            }.stateIn(viewModelScope, SharingStarted.Lazily, UIState(isLoading = false))
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
