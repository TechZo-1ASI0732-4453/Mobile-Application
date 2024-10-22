package com.techzo.cambiazo.data.repository

import com.techzo.cambiazo.common.Resource
import com.techzo.cambiazo.data.remote.products.ProductService
import com.techzo.cambiazo.data.remote.products.toProduct
import com.techzo.cambiazo.domain.Product
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class ProductRepository(private val productService: ProductService) {
    suspend fun getProducts(): Resource<List<Product>> = withContext(Dispatchers.IO) {
        try {
            val response = productService.getProducts()
            if (response.isSuccessful) {
                response.body()?.let { productsDto ->
                    val products = mutableListOf<Product>()
                    productsDto.forEach { productDto ->
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
                response.body()?.let { productsDto ->
                    val products = mutableListOf<Product>()
                    productsDto.forEach { productDto ->
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

    suspend fun getProductById(id: Int): Resource<Product> = withContext(Dispatchers.IO) {
        try {
            val response = productService.getProductById(id)
            if (response.isSuccessful) {
                response.body()?.let { productDto ->
                    return@withContext Resource.Success(data = productDto.toProduct())
                }
                return@withContext Resource.Error("No se encontró el producto")
            }
            return@withContext Resource.Error(response.message())
        } catch (e: Exception) {
            return@withContext Resource.Error(e.message ?: "Ocurrió un error")
        }
    }
    
    suspend fun getProductsByUserId(userId:Int): Resource<List<Product>> = withContext(Dispatchers.IO) {
        try {
            val response = productService.getProductsByUserId(userId)
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

    suspend fun deleteProduct(productId: Int): Resource<Unit> = withContext(Dispatchers.IO) {
        try {
            val response = productService.deleteProduct(productId)
            if (response.isSuccessful) {
                return@withContext Resource.Success(data = Unit)
}