package com.techzo.cambiazo.data.remote.ai


data class CategoryOption(
    val externalKey: String,
    val name: String,
    val synonyms: List<String> = emptyList()
)