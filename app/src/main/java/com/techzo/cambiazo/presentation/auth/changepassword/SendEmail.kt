package com.techzo.cambiazo.presentation.auth.changepassword

import android.util.Log
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONObject

suspend fun sendEmail(name: String, email: String, verificationCode: String) {
    withContext(Dispatchers.IO) {

        val client = OkHttpClient.Builder().build()

        val url = "https://api.emailjs.com/api/v1.0/email/send"

        val serviceId = "service_n1ojeiz"
        val templateId = "template_akfqe76"
        val userId = "lxgr2Cag9uXB3-qzt"

        val templateParams = JSONObject().apply {
            put("name", name)
            put("verification_code", verificationCode)
            put("email", email)
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
                    Log.d("EMAIL_VERIFICATION", "Correo enviado exitosamente! Código de verificación: $verificationCode")
                } else {
                    Log.e("EMAIL_VERIFICATION", "Error al enviar el correo. Código de respuesta: ${response.code}, Mensaje: ${response.message}")
                }
            }
        } catch (e: Exception) {
            Log.e("EMAIL_VERIFICATION", "Error al enviar el correo: ${e.message}")
        }
    }
}