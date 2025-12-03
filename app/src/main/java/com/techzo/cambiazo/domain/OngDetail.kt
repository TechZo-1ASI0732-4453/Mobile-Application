package com.techzo.cambiazo.domain


data class OngDetail(
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
    val schedule: String,
    val category: CategoryOng,
    val projects: List<Project>,
    val accounts: List<AccountNumber>
)

data class CategoryOng(
    val id: Int,
    val name: String
)

data class Project(
    val id: Int,
    val name: String,
    val description: String
)

data class AccountNumber(
    val id: Int,
    val accountNumber: String,
    val bankName: String,
    val currency: String
)