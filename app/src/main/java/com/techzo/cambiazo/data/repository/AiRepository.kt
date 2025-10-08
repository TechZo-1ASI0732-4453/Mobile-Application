package com.techzo.cambiazo.data.repository

import com.techzo.cambiazo.common.Resource
import com.techzo.cambiazo.data.remote.ai.AiService
import com.techzo.cambiazo.data.remote.ai.ProductSuggestionDto
import okhttp3.MultipartBody
import org.json.JSONObject
import retrofit2.HttpException
import java.io.IOException
import javax.inject.Inject
class AiRepository @Inject constructor(
    private val service: AiService
) {
    suspend fun suggestFromImage(userId: Long, filePart: MultipartBody.Part): Resource<ProductSuggestionDto> {
        return try {
            val dto = service.suggestFromImage(userId = userId, file = filePart)
            Resource.Success(dto)
        } catch (e: HttpException) {
            if (e.code() == 403) {
                val raw = e.response()?.errorBody()?.string().orEmpty()
                val msg = try {
                    val json = JSONObject(raw)
                    val type = json.optString("violationType")
                    val message = json.optString("message")
                    val minutes = json.optInt("banDurationMinutes", 0)
                    val policy = json.optString("policyReference")

                    val h = minutes / 60
                    val m = minutes % 60
                    val duration = if (minutes > 0) "${h}h ${m}m" else "sin restricción de tiempo"

                    buildString {
                        appendLine("Tipo: $type")
                        appendLine("Mensaje: $message")
                        appendLine("Sanción: $duration")
                        if (policy.isNotBlank()) append("Política: $policy")
                    }.trim()
                } catch (_: Exception) {
                    raw.ifBlank { "Acceso denegado (403)" }
                }
                Resource.Error(msg)
            } else {
                val msg = e.response()?.errorBody()?.string()?.takeIf { !it.isNullOrBlank() }
                    ?: e.message() ?: "Error HTTP ${e.code()}"
                Resource.Error(msg)
            }
        } catch (e: IOException) {
            Resource.Error("Sin conexión o timeout")
        } catch (e: Exception) {
            Resource.Error(e.localizedMessage ?: "Error llamando IA")
        }
    }
}