package com.techzo.cambiazo.domain

data class FilterValues(
    var categoryId: Int? = null,
    var countryId: Int? = null,
    var departmentId: Int? = null,
    var districtId: Int? = null,
    var maxPrice: Double? = null,
    var minPrice: Double? = null
){
    fun clear(){
        categoryId = null
        countryId = null
        departmentId = null
        districtId = null
        maxPrice = null
        minPrice = null
    }

    fun isEmpty(): Boolean {
        return categoryId == null && countryId == null && departmentId == null && districtId == null && maxPrice == null && minPrice == null
    }

    
}