package com.techzo.cambiazo.common

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import androidx.core.graphics.scale
import java.io.ByteArrayOutputStream
import java.io.InputStream
import kotlin.math.max
import kotlin.math.min
import kotlin.math.roundToInt

object ImageBytes {

    fun fromUriJpeg(
        context: Context,
        uri: android.net.Uri,
        maxSide: Int = 512,
        qualityJpeg: Int = 70
    ): ByteArray {
        val cr = context.contentResolver

        // 1) Leer sólo bounds
        var input: InputStream? = null
        val bounds = BitmapFactory.Options().apply { inJustDecodeBounds = true }
        try {
            input = cr.openInputStream(uri)
            BitmapFactory.decodeStream(input, null, bounds)
        } finally {
            try { input?.close() } catch (_: Throwable) {}
        }

        // 2) Decodificar con sample
        val opts = BitmapFactory.Options().apply {
            inJustDecodeBounds = false
            inSampleSize = calcInSampleSize(bounds.outWidth, bounds.outHeight, maxSide, maxSide)
        }
        val bmp: Bitmap = cr.openInputStream(uri)?.use { BitmapFactory.decodeStream(it, null, opts) }
            ?: throw IllegalStateException("No se pudo decodificar la imagen")

        // 3) Redimensionar al maxSide
        val w = bmp.width
        val h = bmp.height
        val scale = min(maxSide / max(w, h).toFloat(), 1f)
        val resized = if (scale < 1f) {
            val nw = (w * scale).roundToInt()
            val nh = (h * scale).roundToInt()
            bmp.scale(nw, nh)
        } else bmp

        // 4) JPEG a bytes
        val baos = ByteArrayOutputStream()
        resized.compress(Bitmap.CompressFormat.JPEG, qualityJpeg, baos)

        // Evita leaks de memoria
        if (resized !== bmp) bmp.recycle()

        return baos.toByteArray()
    }

    private fun calcInSampleSize(srcW: Int, srcH: Int, reqW: Int, reqH: Int): Int {
        var inSample = 1
        if (srcH > reqH || srcW > reqW) {
            var halfH = srcH / 2
            var halfW = srcW / 2
            while ((halfH / inSample) >= reqH && (halfW / inSample) >= reqW) {
                inSample *= 2
            }
        }
        return inSample
    }
}
