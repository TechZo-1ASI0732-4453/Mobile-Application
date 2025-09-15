package com.techzo.cambiazo.data.repository

import android.content.Context
import android.graphics.BitmapFactory
import android.net.Uri
import com.google.ai.client.generativeai.GenerativeModel
import com.google.ai.client.generativeai.type.content
import com.google.ai.client.generativeai.type.generationConfig
import com.techzo.cambiazo.common.Resource
import com.techzo.cambiazo.data.remote.ai.AiSuggestionDto
import com.techzo.cambiazo.data.remote.ai.CategoryOption
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONObject
import java.text.Normalizer

class GeminiAiRepository(
    private val apiKey: String,
    private val modelName: String = "gemini-1.5-flash-8b-latest"
) {

    companion object {
        private const val MIN_CATEGORY_CONFIDENCE = 0.58f
    }

    suspend fun analyzeImage(
        context: Context,
        uri: Uri,
        taxonomy: List<CategoryOption> = emptyList()
    ): Resource<AiSuggestionDto> =
        withContext(Dispatchers.IO) {
            try {
                val model = GenerativeModel(
                    modelName = modelName,
                    apiKey = apiKey,
                    generationConfig = generationConfig {
                        temperature = 0.0f
                        topK = 1
                        topP = 0.0f
                        maxOutputTokens = 220
                        responseMimeType = "application/json"
                    }
                )

                val bitmap = context.contentResolver.openInputStream(uri)?.use {
                    BitmapFactory.decodeStream(it)
                } ?: return@withContext Resource.Error("No pude leer la imagen.")

                val instruction = buildInstruction(taxonomy)

                val response = model.generateContent(
                    content {
                        text(instruction)
                        image(bitmap)
                    }
                )

                val raw = response.text?.trim().orEmpty()
                if (raw.isBlank()) return@withContext Resource.Error("La IA no devolvió texto.")

                val json = raw.findFirstJsonObject()
                    ?: return@withContext Resource.Error("La respuesta no fue JSON válido.")

                val parsed = JSONObject(json)

                val taxonomyKeys = taxonomy.map { it.externalKey }.toSet()

                var catKey = parsed.optStringOrNull("categoryExternalKey")?.trim()
                    ?.takeIf { it.isNotBlank() }
                if (taxonomyKeys.isNotEmpty() && catKey !in taxonomyKeys) {
                    catKey = null
                }

                val chosenFromCandidates: String? = runCatching {
                    val arr = parsed.optJSONArray("categoryCandidates") ?: return@runCatching null
                    var bestKey: String? = null
                    var bestConf = -1.0
                    for (i in 0 until arr.length()) {
                        val o = arr.optJSONObject(i) ?: continue
                        val key = o.optString("externalKey", "")
                        val conf = o.optDouble("confidence", -1.0)
                        if (key.isNotBlank() &&
                            (taxonomyKeys.isEmpty() || key in taxonomyKeys) &&
                            conf > bestConf
                        ) {
                            bestKey = key
                            bestConf = conf
                        }
                    }
                    if (bestConf >= MIN_CATEGORY_CONFIDENCE) bestKey else null
                }.getOrNull()

                if (chosenFromCandidates != null) {
                    catKey = chosenFromCandidates
                }

                val dto = AiSuggestionDto(
                    titleSuggestion = parsed.optStringOrNull("titleSuggestion")
                        ?.trim()?.trimTo(48),
                    descriptionSuggestion = parsed.optStringOrNull("descriptionSuggestion")
                        ?.trim()?.trimTo(180),
                    categoryExternalKey = catKey?.trim(),
                    priceEstimate = parsed.optIntOrNull("priceEstimate")?.let { it.coerceIn(1, 50_000) },
                    confidence = parsed.optDoubleOrNull("confidence")
                        ?.toFloat()
                        ?.let { it.coerceIn(0f, 1f) },
                    labels = parsed.optStringList("labels")
                        .asSequence()
                        .map { it.trim().lowercase() }
                        .filter { it.isNotEmpty() }
                        .distinct()
                        .take(3)
                        .toList(),
                    conditionScore = parsed.optIntOrNull("conditionScore")?.let { it.coerceIn(1, 10) },
                    conditionComment = parsed.optStringOrNull("conditionComment")
                        ?.trim()?.trimTo(60),
                    improvementTips = parsed.optStringList("improvementTips")
                        .trimShort(maxItems = 4, maxLen = 60),
                    photoTips = parsed.optStringList("photoTips")
                        .trimShort(maxItems = 3, maxLen = 60)
                )

                Resource.Success(dto)
            } catch (t: Throwable) {
                Resource.Error(t.message ?: "Error llamando a Gemini.")
            }
        }

    private fun buildInstruction(taxonomy: List<CategoryOption>): String {
        val taxonomyJson = if (taxonomy.isNotEmpty()) {
            taxonomy.joinToString(prefix = "[", postfix = "]") { c ->
                val syn = c.synonyms.joinToString { "\"$it\"" }
                """{"externalKey":"${c.externalKey}","name":"${c.name}","synonyms":[$syn]}"""
            }
        } else {
            "[]"
        }

        return """
Eres un asistente de marketplace en español. Responde **SOLO** un objeto JSON válido, sin texto adicional.

TAXONOMÍA VÁLIDA:
$taxonomyJson

REGLAS:
- "categoryExternalKey" debe ser una externalKey de la taxonomía; si ninguna aplica con suficiente confianza, usa null.
- Devuelve además "categoryCandidates": top 3 con {externalKey, confidence} (0..1), ordenados desc.
- Usa señales visuales (forma, materiales, uso), texto/ logos (si se ven) y contexto para decidir.
- No inventes marca/modelo si no es evidente.

Reglas de concisión (OBLIGATORIAS):
- "titleSuggestion": ≤ 48 caracteres, claro y sin emojis.
- "descriptionSuggestion": 1 frase, ≤ 180 caracteres, sin listas.
- "labels": máximo 3 etiquetas, todas en minúsculas.
- "improvementTips": máximo 4 ítems, cada uno ≤ 60 caracteres, imperativo (ej.: "Agrega medidas").
- "photoTips": máximo 3 ítems, cada uno ≤ 60 caracteres, prioriza luz, fondo, encuadre/ángulo, limpieza/arrugas, nitidez.
- "conditionComment": ≤ 60 caracteres, telegráfico.
- Si no hay datos confiables, usa null o [] según corresponda.

Responde SOLO este JSON:
{
  "titleSuggestion": string|null,
  "descriptionSuggestion": string|null,
  "categoryExternalKey": string|null,
  "priceEstimate": number|null,
  "confidence": number|null,  
  "labels": string[],                
  "conditionScore": number|null,      
  "conditionComment": string|null,    
  "improvementTips": string[],        
  "photoTips": string[],
  "categoryCandidates": [{"externalKey": string, "confidence": number}]
}
No agregues NADA fuera del JSON.
""".trimIndent()
    }
}

