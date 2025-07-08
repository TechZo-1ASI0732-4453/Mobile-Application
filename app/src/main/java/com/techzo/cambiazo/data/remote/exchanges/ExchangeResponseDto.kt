package com.techzo.cambiazo.data.remote.exchanges

import com.google.gson.annotations.SerializedName

data class ExchangeResponseDto(
    @SerializedName("id")
    val id: Int,

    @SerializedName("productOwnId")
    val productOwnId: Int,

    @SerializedName("productChangeId")
    val productChangeId: Int,

    @SerializedName("status")
    val status: String,

    @SerializedName("exchangeDate")
    val exchangeDate: String,

    @SerializedName("createdAt")
    val createdAt: String,

    @SerializedName("updatedAt")
    val updatedAt: String
)

fun ExchangeResponseDto.toExchangeResponseDto(): ExchangeResponseDto {
    return ExchangeResponseDto(
        id = id,
        productOwnId = productOwnId,
        productChangeId = productChangeId,
        status = status,
        exchangeDate = exchangeDate,
        createdAt = createdAt,
        updatedAt = updatedAt
    )
}

