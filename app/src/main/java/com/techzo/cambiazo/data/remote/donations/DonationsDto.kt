package com.techzo.cambiazo.data.remote.donations

data class OngDto(
    val id: Int,
    val name: String,
    val type: String,
    val aboutUs: String,
    val missionAndVision: String,
    val supportForm: String,
    val address: String,
    val email: String,
    val phone: String,
    val logo: String,
    val website: String,
    val categoryOngId: Int,
    val schedule: String
)
