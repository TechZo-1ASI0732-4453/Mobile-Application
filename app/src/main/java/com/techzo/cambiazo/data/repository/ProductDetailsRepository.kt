package com.techzo.cambiazo.data.repository

import com.techzo.cambiazo.common.Constants
import com.techzo.cambiazo.common.Resource
import com.techzo.cambiazo.data.local.FavoriteProductDao
import com.techzo.cambiazo.data.local.FavoriteProductEntity
import com.techzo.cambiazo.data.remote.products.FavoriteProductDto
import com.techzo.cambiazo.data.remote.products.FavoriteProductRequestDto
import com.techzo.cambiazo.data.remote.products.FavoriteProductService
import com.techzo.cambiazo.data.remote.products.toFavoriteProduct
import com.techzo.cambiazo.domain.FavoriteProduct
import com.techzo.cambiazo.domain.FavoriteProductRequest
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class ProductDetailsRepository(
    private val favoriteProductDao: FavoriteProductDao,
    private val favoriteProductService: FavoriteProductService
) {

    suspend fun isProductFavorite(productId: Int): Resource<Boolean> =
        withContext(Dispatchers.IO) {
            try {
                val favoriteProduct = favoriteProductDao.getFavoriteProductByUserAndProduct(Constants.user!!.id, productId)
                Resource.Success(data = favoriteProduct != null)
            } catch (e: Exception) {
                Resource.Error(e.message ?: "Ocurrió un error")
            }
        }

    suspend fun getFavoriteProductByUserId(userId: Int): Resource<List<FavoriteProduct>> =
        withContext(Dispatchers.IO) {
            try {
                val response = favoriteProductService.getFavoriteProductsByUserId(userId)
                if (response.isSuccessful) {
                    response.body()?.let { favoriteProductsDto ->
                        val favoriteProducts = favoriteProductsDto.map { it.toFavoriteProduct() }
                        return@withContext Resource.Success(data = favoriteProducts)
                    }
                    return@withContext Resource.Error("No se encontraron productos favoritos")
                }
                return@withContext Resource.Error(response.message())
            } catch (e: Exception) {
                return@withContext Resource.Error(e.message ?: "Ocurrió un error")
            }
        }

    suspend fun addFavoriteProduct(productId: Int): Resource<FavoriteProductRequest> =
        withContext(Dispatchers.IO) {
            try {
                val favoriteProductDto = FavoriteProductRequestDto(
                    id = 0,
                    productId = productId,
                    userId = Constants.user!!.id
                )
                val response = favoriteProductService.addFavoriteProduct(favoriteProductDto)
                if (response.isSuccessful) {
                    response.body()?.let {
                        val favoriteProduct = it.toFavoriteProduct()
                        favoriteProductDao.insertFavoriteProduct(FavoriteProductEntity(favoriteProduct.id, favoriteProduct.productId, favoriteProduct.userId))
                        return@withContext Resource.Success(data = favoriteProduct)
                    }
                    return@withContext Resource.Error("Error al agregar a favoritos")
                }
                return@withContext Resource.Error(response.message())
            } catch (e: Exception) {
                Resource.Error(e.message ?: "Ocurrió un error")
            }
        }

    suspend fun deleteFavoriteProduct(productId: Int): Resource<Unit> =
        withContext(Dispatchers.IO) {
            try {
                val deleteResponse = favoriteProductService.removeFavoriteProduct(Constants.user!!.id, productId)
                if (deleteResponse.isSuccessful) {
                    favoriteProductDao.deleteFavoriteProductByUserAndProduct(Constants.user!!.id, productId)
                    return@withContext Resource.Success(Unit)
                } else {
                    return@withContext Resource.Error(deleteResponse.message())
                }
            } catch (e: Exception) {
                Resource.Error(e.message ?: "Ocurrió un error")
            }
        }
}