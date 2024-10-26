package com.techzo.cambiazo.data.remote.location

import com.techzo.cambiazo.domain.Department

data class DepartmentDto(
    val countryId: Int,
    val id: Int,
    val name: String
)

fun DepartmentDto.toDepartment() = Department(
    countryId = countryId,
    id = id,
    name = name
)