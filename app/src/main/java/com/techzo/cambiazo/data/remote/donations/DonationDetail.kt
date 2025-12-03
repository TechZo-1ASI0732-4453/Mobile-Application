package com.techzo.cambiazo.data.remote.donations


import com.techzo.cambiazo.domain.AccountNumber
import com.techzo.cambiazo.domain.CategoryOng
import com.techzo.cambiazo.domain.OngDetail
import com.techzo.cambiazo.domain.Project

data class OngDetailDto(
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
    val categoryOngId: CategoryOngDto,
    val projects: List<ProjectDto>,
    val accountNumbers: List<AccountNumberDto>
)

data class CategoryOngDto(
    val id: Int,
    val name: String,
    val categoryId: Int
)

data class ProjectDto(
    val id: Int,
    val createdAt: String,
    val updatedAt: String,
    val name: String,
    val description: String,
    val ongId: Int
)

data class AccountNumberDto(
    val id: Int,
    val accountNumber: String?,
    val bankName: String?,
    val currency: String?,
    val ongId: Int
)

fun CategoryOngDto.toCategoryOng() = CategoryOng(
    id = id,
    name = name
)

fun ProjectDto.toProject() = Project(
    id = id,
    name = name,
    description = description
)

fun AccountNumberDto.toAccountNumber() = AccountNumber(
    id = id,
    accountNumber = accountNumber ?: "Sin cuenta",
    bankName = bankName ?: "Desconocido",
    currency = currency ?: "-"
)
fun OngDetailDto.toOng(): OngDetail {
    return OngDetail(
        id = id,
        name = name,
        type = type,
        aboutUs = aboutUs,
        missionAndVision = missionAndVision,
        supportForm = supportForm,
        address = address,
        email = email,
        phone = phone,
        logo = logo,
        website = website,
        schedule = schedule,
        category = categoryOngId.toCategoryOng(),
        projects = projects.map { it.toProject() },
        accounts = accountNumbers.map { it.toAccountNumber() }
    )
}