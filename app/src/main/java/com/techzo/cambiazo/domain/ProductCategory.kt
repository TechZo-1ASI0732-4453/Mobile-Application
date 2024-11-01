package com.techzo.cambiazo.domain

import android.os.Parcelable
import kotlinx.parcelize.Parcelize


@Parcelize
data class ProductCategory(
    val id: Int,
    val name: String
): Parcelable

