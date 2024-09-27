package com.techzo.cambiazo.data.repository

import android.util.Log
import com.techzo.cambiazo.common.Resource
import com.techzo.cambiazo.data.remote.AuthService
import com.techzo.cambiazo.data.remote.SignUpRequestDto
import com.techzo.cambiazo.data.remote.UserRequestDto
import com.techzo.cambiazo.data.remote.toUser
import com.techzo.cambiazo.data.remote.toUserSignUp
import com.techzo.cambiazo.domain.model.User
import com.techzo.cambiazo.domain.model.UserSignUp
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class AuthRepository(private val authService: AuthService) {
    suspend fun signIn(username: String, password: String): Resource<User> = withContext(Dispatchers.IO){
        try{
            val response = authService.signIn(UserRequestDto(username, password)).execute()
            if(response.isSuccessful){
                val user = response.body()?.toUser()
                Log.d("user", "Response: ${response}")
                if(user != null){
                    return@withContext Resource.Success(user)
                }
                return@withContext Resource.Error("Usuario no encontrado")
            }
            Log.d("user", "Username: ${username}, Password: ${password}")
            return@withContext Resource.Error(response.message())
        }catch (e: Exception){
            return@withContext Resource.Error(e.message ?: "Ocurrió un error")
        }
    }

    suspend fun signUp(username: String, password: String, name: String, phoneNumber: String, profilePicture: String, roles: List<String>): Resource<UserSignUp> = withContext(Dispatchers.IO){
        try{
            val response = authService.signUp(SignUpRequestDto(username, password, name, phoneNumber, profilePicture, roles)).execute()
            if(response.isSuccessful){
                val user = response.body()?.toUserSignUp()
                Log.d("user", "Response: ${response}")
                if(user != null){
                    return@withContext Resource.Success(user)
                }
                return@withContext Resource.Error("Usuario no encontrado")
            }
            Log.d("user", "Username: ${username}, Password: ${password}, Name: ${name}, Phone Number: ${phoneNumber}, Profile Picture: ${profilePicture}, Roles: ${roles}")
            return@withContext Resource.Error(response.message())
        }catch (e: Exception){
            return@withContext Resource.Error(e.message ?: "Ocurrió un error")
        }
    }
}