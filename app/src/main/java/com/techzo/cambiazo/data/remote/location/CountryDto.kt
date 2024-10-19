package com.techzo.cambiazo.data.remote.location

import com.techzo.cambiazo.domain.Country

data class CountryDto(
    val id: Int,
    val name: String
)

fun CountryDto.toCountry() = Country(
    id = id,
    name = name
)

