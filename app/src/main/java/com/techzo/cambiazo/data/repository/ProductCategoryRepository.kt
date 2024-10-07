package com.techzo.cambiazo.data.repository

import com.techzo.cambiazo.common.Resource
import com.techzo.cambiazo.data.remote.products.ProductCategoryService
import com.techzo.cambiazo.data.remote.products.toProductCategory
import com.techzo.cambiazo.domain.ProductCategory
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class ProductCategoryRepository(private val productCategoryService: ProductCategoryService) {

    suspend fun getProductCategories(): Resource<List<ProductCategory>> = withContext(Dispatchers.IO) {
        try {
            val response = productCategoryService.getProductCategories()
            if (response.isSuccessful) {
                response.body()?.let{ productCategoriesDto->
                    val productCategories = mutableListOf<ProductCategory>()
                    productCategoriesDto.forEach{ productCategoryDto->
                        productCategories.add(productCategoryDto.toProductCategory())

                    }
                    return@withContext Resource.Success(data = productCategories)
                }
                return@withContext Resource.Error("No se encontraron categorías de productos")
            }
            return@withContext Resource.Error(response.message())
        } catch (e: Exception) {
            return@withContext Resource.Error(e.message ?: "Ocurrió un error")
        }
    }

}