package com.techzo.cambiazo.data.repository


import com.techzo.cambiazo.common.Resource
import com.techzo.cambiazo.data.remote.auth.UserService
import com.techzo.cambiazo.data.remote.auth.toUser
import com.techzo.cambiazo.domain.User
import com.techzo.cambiazo.domain.UserEdit
import com.techzo.cambiazo.domain.UserSignIn
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class UserRepository(private val userService: UserService) {

    suspend fun getUsers(): Resource<List<User>> = withContext(Dispatchers.IO) {
        try{
            val response = userService.getUsers()
            if(response.isSuccessful){
                response.body()?.let { usersDto ->
                    val users = mutableListOf<User>()
                    usersDto.forEach { userDto ->
                        users.add(userDto.toUser())
                    }
                    return@withContext Resource.Success(data = users)
                }
                return@withContext Resource.Error("No se encontraron usuarios")
            }
            return@withContext Resource.Error(response.message())
        }catch (e: Exception){
            return@withContext Resource.Error(e.message ?: "Ocurrió un error")
        }
    }

    suspend fun getUserById(id: Int): Resource<User> = withContext(Dispatchers.IO) {
        try{
            val response = userService.getUserById(id)
            if(response.isSuccessful){
                response.body()?.let { userDto ->
                    return@withContext Resource.Success(data = userDto.toUser())
                }
                return@withContext Resource.Error("No se encontró el usuario")
            }
            return@withContext Resource.Error(response.message())
        }catch (e: Exception){
            return@withContext Resource.Error(e.message ?: "Ocurrió un error")
        }
    }

    suspend fun updateUserById(id: Int, user: UserEdit): Resource<UserSignIn> = withContext(Dispatchers.IO) {
        try {
            val response = userService.updateUserById(id, user)
            if (response.isSuccessful) {
                response.body()?.let { userSignIn ->
                    return@withContext Resource.Success(data = userSignIn)
                }
                return@withContext Resource.Error("No se encontró el usuario")
            }
            return@withContext Resource.Error(response.message())
        } catch (e: Exception) {
            return@withContext Resource.Error(e.message ?: "Ocurrió un error")
        }
    }
}