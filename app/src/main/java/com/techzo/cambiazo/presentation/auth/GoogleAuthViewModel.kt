package com.techzo.cambiazo.presentation.auth

import android.content.Context
import android.content.Intent
import android.util.Log
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.firebase.auth.AuthCredential
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.techzo.cambiazo.R
import com.techzo.cambiazo.common.Constants
import com.techzo.cambiazo.common.Resource
import com.techzo.cambiazo.data.repository.AuthRepository
import com.techzo.cambiazo.data.repository.UserRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import javax.inject.Inject

@HiltViewModel
class GoogleAuthViewModel @Inject constructor(
    private val authRepository: AuthRepository,
    private val userRepository: UserRepository
) : ViewModel() {

    private val auth = FirebaseAuth.getInstance()
    val phoneNumber = mutableStateOf("")

    fun onPhoneNumberChange(phone: String) {
        phoneNumber.value = phone
    }

    fun getGoogleSignInIntent(context: Context): Intent {
        val options = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(context.getString(R.string.default_web_client_id))
            .requestEmail()
            .requestProfile()
            .build()
        return GoogleSignIn.getClient(context, options).apply {
            signOut()
        }.signInIntent
    }

    fun handleGoogleSignInResult(data: Intent?, onResult: (AuthCredential?, Boolean) -> Unit) {
        try {
            val account = GoogleSignIn.getSignedInAccountFromIntent(data)
                .getResult(ApiException::class.java)
            val credential = GoogleAuthProvider.getCredential(account.idToken, null)
            onResult(credential, true)
        } catch (e: ApiException) {
            onResult(null, false)
        }
    }

    suspend fun signInWithGoogleCredential(
        credential: AuthCredential,
        onResult: (Boolean, Boolean) -> Unit
    ) {
        try {
            auth.signInWithCredential(credential).await()
            val email = auth.currentUser?.email?.trim()?.lowercase() ?: ""
            val userExists = userRepository.checkIfUserExistsByEmail(email)

            if (userExists) {
                val signInResult = authRepository.signIn(email, auth.currentUser!!.uid)
                if (signInResult is Resource.Success) {
                    Constants.token = signInResult.data?.token
                    Constants.user = signInResult.data
                    onResult(true, false)
                } else {
                    onResult(false, false)
                }
            } else {
                onResult(false, true)
            }
        } catch (e: Exception) {
            onResult(false, false)
        }
    }


    fun completeRegistration(phone: String, onComplete: () -> Unit) {
        viewModelScope.launch {
            try {
                val firebaseUser = auth.currentUser ?: return@launch
                val email = firebaseUser.email ?: ""
                val name = firebaseUser.displayName ?: ""
                val profilePicture = firebaseUser.photoUrl?.toString() ?: ""
                val firebaseUid = firebaseUser.uid

                val signUpResult = authRepository.signUp(
                    username = email,
                    password = firebaseUid,
                    name = name,
                    phoneNumber = phone,
                    profilePicture = profilePicture,
                    roles = listOf("ROLE_USER")
                )

                if (signUpResult is Resource.Success) {
                    val signInResult = authRepository.signIn(email, firebaseUid)
                    if (signInResult is Resource.Success) {
                        Constants.token = signInResult.data?.token
                        Constants.user = signInResult.data
                        onComplete()
                    }
                }
            } catch (e: Exception) {
                Log.e("GoogleAuthViewModel", "Error during registration completion", e)
            }
        }
    }
}
