package com.techzo.cambiazo.data.remote.products

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Location(
    val countryId: Int,
    val countryName: String,
    val departmentId: Int,
    val departmentName: String,
    val districtId: Int,
    val districtName: String
): Parcelable