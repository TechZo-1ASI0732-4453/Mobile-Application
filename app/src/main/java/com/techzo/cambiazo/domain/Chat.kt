package com.techzo.cambiazo.domain

import com.google.gson.Gson
import java.text.SimpleDateFormat
import java.util.*


data class Chat(
    val senderId: String,
    val receiverId: String,
    val conversationId: String,
    val content: String,
    val timestamp: String = getCurrentTimestamp(),
    val id: String = UUID.randomUUID().toString(),

) {
    fun toJson(): String = Gson().toJson(this)

    companion object {
        fun fromJson(json: String): Chat = Gson().fromJson(json, Chat::class.java)

        private fun getCurrentTimestamp(): String {
            val sdf = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.getDefault())
            sdf.timeZone = TimeZone.getTimeZone("UTC")
            return sdf.format(Date(System.currentTimeMillis()))
        }
    }
}


