package com.techzo.cambiazo.presentation.explorer.offer

import android.util.Log
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONObject

suspend fun sendOfferEmail(
    name: String,
    email: String,
    itemTitle: String,
    status: String
) {
    withContext(Dispatchers.IO) {

        val client = OkHttpClient.Builder().build()

        val url = "https://api.emailjs.com/api/v1.0/email/send"

        val serviceId = "service_n1ojeiz"
        val templateId = "template_ogjf4h9"
        val userId = "lxgr2Cag9uXB3-qzt"


        val (statusMessage, statusColor, statusHint) = when (status.uppercase()) {
            "RECIBIDA" -> Triple(
                "¡Has recibido una nueva oferta por uno de tus productos!",
                "#28a745",
                "Revisa los detalles en la app y responde cuanto antes."
            )
            "ACEPTADA" -> Triple(
                "¡Tu oferta ha sido aceptada! Felicidades, ahora pueden coordinar el intercambio.",
                "#007bff",
                "Contacta a la otra persona desde la app para concretar el trato."
            )
            "RECHAZADA" -> Triple(
                "Tu oferta fue rechazada. ¡No te desanimes y sigue buscando artículos interesantes!",
                "#dc3545",
                "Puedes seguir explorando opciones dentro de CambiaZo."
            )
            else -> Triple(
                "Hay una actualización respecto a tu oferta.",
                "#6c757d",
                "Ingresa a la app para más detalles."
            )
        }

        val templateParams = JSONObject().apply {
            put("name", name)
            put("email", email)
            put("item_title", itemTitle.uppercase())
            put("status", status)
            put("status_message", statusMessage)
            put("status_color", statusColor)
            put("status_hint", statusHint)
        }

        val json = JSONObject().apply {
            put("service_id", serviceId)
            put("template_id", templateId)
            put("user_id", userId)
            put("template_params", templateParams)
        }

        val mediaType = "application/json".toMediaTypeOrNull()
        val body = json.toString().toRequestBody(mediaType)

        val request = Request.Builder()
            .url(url)
            .post(body)
            .addHeader("Content-Type", "application/json")
            .build()

        try {
            client.newCall(request).execute().use { response ->
                if (response.isSuccessful) {
                    Log.d("EMAIL_OFFER", "Correo de oferta ($status) enviado exitosamente a $email")
                } else {
                    Log.e("EMAIL_OFFER", "Error al enviar correo. Código: ${response.code}, Mensaje: ${response.message}")
                }
            }
        } catch (e: Exception) {
            Log.e("EMAIL_OFFER", "Excepción al enviar el correo: ${e.message}")
        }
    }
}

