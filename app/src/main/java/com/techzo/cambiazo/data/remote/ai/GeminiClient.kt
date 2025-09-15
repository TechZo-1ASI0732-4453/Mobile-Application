package com.techzo.cambiazo.data.remote.replicate

import com.techzo.cambiazo.data.remote.ai.GeminiApi
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.scalars.ScalarsConverterFactory
import java.util.concurrent.TimeUnit

object GeminiClient {

    fun create(
        baseUrl: String = "https://api.replicate.com/v1/",
        userAgent: String = "Cambiazo/1.0 (Android)"
    ): GeminiApi {

        val headers = Interceptor { chain ->
            val req = chain.request().newBuilder()
                .header("User-Agent", userAgent)
                .build()
            chain.proceed(req)
        }

        val log = HttpLoggingInterceptor().apply {
            // Evita volcado de binarios y claves en prod
            level = if (isDebugBuild()) HttpLoggingInterceptor.Level.BASIC
            else HttpLoggingInterceptor.Level.NONE
        }

        val client = OkHttpClient.Builder()
            .retryOnConnectionFailure(true)
            .addInterceptor(headers)
            .addInterceptor(log)
            .connectTimeout(20, TimeUnit.SECONDS)
            .readTimeout(60, TimeUnit.SECONDS)
            .writeTimeout(60, TimeUnit.SECONDS)
            .callTimeout(90, TimeUnit.SECONDS)
            .build()

        return Retrofit.Builder()
            .baseUrl(baseUrl)
            .addConverterFactory(ScalarsConverterFactory.create()) // devolvemos String crudo
            .client(client)
            .build()
            .create(GeminiApi::class.java)
    }

    private fun isDebugBuild(): Boolean {
        return try {
            // Evita dependencia directa a BuildConfig si el módulo no la expone
            Class.forName("com.techzo.cambiazo.BuildConfig")
                .getField("DEBUG")
                .getBoolean(null)
        } catch (_: Throwable) {
            false
        }
    }
}
