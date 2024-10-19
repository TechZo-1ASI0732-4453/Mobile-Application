package com.techzo.cambiazo.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface FavoriteProductDao {
    @Query("SELECT * FROM favorite_products WHERE userId = :userId AND productId = :productId LIMIT 1")
    suspend fun getFavoriteProductByUserAndProduct(userId: Int, productId: Int): FavoriteProductEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertFavoriteProduct(favoriteProduct: FavoriteProductEntity)

    @Query("DELETE FROM favorite_products WHERE userId = :userId AND productId = :productId")
    suspend fun deleteFavoriteProductByUserAndProduct(userId: Int, productId: Int)
}
