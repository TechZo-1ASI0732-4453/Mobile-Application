package com.techzo.cambiazo.data.remote.exchanges

import com.google.gson.annotations.SerializedName
import com.techzo.cambiazo.data.repository.ProductRepository
import com.techzo.cambiazo.data.repository.UserRepository
import com.techzo.cambiazo.domain.Exchange

data class ExchangeDto(
    @SerializedName("id")
    val id: Int,
    @SerializedName("productOwnId")
    val productOwnId: Int,
    @SerializedName("productChangeId")
    val productChangeId: Int,
    @SerializedName("status")
    val status: String,
)

suspend fun ExchangeDto.toExchange(productRepository: ProductRepository, userRepository: UserRepository): Exchange {
    val productOwnResource = productRepository.getProductById(productOwnId)
    val productChangeResource = productRepository.getProductById(productChangeId)

    val productOwn = productOwnResource.data ?: throw Exception("Failed to fetch productOwn")
    val productChange = productChangeResource.data ?: throw Exception("Failed to fetch productChange")

    val userOwnResource = userRepository.getUserById(productOwn.user.id)
    val userChangeResource = userRepository.getUserById(productChange.user.id)

    val userOwn = userOwnResource.data ?: throw Exception("Failed to fetch userOwn")
    val userChange = userChangeResource.data ?: throw Exception("Failed to fetch userChange")

    return Exchange(
        id = id,
        productOwn = productOwn,
        productChange = productChange,
        userOwn = userOwn,
        userChange = userChange,
        status = status
    )
}
