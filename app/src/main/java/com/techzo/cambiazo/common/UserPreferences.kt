package com.techzo.cambiazo.common

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private val Context.dataStore by preferencesDataStore("user_preferences")

class UserPreferences(context: Context) {

    private val dataStore = context.dataStore

    companion object {
        val ID_KEY = intPreferencesKey("id")
        val USERNAME_KEY = stringPreferencesKey("username")
        val NAME_KEY = stringPreferencesKey("name")
        val PHONE_NUMBER_KEY = stringPreferencesKey("phone_number")
        val PROFILE_PICTURE_KEY = stringPreferencesKey("profile_picture")
        val TOKEN_KEY = stringPreferencesKey("token")
        val IS_GOOGLE_ACCOUNT_KEY = stringPreferencesKey("is_google_account")
    }

    // Función para guardar los datos de usuario
    suspend fun saveUserSession(id: Int, username: String, name: String, phoneNumber: String, profilePicture: String, token: String, isGoogleAccount: Boolean) {
        dataStore.edit { preferences ->
            preferences[USERNAME_KEY] = username
            preferences[TOKEN_KEY] = token
            preferences[ID_KEY] = id
            preferences[NAME_KEY] = name
            preferences[PHONE_NUMBER_KEY] = phoneNumber
            preferences[PROFILE_PICTURE_KEY] = profilePicture
            preferences[IS_GOOGLE_ACCOUNT_KEY] = isGoogleAccount.toString()
        }
    }

    // Función para obtener el token
    val getToken: Flow<String?> = dataStore.data
        .map { preferences ->
            preferences[TOKEN_KEY]
        }

    // Función para obtener el nombre de usuario
    val getUsername: Flow<String?> = dataStore.data
        .map { preferences ->
            preferences[USERNAME_KEY]
        }

    val getProfilePicture: Flow<String?> = dataStore.data
        .map { preferences ->
            preferences[PROFILE_PICTURE_KEY]
        }

    val getName: Flow<String?> = dataStore.data
        .map { preferences ->
            preferences[NAME_KEY]
        }

    val getPhoneNumber: Flow<String?> = dataStore.data
        .map { preferences ->
            preferences[PHONE_NUMBER_KEY]
        }

    val getId: Flow<Int?> = dataStore.data
        .map { preferences ->
            preferences[ID_KEY]
        }

    val getIsGoogleAccount: Flow<Boolean?> = dataStore.data
        .map { preferences ->
            preferences[IS_GOOGLE_ACCOUNT_KEY]?.toBoolean()
        }


    // Función para eliminar la sesión
    suspend fun clearSession() {
        dataStore.edit { it.clear() }
    }

}