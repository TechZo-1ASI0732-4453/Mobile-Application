package com.techzo.cambiazo.data.remote.exchanges

import retrofit2.Response
import retrofit2.http.*

interface ExchangeService {

    @Headers("Content-Type: application/json",
        "Accept: application/json")
    @GET("exchanges/userOwn/{userId}")
    suspend fun getExchangesByUserOwnId(@Path("userId") userId: Int): Response<List<ExchangeDto>>

    @Headers("Content-Type: application/json",
        "Accept: application/json")
    @GET("exchanges/userChange/{userId}")
    suspend fun getExchangesByUserChangeId(@Path("userId") userId: Int): Response<List<ExchangeDto>>

    @Headers("Content-Type: application/json",
        "Accept: application/json")
    @GET("exchanges/finished/{userId}")
    suspend fun getFinishedExchangesByUserId(@Path("userId") userId: Int): Response<List<ExchangeDto>>

    @Headers("Content-Type: application/json",
        "Accept: application/json")
    @GET("exchanges/{exchangeId}")
    suspend fun getExchangeById(@Path("exchangeId") exchangeId: Int): Response<ExchangeDto>

    @Headers("Content-Type: application/json", "Accept: application/json")
    @POST("exchanges")
    suspend fun createExchange(@Body exchange: ExchangeRequestDto): Response<ExchangeResponseDto>
  
    @Headers("Content-Type: application/json",
        "Accept: application/json")
    @PUT("exchanges/status/{exchangeId}")
    suspend fun updateExchangeStatus(@Path("exchangeId") exchangeId: Int, @Body status: ExchangeStatusRequestDto):Response<ExchangeDto>

    @Headers("Content-Type: application/json",
        "Accept: application/json")
    @DELETE("exchanges/delete/{exchangeId}")
    suspend fun deleteExchange(@Path("exchangeId") exchangeId: Int): Response<Void>


}