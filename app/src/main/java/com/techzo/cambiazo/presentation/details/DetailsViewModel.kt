package com.techzo.cambiazo.presentation.details

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.techzo.cambiazo.common.Constants
import com.techzo.cambiazo.common.Resource
import com.techzo.cambiazo.common.UIState
import com.techzo.cambiazo.data.repository.DetailsRepository
import com.techzo.cambiazo.domain.*
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class DetailsViewModel @Inject constructor(
    private val repository: DetailsRepository
) : ViewModel() {

    private val _product = mutableStateOf(UIState<Product>())
    val product: State<UIState<Product>> = _product

    private val _user = mutableStateOf(UIState<User>())
    val user: State<UIState<User>> = _user

    private val _reviews = mutableStateOf(UIState<List<Review>>())
    val reviews: State<UIState<List<Review>>> = _reviews

    private val _productCategory = mutableStateOf(UIState<ProductCategory>())
    val productCategory: State<UIState<ProductCategory>> = _productCategory

    private val _district = mutableStateOf(UIState<District>())
    val district: State<UIState<District>> = _district

    private val _department = mutableStateOf(UIState<Department>())
    val department: State<UIState<Department>> = _department

    private val _isFavorite = mutableStateOf(UIState<Boolean>(data = false))
    val isFavorite: State<UIState<Boolean>> = _isFavorite

    private var currentFavoriteProductId: Int? = null

    fun loadProductDetails(productId: Int, userId: Int) {
        _product.value = UIState(isLoading = true)
        _user.value = UIState(isLoading = true)
        _reviews.value = UIState(isLoading = true)
        _productCategory.value = UIState(isLoading = true)
        _district.value = UIState(isLoading = true)
        _department.value = UIState(isLoading = true)
        _isFavorite.value = UIState(isLoading = true)

        viewModelScope.launch {
            val productDeferred = async { repository.getProductById(productId) }
            val userDeferred = async { repository.getUserById(userId) }
            val reviewsDeferred = async { repository.getReviewsByUserId(userId) }
            val isFavoriteDeferred = async { repository.isProductFavorite(userId, productId) }

            val productResult = productDeferred.await()
            val userResult = userDeferred.await()
            val reviewsResult = reviewsDeferred.await()
            val isFavoriteResult = isFavoriteDeferred.await()

            if (productResult is Resource.Success) {
                _product.value = UIState(data = productResult.data)

                val categoryDeferred = async { repository.getProductCategoryById(productResult.data!!.productCategoryId) }
                val districtDeferred = async { repository.getDistrictById(productResult.data!!.districtId) }

                val categoryResult = categoryDeferred.await()
                val districtResult = districtDeferred.await()

                _productCategory.value = UIState(data = categoryResult.data)
                _district.value = UIState(data = districtResult.data)

                if (districtResult is Resource.Success) {
                    _department.value = repository.getDepartmentById(districtResult.data!!.departmentId).let {
                        UIState(data = it.data)
                    }
                }
            } else {
                _product.value = UIState(message = productResult.message ?: "Error al cargar el producto")
            }

            _user.value = UIState(data = userResult.data)
            _reviews.value = UIState(data = reviewsResult.data)
            _isFavorite.value = UIState(data = isFavoriteResult.data)

            if (isFavoriteResult is Resource.Success && isFavoriteResult.data == true) {
                currentFavoriteProductId = productId
            }
        }
    }

    fun addProductToFavorites(productId: Int) {
        viewModelScope.launch {
            val result = repository.addFavoriteProduct(Constants.user!!.id, productId)
            if (result is Resource.Success) {
                _isFavorite.value = UIState(data = true)
                currentFavoriteProductId = result.data?.id
            }
        }
    }

    fun removeProductFromFavorites() {
        viewModelScope.launch {
            currentFavoriteProductId?.let { productId ->
                val result = repository.removeFavoriteProduct(productId)
                if (result is Resource.Success) {
                    _isFavorite.value = UIState(data = false)
                    currentFavoriteProductId = null
                }
            }
        }
    }
}