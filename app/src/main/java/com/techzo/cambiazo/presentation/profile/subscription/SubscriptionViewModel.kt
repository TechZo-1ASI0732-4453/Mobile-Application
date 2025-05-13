package com.techzo.cambiazo.presentation.profile.subscription

import android.content.Intent
import android.util.Base64
import android.util.Log
import android.widget.Toast
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.paypal.android.corepayments.CoreConfig
import com.paypal.android.corepayments.Environment
import com.paypal.android.corepayments.PayPalSDKError
import com.paypal.android.paypalwebpayments.PayPalWebCheckoutClient
import com.paypal.android.paypalwebpayments.PayPalWebCheckoutFundingSource
import com.paypal.android.paypalwebpayments.PayPalWebCheckoutListener
import com.paypal.android.paypalwebpayments.PayPalWebCheckoutRequest
import com.paypal.android.paypalwebpayments.PayPalWebCheckoutResult
import com.techzo.cambiazo.common.Constants
import com.techzo.cambiazo.common.Resource
import com.techzo.cambiazo.common.UIState
import com.techzo.cambiazo.data.remote.subscriptions.SubscriptionRequestDto
import com.techzo.cambiazo.data.repository.SubscriptionRepository
import com.techzo.cambiazo.domain.Plan
import com.techzo.cambiazo.domain.Subscription
import com.techzo.cambiazo.domain.SubscriptionResponse
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONArray
import org.json.JSONObject
import java.util.UUID
import javax.inject.Inject

