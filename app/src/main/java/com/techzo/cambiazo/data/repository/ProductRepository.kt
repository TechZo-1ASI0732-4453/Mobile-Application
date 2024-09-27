package com.techzo.cambiazo.data.repository

import com.techzo.cambiazo.common.Resource
import com.techzo.cambiazo.data.remote.ProductService
import com.techzo.cambiazo.data.remote.toProduct
import com.techzo.cambiazo.domain.model.Product
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class ProductRepository(private val productService: ProductService) {
    suspend fun getProducts(): Resource<List<Product>> = withContext(Dispatchers.IO) {
        try {
            val response = productService.getProducts()
            if (response.isSuccessful) {
                response.body()?.let{ productsDto->
                    val products = mutableListOf<Product>()
                    productsDto.forEach{ productDto->
                        products.add(productDto.toProduct())
                    }
                    return@withContext Resource.Success(data = products)
                }
                return@withContext Resource.Error("No se encontraron categorías de productos")
            }
            return@withContext Resource.Error(response.message())
        } catch (e: Exception) {
            return@withContext Resource.Error(e.message ?: "Ocurrió un error")
        }
    }

    suspend fun getProductsByCategoryId(id: Int): Resource<List<Product>> = withContext(Dispatchers.IO) {
        try {
            val response = productService.getProductsByCategoryId(id)
            if (response.isSuccessful) {
                response.body()?.let{ productsDto->
                    val products = mutableListOf<Product>()
                    productsDto.forEach{ productDto->
                        products.add(productDto.toProduct())
                    }
                    return@withContext Resource.Success(data = products)
                }
                return@withContext Resource.Error("No se encontraron productos")
            }
            return@withContext Resource.Error(response.message())
        } catch (e: Exception) {
            return@withContext Resource.Error(e.message ?: "Ocurrió un error")
        }
    }
}