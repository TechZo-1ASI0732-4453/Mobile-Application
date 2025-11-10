package com.techzo.cambiazo.data.remote.chat

import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.TimeZone

object DateTimeUtils {
    private val mainIsoUtc: ThreadLocal<SimpleDateFormat> = object : ThreadLocal<SimpleDateFormat>() {
        override fun initialValue(): SimpleDateFormat {
            return SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.US).apply {
                timeZone = TimeZone.getTimeZone("UTC")
            }
        }
    }

    private val parsers: Array<ThreadLocal<SimpleDateFormat>> = arrayOf(
        object : ThreadLocal<SimpleDateFormat>() {
            override fun initialValue() = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.US).apply {
                timeZone = TimeZone.getTimeZone("UTC")
            }
        },
        // 2025-11-09T18:20:04Z
        object : ThreadLocal<SimpleDateFormat>() {
            override fun initialValue() = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'", Locale.US).apply {
                timeZone = TimeZone.getTimeZone("UTC")
            }
        },
        object : ThreadLocal<SimpleDateFormat>() {
            override fun initialValue() = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ", Locale.US).apply {
                timeZone = TimeZone.getTimeZone("UTC")
            }
        },
        object : ThreadLocal<SimpleDateFormat>() {
            override fun initialValue() = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssZ", Locale.US).apply {
                timeZone = TimeZone.getTimeZone("UTC")
            }
        }
    )

    fun nowIsoUtcMillis(): String = mainIsoUtc.get().format(Date())

    fun toIsoUtcMillis(date: Date): String = mainIsoUtc.get().format(date)

    fun parseIsoToDate(s: String?): Date? {
        if (s.isNullOrBlank()) return null
        val normalized = normalizeOffset(s)
        for (fmt in parsers) {
            try {
                val d = fmt.get().parse(normalized)
                if (d != null) return d
            } catch (_: ParseException) { /* intenta siguiente */ }
        }
        return null
    }

    fun parseIsoToEpochMillis(s: String?): Long? = parseIsoToDate(s)?.time

    private fun normalizeOffset(s: String): String =
        s.replace(Regex("(\\+|\\-)(\\d{2}):(\\d{2})$"), "$1$2$3")
}