@HiltViewModel
class SubscriptionViewModel @Inject constructor(
    private val savedStateHandle: SavedStateHandle,
    private val subscriptionRepository: SubscriptionRepository
): ViewModel() {

    private val _state = mutableStateOf(UIState<List<Plan>>())
    val state: State<UIState<List<Plan>>> = _state

    private val _newSubscription = mutableStateOf(UIState<SubscriptionResponse>())
    val newSubscription: State<UIState<SubscriptionResponse>> = _newSubscription

    private val _subscription = mutableStateOf(Constants.userSubscription!!)
    val subscription: State<Subscription> get() = _subscription

    private fun updateSubscription(updatedSubscription: Subscription) {
        _subscription.value = updatedSubscription
        Constants.userSubscription = updatedSubscription
    }

    private val _selectedPlan = mutableStateOf<Int?>(null)
    val selectedPlan: State<Int?> get() = _selectedPlan

    // PayPal
    private val clientID = "AT_QbZW3G_KXxMuv-5-p7J3CtSUHWvw-LwtMI09GB1tsDmcarNRY5Gu_Hb02bCKibTfUydx6dAy997wX"
    private val secretID = "EHB1ejyJQBoMD5h6m-vm6-XdF8CZ0sBefWyj_deErYyYDd-BhdqeREl-uQOfeStCstCnUhFHqRNpEKhv"
    private val returnUrl = "com.techzo.cambiazo://paypalpay"
    var accessToken: String = ""
    var orderId: String = ""
    private var orderid = ""
    private lateinit var uniqueId: String


    init {
        getPlans()
        val planIdString: String? = savedStateHandle["planId"]
        val planId = planIdString?.toIntOrNull()
        if (planId != null) {
            _selectedPlan.value = planId
        }
        fetchAccessToken()
    }

    private fun getPlans() {
        _state.value = UIState(isLoading = true)
        viewModelScope.launch {
            val result = subscriptionRepository.getPlans()
            if (result is Resource.Success) {
                _state.value = UIState(data = result.data ?: emptyList(), isLoading = false)
            } else {
                _state.value = UIState(message = result.message ?: "Ocurrió un error")
            }
        }
    }

    fun createSubscription(planId: Int) {
        viewModelScope.launch {
            val subscriptionRequest = SubscriptionRequestDto(
                state = "Activo",
                userId = Constants.user!!.id,
                planId = planId
            )

            val result = subscriptionRepository.createSubscription(subscriptionRequest)

            if (result is Resource.Success) {
                val newPlan = state.value.data?.find { it.id == planId }

                val newSubscription = newPlan?.let {
                    Subscription(
                        id = result.data?.id ?: 0,
                        startDate = result.data?.startDate ?: "",
                        endDate = result.data?.endDate ?: "",
                        state = result.data?.state ?: "",
                        userId = result.data?.userId ?: 0,
                        plan = it,
                    )
                }

                if (newSubscription != null) {
                    updateSubscription(newSubscription)
                }

                _newSubscription.value = UIState(data = result.data)
            } else {
                _newSubscription.value = UIState(message = result.message ?: "Error al crear la suscripción")
            }
        }
    }

    fun cancelSubscription() {
        createSubscription(1)
    }

    // --- PayPal functions ---

    private suspend fun handlerOrderID(orderID: String, activity: FragmentActivity) {
        withContext(Dispatchers.Main) {
            val config = CoreConfig(clientID, environment = Environment.SANDBOX)
            val payPalWebCheckoutClient = PayPalWebCheckoutClient(activity, config, returnUrl)
            payPalWebCheckoutClient.listener = object : PayPalWebCheckoutListener {
                override fun onPayPalWebSuccess(result: PayPalWebCheckoutResult) {
                    Log.d("PayPal", "onPayPalWebSuccess: $result")
                }

                override fun onPayPalWebFailure(error: PayPalSDKError) {
                    Log.d("PayPal", "onPayPalWebFailure: $error")
                }

                override fun onPayPalWebCanceled() {
                    Log.d("PayPal", "onPayPalWebCanceled: ")
                }
            }

            orderid = orderID
            val payPalWebCheckoutRequest =
                PayPalWebCheckoutRequest(orderID, fundingSource = PayPalWebCheckoutFundingSource.PAYPAL)
            payPalWebCheckoutClient.start(payPalWebCheckoutRequest)
        }
    }

    fun startOrder(activity: FragmentActivity) {
        viewModelScope.launch(Dispatchers.IO) {
            uniqueId = UUID.randomUUID().toString()

            val orderJson = JSONObject().apply {
                put("intent", "CAPTURE")
                put("purchase_units", JSONArray().apply {
                    put(JSONObject().apply {
                        put("reference_id", uniqueId)
                        put("amount", JSONObject().apply {
                            put("currency_code", "USD")
                            put("value", "5.00")
                        })
                    })
                })
                put("payment_source", JSONObject().apply {
                    put("paypal", JSONObject().apply {
                        put("experience_context", JSONObject().apply {
                            put("payment_method_preference", "IMMEDIATE_PAYMENT_REQUIRED")
                            put("brand_name", "CambiaZo")
                            put("locale", "en-US")
                            put("landing_page", "LOGIN")
                            put("shipping_preference", "NO_SHIPPING")
                            put("user_action", "PAY_NOW")
                            put("return_url", returnUrl)
                            put("cancel_url", "https://example.com/cancelUrl")
                        })
                    })
                })
            }

            val body = orderJson.toString().toRequestBody("application/json".toMediaType())

            val request = Request.Builder()
                .url("https://api-m.sandbox.paypal.com/v2/checkout/orders")
                .post(body)
                .addHeader("Authorization", "Bearer $accessToken")
                .addHeader("Content-Type", "application/json")
                .build()

            val client = OkHttpClient()
            client.newCall(request).execute().use { response ->
                if (response.isSuccessful) {
                    val json = JSONObject(response.body?.string() ?: "")
                    orderId = json.getString("id")
                    Log.d("PayPal", "Order ID: $orderId")
                    handlerOrderID(orderId, activity)
                } else {
                    Log.e("PayPal", "Error al crear orden: ${response.message}")
                }
            }
        }
    }

    private fun fetchAccessToken() {
        viewModelScope.launch(Dispatchers.IO) {
            val authString = "$clientID:$secretID"
            val encodedAuth = Base64.encodeToString(authString.toByteArray(), Base64.NO_WRAP)

            val body = "grant_type=client_credentials".toRequestBody("application/x-www-form-urlencoded".toMediaType())
            val request = Request.Builder()
                .url("https://api-m.sandbox.paypal.com/v1/oauth2/token")
                .post(body)
                .addHeader("Authorization", "Basic $encodedAuth")
                .addHeader("Content-Type", "application/x-www-form-urlencoded")
                .build()

            val client = OkHttpClient()
            client.newCall(request).execute().use { response ->
                if (response.isSuccessful) {
                    val json = JSONObject(response.body?.string() ?: "")
                    accessToken = json.getString("access_token")
                    Log.d("PayPal", "Access token: $accessToken")
                } else {
                    Log.e("PayPal", "Error: ${response.message}")
                }
            }
        }
    }


    fun captureOrder(orderId: String) {
        viewModelScope.launch(Dispatchers.IO) {
            val request = Request.Builder()
                .url("https://api-m.sandbox.paypal.com/v2/checkout/orders/$orderId/capture")
                .post("{}".toRequestBody("application/json".toMediaType()))
                .addHeader("Authorization", "Bearer $accessToken")
                .addHeader("Content-Type", "application/json")
                .build()

            val client = OkHttpClient()
            client.newCall(request).execute().use { response ->
                if (response.isSuccessful) {
                    val json = JSONObject(response.body?.string() ?: "")
                    Log.d("PayPal", "Capture success: $json")
                    withContext(Dispatchers.Main) {
                        Log.d("PayPal", "Capture success 2: $json")
                    }
                } else {
                    Log.e("PayPal", "Capture failed: ${response.message}")
                    withContext(Dispatchers.Main) {
                        Log.d("PayPal", "Fallo al captutrar el pago ")
                    }
                }
            }
        }
    }


}
