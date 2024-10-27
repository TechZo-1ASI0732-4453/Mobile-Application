package com.techzo.cambiazo.presentation.explorer.productdetails

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.techzo.cambiazo.common.Resource
import com.techzo.cambiazo.common.UIState
import com.techzo.cambiazo.data.repository.ProductDetailsRepository
import com.techzo.cambiazo.data.repository.ProductRepository
import com.techzo.cambiazo.data.repository.ReviewRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject
import com.techzo.cambiazo.domain.Product
import com.techzo.cambiazo.domain.Review
import kotlinx.coroutines.async

@HiltViewModel
class ProductDetailsViewModel @Inject constructor(
    private val repository: ProductDetailsRepository,
    private val productRepository: ProductRepository,
    private val reviewRepository: ReviewRepository
) : ViewModel() {

    private val _product = mutableStateOf(UIState<Product>())
    val product: State<UIState<Product>> = _product

    private val _reviews = mutableStateOf(UIState<List<Review>>())
    val reviews: State<UIState<List<Review>>> = _reviews

    private val _isFavorite = mutableStateOf(UIState<Boolean>(data = false))
    val isFavorite: State<UIState<Boolean>> = _isFavorite

    private val _averageRating = mutableStateOf<Double?>(null)
    val averageRating: State<Double?> get() = _averageRating

    private val _countReviews = mutableStateOf<Int?>(null)
    val countReviews: State<Int?> get() = _countReviews

    fun loadProductDetails(productId: Int, userId: Int) {
        _product.value = UIState(isLoading = true)
        _reviews.value = UIState(isLoading = true)
        _isFavorite.value = UIState(isLoading = true)
        _averageRating.value = null
        _countReviews.value = null

        viewModelScope.launch {
            try {
                val productDeferred = async { productRepository.getProductById(productId) }
                val isFavoriteDeferred = async { repository.isProductFavorite(productId) }
                val reviewsAndRatingDeferred = async { reviewRepository.getAverageRatingAndReviewsByUserId(userId) }

                val productResult = productDeferred.await()
                val isFavoriteResult = isFavoriteDeferred.await()
                val reviewsAndRatingResult = reviewsAndRatingDeferred.await()

                when (productResult) {
                    is Resource.Success -> _product.value = UIState(data = productResult.data)
                    is Resource.Error -> _product.value = UIState(message = productResult.message ?: "Error al cargar detalles del producto")
                }

                when (isFavoriteResult) {
                    is Resource.Success -> _isFavorite.value = UIState(data = isFavoriteResult.data ?: false)
                    is Resource.Error -> _isFavorite.value = UIState(message = "Error al verificar si es favorito")
                }

                when (reviewsAndRatingResult) {
                    is Resource.Success -> {
                        val (averageRatingUser, reviewsList) = reviewsAndRatingResult.data!!
                        _averageRating.value = averageRatingUser.averageRating
                        _countReviews.value = averageRatingUser.countReviews
                        _reviews.value = UIState(data = reviewsList)
                    }
                    is Resource.Error -> {
                        _averageRating.value = null
                        _countReviews.value = null
                        _reviews.value = UIState(message = reviewsAndRatingResult.message ?: "Error al cargar reseñas")
                    }
                }
            } catch (e: Exception) {
                _product.value = UIState(message = "Error inesperado: ${e.message}")
                _isFavorite.value = UIState(data = false)
                _reviews.value = UIState(message = "Error inesperado: ${e.message}")
                _averageRating.value = null
                _countReviews.value = null
            }
        }
    }

    fun addProductToFavorites(productId: Int) {
        viewModelScope.launch {
            val result = repository.addFavoriteProduct(productId)
            if (result is Resource.Success) {
                _isFavorite.value = UIState(data = true)
            } else {
                _isFavorite.value = UIState(
                    data = false,
                    message = result.message ?: "Error al agregar a favoritos"
                )
            }
        }
    }

    fun removeProductFromFavorites(productId: Int) {
        viewModelScope.launch {
            val result = repository.deleteFavoriteProduct(productId)
            if (result is Resource.Success) {
                _isFavorite.value = UIState(data = false)
            } else {
                _isFavorite.value = UIState(
                    data = true,
                    message = result.message ?: "Error al eliminar de favoritos"
                )
            }
        }
    }

    fun toggleFavoriteStatus(productId: Int, isCurrentlyFavorite: Boolean) {
        if (isCurrentlyFavorite) {
            removeProductFromFavorites(productId)
        } else {
            addProductToFavorites(productId)
        }
    }
}
