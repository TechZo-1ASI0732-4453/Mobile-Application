package com.techzo.cambiazo.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "favorite_products")
data class FavoriteProductEntity(
    @PrimaryKey
    val id: Int,
    val productId: Int,
    val userId: Int
)
