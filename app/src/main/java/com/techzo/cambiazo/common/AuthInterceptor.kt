package com.techzo.cambiazo.common

import okhttp3.Interceptor
import okhttp3.Response

class AuthInterceptor(private val tokenProvider: () -> String?) : Interceptor {

    private val openPaths = setOf(
        "/authentication/sign-in",
        "/authentication/sign-up",
        "/users/username"
    )

    override fun intercept(chain: Interceptor.Chain): Response {
        val req = chain.request()
        val path = req.url.encodedPath
        val needsAuth = openPaths.none { path.startsWith(it, true) }

        val newReq = if (needsAuth) {
            val token = tokenProvider().orEmpty()
            if (token.isNotBlank())
                req.newBuilder().addHeader("Authorization", "Bearer $token").build()
            else req
        } else req

        return chain.proceed(newReq)
    }
}