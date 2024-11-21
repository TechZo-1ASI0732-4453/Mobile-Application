package com.techzo.cambiazo.presentation.auth

import android.content.Context
import android.content.Intent
import android.util.Log
import androidx.compose.runtime.State
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
import com.techzo.cambiazo.common.UIState
import com.techzo.cambiazo.common.UserPreferences
import com.techzo.cambiazo.data.repository.AuthRepository
import com.techzo.cambiazo.data.repository.SubscriptionRepository
import com.techzo.cambiazo.data.repository.UserRepository
import com.techzo.cambiazo.domain.Subscription
import com.techzo.cambiazo.domain.UserSignIn
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

@HiltViewModel
class GoogleAuthViewModel @Inject constructor(
    private val authRepository: AuthRepository,
    private val userRepository: UserRepository,
    private val userPreferences: UserPreferences,
    private val subscriptionRepository: SubscriptionRepository
) : ViewModel() {

    private val _subscription = mutableStateOf(UIState<Subscription>())
    val subscription: State<UIState<Subscription>> get() = _subscription

    private val _state = mutableStateOf(UIState<UserSignIn>())
    val state: State<UIState<UserSignIn>> get() = _state


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
                signInResult.data?.let{
                    userPreferences.saveUserSession(it.id, it.username, it.name, it.phoneNumber, it.profilePicture, it.token, it.isGoogleAccount)
                    Constants.token = it.token
                    Constants.user = it
                    Constants.userSubscription = getUserSubscription(it.id)
                    onResult(true, false)
                } ?: run {
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
                    roles = listOf("ROLE_USER"),
                    isGoogleAccount = true
                )

                if (signUpResult is Resource.Success) {
                    val signInResult = authRepository.signIn(email, firebaseUid)
                    signInResult.data?.let{
                        userPreferences.saveUserSession(it.id, it.username, it.name, it.phoneNumber, it.profilePicture, it.token, it.isGoogleAccount)
                        Constants.token = it.token
                        Constants.user = it
                        Constants.userSubscription = getUserSubscription(it.id)
                        onComplete()
                    }
                }
            } catch (e: Exception) {
                Log.e("GoogleAuthViewModel", "Error during registration completion", e)
            }
        }
    }

    private suspend fun getUserSubscription(userId: Int): Subscription? {
        _subscription.value = UIState(isLoading = true)
        val result = subscriptionRepository.getSubscriptionByUserId(userId)
        return if (result is Resource.Success) {
            _subscription.value = UIState(data = result.data)
            result.data
        } else {
            _subscription.value = UIState(message = "Datos del usuario incorrectos")
            null
        }
    }
}