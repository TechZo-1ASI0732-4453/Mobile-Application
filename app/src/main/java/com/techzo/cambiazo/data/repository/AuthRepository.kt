package com.techzo.cambiazo.data.repository

import android.util.Log
import com.techzo.cambiazo.common.Resource
import com.techzo.cambiazo.data.remote.auth.AuthService
import com.techzo.cambiazo.data.remote.signup.SignUpRequestDto
import com.techzo.cambiazo.data.remote.auth.UserSignInRequestDto
import com.techzo.cambiazo.data.remote.auth.toUserSignIn
import com.techzo.cambiazo.data.remote.signup.toUserSignUp
import com.techzo.cambiazo.domain.UserSignIn
import com.techzo.cambiazo.domain.UserSignUp
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class AuthRepository(private val authService: AuthService) {
    suspend fun signIn(username: String, password: String): Resource<UserSignIn> = withContext(Dispatchers.IO){
        try{
            val response = authService.signIn(UserSignInRequestDto(username, password)).execute()
            if(response.isSuccessful){
                val user = response.body()?.toUserSignIn()
                if(user != null){
                    return@withContext Resource.Success(user)
                }
                return@withContext Resource.Error("Usuario no encontrado")
            }
            return@withContext Resource.Error(response.message())
        }catch (e: Exception){
            return@withContext Resource.Error(e.message ?: "Ocurrió un error")
        }
    }

    suspend fun signUp(
        username: String,
        password: String,
        name: String,
        phoneNumber: String,
        profilePicture: String,
        roles: List<String>,
        isGoogleAccount: Boolean // Nuevo parámetro para diferenciar registros
    ): Resource<UserSignUp> = withContext(Dispatchers.IO) {
        try {
            // Actualización del DTO con el nuevo campo
            val response = authService.signUp(
                SignUpRequestDto(
                    username = username,
                    password = password,
                    name = name,
                    phoneNumber = phoneNumber,
                    profilePicture = profilePicture,
                    roles = roles,
                    isGoogleAccount = isGoogleAccount
                )
            ).execute()

            if (response.isSuccessful) {
                val user = response.body()?.toUserSignUp()
                if (user != null) {
                    return@withContext Resource.Success(user)
                }
                return@withContext Resource.Error("Usuario no encontrado")
            }

            return@withContext Resource.Error(response.message())
        } catch (e: Exception) {
            return@withContext Resource.Error(e.message ?: "Ocurrió un error")
        }
    }

}