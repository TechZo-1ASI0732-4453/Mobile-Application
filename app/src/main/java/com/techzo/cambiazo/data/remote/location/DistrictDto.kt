package com.techzo.cambiazo.data.remote.location

import com.techzo.cambiazo.domain.District

data class DistrictDto(
    val departmentId: Int,
    val id: Int,
    val name: String
)


fun DistrictDto.toDistrict() = District(
    departmentId = departmentId,
    id = id,
    name = name
)