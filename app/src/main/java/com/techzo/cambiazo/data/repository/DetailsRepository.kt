package com.techzo.cambiazo.data.repository

import com.techzo.cambiazo.common.Constants
import com.techzo.cambiazo.common.Resource
import com.techzo.cambiazo.data.remote.auth.UserService
import com.techzo.cambiazo.data.remote.auth.toUser
import com.techzo.cambiazo.data.remote.location.DepartmentService
import com.techzo.cambiazo.data.remote.location.DistrictService
import com.techzo.cambiazo.data.remote.location.toDepartment
import com.techzo.cambiazo.data.remote.location.toDistrict
import com.techzo.cambiazo.data.remote.products.FavoriteProductDto
import com.techzo.cambiazo.data.remote.products.FavoriteProductService
import com.techzo.cambiazo.data.remote.products.ProductCategoryService
import com.techzo.cambiazo.data.remote.products.ProductService
import com.techzo.cambiazo.data.remote.products.toFavoriteProduct
import com.techzo.cambiazo.data.remote.products.toFavoriteProductDto
import com.techzo.cambiazo.data.remote.products.toProduct
import com.techzo.cambiazo.data.remote.products.toProductCategory
import com.techzo.cambiazo.data.remote.reviews.ReviewService
import com.techzo.cambiazo.data.remote.reviews.toReview
import com.techzo.cambiazo.domain.Department
import com.techzo.cambiazo.domain.District
import com.techzo.cambiazo.domain.FavoriteProduct
import com.techzo.cambiazo.domain.Product
import com.techzo.cambiazo.domain.ProductCategory
import com.techzo.cambiazo.domain.Review
import com.techzo.cambiazo.domain.User
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class DetailsRepository(
    private val productService: ProductService,
    private val userService: UserService,
    private val reviewService: ReviewService,
    private val categoryService: ProductCategoryService,
    private val districtService: DistrictService,
    private val departmentService: DepartmentService,
    private val favoriteProductService: FavoriteProductService
) {
    suspend fun getReviewsByUserId(userId: Int): Resource<List<Review>> = withContext(Dispatchers.IO) {
        try {
            val response = reviewService.getReviewsByUserId(userId)
            if (response.isSuccessful) {
                response.body()?.let { reviewsDto ->
                    val reviews = reviewsDto.map { it.toReview() }
                    return@withContext Resource.Success(data = reviews)
                }
                return@withContext Resource.Error("No reviews found")
            }
            return@withContext Resource.Error(response.message())
        } catch (e: Exception) {
            return@withContext Resource.Error(e.message ?: "An error occurred")
        }
    }

    suspend fun getDistrictById(districtId: Int): Resource<District> = withContext(Dispatchers.IO) {
        try {
            val response = districtService.getDistrictById(districtId)
            if (response.isSuccessful) {
                response.body()?.let { districtDto ->
                    return@withContext Resource.Success(data = districtDto.toDistrict())
                }
                return@withContext Resource.Error("District not found")
            }
            return@withContext Resource.Error(response.message())
        } catch (e: Exception) {
            return@withContext Resource.Error(e.message ?: "An error occurred")
        }
    }


    suspend fun getUserById(id: Int): Resource<User> = withContext(Dispatchers.IO) {
        try{
            val response = userService.getUserById(id)
            if(response.isSuccessful){
                response.body()?.let { userDto ->
                    return@withContext Resource.Success(data = userDto.toUser())
                }
                return@withContext Resource.Error("No se encontró el usuario")
            }
            return@withContext Resource.Error(response.message())
        }catch (e: Exception){
            return@withContext Resource.Error(e.message ?: "Ocurrió un error")
        }
    }

    suspend fun getProductById(productId: Int): Resource<Product> = withContext(Dispatchers.IO) {
        try {
            val response = productService.getProductById(productId)
            if (response.isSuccessful) {
                response.body()?.let { productDto ->
                    return@withContext Resource.Success(data = productDto.toProduct())
                }
                return@withContext Resource.Error("Product not found")
            }
            return@withContext Resource.Error(response.message())
        } catch (e: Exception) {
            return@withContext Resource.Error(e.message ?: "An error occurred")
        }
    }

    suspend fun getProductCategoryById(categoryId: Int): Resource<ProductCategory> = withContext(Dispatchers.IO) {
        try {
            val response = categoryService.getCategoryById(categoryId)
            if (response.isSuccessful) {
                response.body()?.let { categoryDto ->
                    return@withContext Resource.Success(data = categoryDto.toProductCategory())
                }
                return@withContext Resource.Error("Category not found")
            }
            return@withContext Resource.Error(response.message())
        } catch (e: Exception) {
            return@withContext Resource.Error(e.message ?: "An error occurred")
        }
    }

    suspend fun getDepartmentById(departmentId: Int): Resource<Department> = withContext(Dispatchers.IO) {
        try {
            val response = departmentService.getDepartmentById(departmentId)
            if (response.isSuccessful) {
                response.body()?.let { departmentDto ->
                    return@withContext Resource.Success(data = departmentDto.toDepartment())
                }
                return@withContext Resource.Error("Department not found")
            }
            return@withContext Resource.Error(response.message())
        } catch (e: Exception) {
            return@withContext Resource.Error(e.message ?: "An error occurred")
        }
    }

    suspend fun isProductFavorite(userId: Int, productId: Int): Resource<Boolean> = withContext(Dispatchers.IO) {
        try {
            val response = favoriteProductService.getFavoriteProductsByUserId(userId)
            if (response.isSuccessful) {
                response.body()?.let { favoriteProductsDto ->
                    val isFavorite = favoriteProductsDto.any { it.productId == productId }
                    return@withContext Resource.Success(data = isFavorite)
                }
                return@withContext Resource.Error("No favorite products found")
            }
            return@withContext Resource.Error(response.message())
        } catch (e: Exception) {
            return@withContext Resource.Error(e.message ?: "An error occurred")
        }
    }

    // Agregar un producto a los favoritos
    suspend fun addFavoriteProduct(userId: Int, productId: Int): Resource<FavoriteProduct> = withContext(Dispatchers.IO) {
        try {
            val favoriteProduct = FavoriteProduct(id = 0, productId = productId, userId = userId)
            val response = favoriteProductService.addFavoriteProduct(favoriteProduct.toFavoriteProductDto())
            if (response.isSuccessful) {
                response.body()?.let {
                    return@withContext Resource.Success(data = it.toFavoriteProduct())
                }
            }
            return@withContext Resource.Error(response.message())
        } catch (e: Exception) {
            return@withContext Resource.Error(e.message ?: "An error occurred")
        }
    }

    // Eliminar un producto de los favoritos
    suspend fun removeFavoriteProduct(favoriteProductId: Int): Resource<Unit> = withContext(Dispatchers.IO) {
        try {
            val response = favoriteProductService.removeFavoriteProduct(Constants.user!!.id,favoriteProductId)
            if (response.isSuccessful) {
                return@withContext Resource.Success(Unit)
            }
            return@withContext Resource.Error(response.message())
        } catch (e: Exception) {
            return@withContext Resource.Error(e.message ?: "An error occurred")
        }
    }
}