private fun String.findFirstJsonObject(): String? {
    val start = indexOf('{')
    val end = lastIndexOf('}')
    return if (start >= 0 && end > start) substring(start, end + 1) else null
}

private fun JSONObject.optStringOrNull(key: String): String? =
    if (has(key) && !isNull(key)) optString(key, null) else null

private fun JSONObject.optIntOrNull(key: String): Int? =
    if (has(key) && !isNull(key)) {
        when (val any = opt(key)) {
            is Number -> any.toInt()
            is String -> any.toIntOrNull()
            else -> null
        }
    } else null

private fun JSONObject.optDoubleOrNull(key: String): Double? =
    if (has(key) && !isNull(key)) {
        when (val any = opt(key)) {
            is Number -> any.toDouble()
            is String -> any.toDoubleOrNull()
            else -> null
        }
    } else null

private fun JSONObject.optStringList(key: String): List<String> {
    if (!has(key) || isNull(key)) return emptyList()
    val arr = optJSONArray(key) ?: return emptyList()
    return buildList {
        for (i in 0 until arr.length()) add(arr.optString(i, "").trim())
    }.filter { it.isNotEmpty() }
}

private fun List<String>.trimShort(maxItems: Int, maxLen: Int): List<String> =
    asSequence()
        .map { it.trim().replace(Regex("""^[•·\-\–\—]+\s*"""), "") }
        .filter { it.isNotBlank() }
        .map { if (it.length > maxLen) it.take(maxLen).trimEnd() else it }
        .take(maxItems)
        .toList()

private fun String.trimTo(maxLen: Int): String =
    if (length > maxLen) take(maxLen).trimEnd() else this

private fun String.removeAccents(): String =
    Normalizer.normalize(this, Normalizer.Form.NFD)
        .replace(Regex("\\p{InCombiningDiacriticalMarks}+"), "")
