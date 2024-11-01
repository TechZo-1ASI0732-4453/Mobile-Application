package com.techzo.cambiazo.domain

import android.os.Parcelable
import com.techzo.cambiazo.data.remote.products.Location
import kotlinx.parcelize.Parcelize

@Parcelize
data class Product(
    val id: Int,
    val name: String,
    val description: String,
    val desiredObject: String,
    val price: Int,
    val image: String,
    val boost: Boolean,
    val available: Boolean,
    val productCategory: ProductCategory,
    val user: User,
    val location: Location
): Parcelable



