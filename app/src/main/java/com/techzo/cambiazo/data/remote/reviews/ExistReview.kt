package com.techzo.cambiazo.data.remote.reviews

import com.google.gson.annotations.SerializedName

data class ExistReview(
    @SerializedName("existReview")
    val existReview: Boolean
)
