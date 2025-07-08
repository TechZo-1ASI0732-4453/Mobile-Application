package com.techzo.cambiazo.data.repository

import com.techzo.cambiazo.common.Resource
import com.techzo.cambiazo.data.remote.exchanges.ExchangeDto
import com.techzo.cambiazo.data.remote.exchanges.ExchangeRequestDto
import com.techzo.cambiazo.data.remote.exchanges.ExchangeResponseDto
import com.techzo.cambiazo.data.remote.exchanges.ExchangeService
import com.techzo.cambiazo.data.remote.exchanges.ExchangeStatusRequestDto
import com.techzo.cambiazo.data.remote.exchanges.toExchange
import com.techzo.cambiazo.domain.Exchange
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.withContext

class ExchangeRepository(private val exchangeService: ExchangeService
) {
    suspend fun getExchangesByUserOwnId(userId: Int): Resource<List<Exchange>> =
        withContext(Dispatchers.IO) {
            try {
                val response = exchangeService.getExchangesByUserOwnId(userId)
                if (response.isSuccessful) {
                    response.body()?.let { exchangesDto ->
                        val pendingExchanges = exchangesDto.filter { it.status == "Pendiente" }
                        val exchanges = pendingExchanges.map { it.toExchange() }
                        return@withContext Resource.Success(data = exchanges)
                    }
                    return@withContext Resource.Error("No exchanges found")
                }
                return@withContext Resource.Error(response.message())
            } catch (e: Exception) {
                return@withContext Resource.Error(e.message ?: "An error occurred")
            }
        }

    suspend fun getExchangesByUserChangeId(userId: Int): Resource<List<Exchange>> =
        withContext(Dispatchers.IO) {
            try {
                val response = exchangeService.getExchangesByUserChangeId(userId)
                if (response.isSuccessful) {
                    response.body()?.let { exchangesDto ->
                        val pendingExchanges = exchangesDto.filter { it.status == "Pendiente" }
                        val exchanges = pendingExchanges.map { it.toExchange() }
                        return@withContext Resource.Success(data = exchanges)
                    }
                    return@withContext Resource.Error("No exchanges found")
                }
                return@withContext Resource.Error(response.message())
            } catch (e: Exception) {
                return@withContext Resource.Error(e.message ?: "An error occurred")
            }
        }

    suspend fun getFinishedExchanges(userId: Int): Resource<List<Exchange>> =
        withContext(Dispatchers.IO) {
            try {
                val response = exchangeService.getFinishedExchangesByUserId(userId)
                if (response.isSuccessful) {
                    response.body()?.let { exchangesDto ->
                        val exchanges = exchangesDto.map { it.toExchange() }
                        return@withContext Resource.Success(data = exchanges)
                    }
                    return@withContext Resource.Error("No exchanges found")
                }
                return@withContext Resource.Error(response.message())
            } catch (e: Exception) {
                return@withContext Resource.Error(e.message ?: "An error occurred")
            }
        }

    suspend fun getExchangeById(exchangeId: Int): Resource<Exchange> = withContext(Dispatchers.IO) {
        try {
            val response = exchangeService.getExchangeById(exchangeId)
            if (response.isSuccessful) {
                response.body()?.let { exchangeDto ->
                    val exchange = exchangeDto.toExchange()
                    return@withContext Resource.Success(data = exchange)
                }
                return@withContext Resource.Error("Exchange not found")
            }
            return@withContext Resource.Error(response.message())
        } catch (e: Exception) {
            return@withContext Resource.Error(e.message ?: "An error occurred")
        }
    }

    suspend fun updateExchangeStatus(exchangeId: Int, status: String): Resource<Boolean> =
        withContext(Dispatchers.IO) {
            try {
                val response = exchangeService.updateExchangeStatus(
                    exchangeId,
                    ExchangeStatusRequestDto(status)
                )
                if (response.isSuccessful) {
                    return@withContext Resource.Success(true)
                }
                return@withContext Resource.Error(response.message())
            } catch (e: Exception) {
                return@withContext Resource.Error(e.message ?: "An error occurred")
            }
        }


    suspend fun createExchange(exchangeRequestDto: ExchangeRequestDto): Resource<ExchangeResponseDto> {
        return withContext(Dispatchers.IO) {
            try {
                val response = exchangeService.createExchange(exchangeRequestDto)
                if (response.isSuccessful && response.body() != null) {
                    Resource.Success(response.body()!!)
                } else {
                    Resource.Error("Error al crear el intercambio: ${response.code()}")
                }
            } catch (e: Exception) {
                Resource.Error("Excepción al crear intercambio: ${e.message}")
            }
        }
    }


    suspend fun checkIfExchangeExists(
        userId: Int,
        productOwnId: Int,
        productChangeId: Int
    ): Resource<Boolean> =
        withContext(Dispatchers.IO) {
            try {
                coroutineScope {
                    val responseOwnDeferred =
                        async { exchangeService.getExchangesByUserOwnId(userId) }
                    val responseChangeDeferred =
                        async { exchangeService.getExchangesByUserChangeId(userId) }

                    val responseOwn = responseOwnDeferred.await()
                    val responseChange = responseChangeDeferred.await()

                    if (responseOwn.isSuccessful && responseChange.isSuccessful) {
                        val exchangesDto =
                            (responseOwn.body() ?: emptyList()) + (responseChange.body()
                                ?: emptyList())
                        val exists = exchangesDto.any { exchange ->
                            exchange.status == "Pendiente" &&
                                    ((exchange.productOwn.id == productOwnId && exchange.productChange.id == productChangeId) ||
                                            (exchange.productOwn.id == productChangeId && exchange.productChange.id == productOwnId))
                        }
                        Resource.Success(exists)
                    } else {
                        Resource.Error("No se pudieron obtener las ofertas")
                    }
                }
            } catch (e: Exception) {
                Resource.Error(e.message ?: "Ocurrió un error")
            }
        }

    suspend fun deleteExchange(exchangeId: Int): Resource<Boolean> {
        return withContext(Dispatchers.IO) {
            try {
                val response = exchangeService.deleteExchange(exchangeId)
                if (response.isSuccessful) {
                    Resource.Success(true)
                } else {
                    Resource.Error("Failed to delete exchange")
                }
            } catch (e: Exception) {
                Resource.Error(e.message ?: "Unknown error")
            }
        }
    }
}
