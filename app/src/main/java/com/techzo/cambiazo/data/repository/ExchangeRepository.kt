package com.techzo.cambiazo.data.repository

import com.techzo.cambiazo.common.Resource
import com.techzo.cambiazo.data.remote.exchanges.ExchangeService
import com.techzo.cambiazo.data.remote.exchanges.toExchange
import com.techzo.cambiazo.domain.Exchange
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class ExchangeRepository(private val exchangeService: ExchangeService,
    private val productRepository: ProductRepository,
    private val userRepository: UserRepository
) {
    suspend fun getExchangesByUserOwnId(userId: Int): Resource<List<Exchange>> = withContext(Dispatchers.IO) {
        try {
            val response = exchangeService.getExchangesByUserOwnId(userId)
            if (response.isSuccessful) {
                response.body()?.let { exchangesDto ->
                    val exchanges = exchangesDto.map { it.toExchange(productRepository, userRepository) }
                    return@withContext Resource.Success(data = exchanges)
                }
                return@withContext Resource.Error("No exchanges found")
            }
            return@withContext Resource.Error(response.message())
        } catch (e: Exception) {
            return@withContext Resource.Error(e.message ?: "An error occurred")
        }
    }

    suspend fun getExchangesByUserChangeId(userId: Int): Resource<List<Exchange>> = withContext(Dispatchers.IO) {
        try {
            val response = exchangeService.getExchangesByUserChangeId(userId)
            if (response.isSuccessful) {
                response.body()?.let { exchangesDto ->
                    val exchanges = exchangesDto.map { it.toExchange(productRepository, userRepository) }
                    return@withContext Resource.Success(data = exchanges)
                }
                return@withContext Resource.Error("No exchanges found")
            }
            return@withContext Resource.Error(response.message())
        } catch (e: Exception) {
            return@withContext Resource.Error(e.message ?: "An error occurred")
        }
    }
}
