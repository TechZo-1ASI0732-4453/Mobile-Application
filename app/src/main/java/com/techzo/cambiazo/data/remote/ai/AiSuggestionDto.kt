package com.techzo.cambiazo.data.remote.ai

data class AiSuggestionDto(
    val titleSuggestion: String? = null,
    val descriptionSuggestion: String? = null,
    val categoryExternalKey: String? = null,
    val priceEstimate: Int? = null,
    val confidence: Float? = null,
    val labels: List<String> = emptyList(),
    val conditionScore: Int? = null,
    val conditionComment: String? = null,
    val improvementTips: List<String> = emptyList(),
    val photoTips: List<String> = emptyList()
)
