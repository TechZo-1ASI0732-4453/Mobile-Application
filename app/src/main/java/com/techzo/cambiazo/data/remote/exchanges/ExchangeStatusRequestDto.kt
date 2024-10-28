package com.techzo.cambiazo.data.remote.exchanges

import com.google.gson.annotations.SerializedName

data class ExchangeStatusRequestDto(
    @SerializedName("status")
    val status: String
)
