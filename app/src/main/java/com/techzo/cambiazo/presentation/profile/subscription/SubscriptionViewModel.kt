package com.techzo.cambiazo.presentation.profile.subscription

import android.content.Intent
import android.os.Handler
import android.os.Looper
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
import com.techzo.cambiazo.data.remote.invoice.CreateInvoicePayload
import com.techzo.cambiazo.data.remote.subscriptions.SubscriptionRequestDto
import com.techzo.cambiazo.data.repository.InvoiceRepository
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
    private val subscriptionRepository: SubscriptionRepository,
    private val invoiceRepository: InvoiceRepository
): ViewModel() {

    private val _state = mutableStateOf(UIState<List<Plan>>())
    val state: State<UIState<List<Plan>>> = _state

    private val _newSubscription = mutableStateOf(UIState<SubscriptionResponse>())
    val newSubscription: State<UIState<SubscriptionResponse>> = _newSubscription

    private val _subscription = mutableStateOf(Constants.userSubscription!!)
    val subscription: State<Subscription> get() = _subscription
    private val _selectedPlan = mutableStateOf<Int?>(null)
    val selectedPlan: State<Int?> get() = _selectedPlan

    // PayPal
    private val clientID = "AWoxCShd6JWv2hDNHIKP9x3DjypVC9f19TC3zZt8ou7H_KjMJQx2pOFUHgi9-bJOKG7fZmN5v1HJYfy-"
    private val secretID = "EBNZWHb4IzRmpMWGEEKdJb0tMQk4K7qSYTY-W0W75fSwWdaweVj3Gl_tdem2ZqqZ_oMub1VMCsROrfpN"
    private val returnUrl = "com.techzo.cambiazo://paypalpay"
    private var planIdPending: Int? = null
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
                    _subscription.value = newSubscription
                    Constants.userSubscription = newSubscription
                }

                _newSubscription.value = UIState(data = result.data)

                getPlans()
            } else {
                _newSubscription.value = UIState(message = result.message ?: "Error al crear la suscripción")
            }
        }
    }

    fun cancelSubscription() {
        createSubscription(1)
    }

    private suspend fun obtenerIdSubscripcionActual(userId: Int): Int? {
        val result = subscriptionRepository.getSubscriptionByUserId(userId)
        return if (result is Resource.Success) {
            result.data?.id
        } else {
            Log.e("PayPal", "Error al obtener la suscripción actual: ${result.message}")
            null
        }
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
                    Log.e("PayPal", "onPayPalWebFailure: $error")
                }

                override fun onPayPalWebCanceled() {
                    Log.w("PayPal", "onPayPalWebCanceled: esperando redirección...")

                    Handler(Looper.getMainLooper()).postDelayed({
                        Log.w("PayPal", "Verifica si se disparó onNewIntent correctamente")
                    }, 1500)
                }

            }

            orderid = orderID
            val payPalWebCheckoutRequest =
                PayPalWebCheckoutRequest(orderID, fundingSource = PayPalWebCheckoutFundingSource.PAYPAL)
            payPalWebCheckoutClient.start(payPalWebCheckoutRequest)
        }
    }

    fun refreshSubscriptionAndPlans() {
        viewModelScope.launch {
            val result = subscriptionRepository.getSubscriptionByUserId(Constants.user!!.id)
            if (result is Resource.Success && result.data != null) {
                val subscriptionDto = result.data
                val plan = state.value.data?.find { it.id == subscriptionDto.plan.id }

                if (plan != null) {
                    val newSubscription = Subscription(
                        id = subscriptionDto.id,
                        startDate = subscriptionDto.startDate,
                        endDate = subscriptionDto.endDate,
                        state = subscriptionDto.state,
                        userId = subscriptionDto.userId,
                        plan = plan
                    )

                    _subscription.value = newSubscription
                    Constants.userSubscription = newSubscription
                }

                getPlans() // Actualiza la lista de planes
            } else {
                Log.e("Subscription", "Failed to refresh subscription: ${result.message}")
            }
        }
    }

    fun startOrder(activity: FragmentActivity, amount: Double, planId: Int) {
        planIdPending = planId
        savedStateHandle["planIdPending"] = planId
        Constants.selectedPlanId = planId

        viewModelScope.launch(Dispatchers.IO) {
            fetchAccessToken()

            uniqueId = UUID.randomUUID().toString()

            val orderJson = JSONObject().apply {
                put("intent", "CAPTURE")
                put("purchase_units", JSONArray().apply {
                    put(JSONObject().apply {
                        put("reference_id", uniqueId)
                        put("amount", JSONObject().apply {
                            put("currency_code", "USD")
                            put("value", String.format("%.2f", amount))
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

    fun captureOrder(orderId: String, onSuccess: () -> Unit = {}) {
        viewModelScope.launch(Dispatchers.IO) {
            val request = Request.Builder()
                .url("https://api-m.sandbox.paypal.com/v2/checkout/orders/$orderId/capture")
                .post("{}".toRequestBody("application/json".toMediaType()))
                .addHeader("Authorization", "Bearer $accessToken")
                .addHeader("Content-Type", "application/json")
                .build()

            OkHttpClient().newCall(request).execute().use { response ->
                if (response.isSuccessful) {
                    val json = JSONObject(response.body?.string() ?: "")
                    Log.d("PayPal", "Capture success: $json")
                    withContext(Dispatchers.Main) {
                        val planId = planIdPending
                            ?: savedStateHandle.get<Int>("planIdPending")
                            ?: savedStateHandle.get<String>("planId")?.toIntOrNull()
                            ?: selectedPlan.value
                            ?: Constants.selectedPlanId

                        if (planId != null) {
                            createSubscription(planId)
                            val plan = state.value.data?.find { it.id == planId }
                            if (plan != null) {
                                val invoicePayload = CreateInvoicePayload(
                                    totalAmount = plan.price,
                                    concept = "Compra de Suscripción: ${plan.name}",
                                    userId = Constants.user!!.id
                                )
                                viewModelScope.launch {
                                    invoiceRepository.createInvoice(invoicePayload)
                                }
                            }
                            refreshSubscriptionAndPlans()
                            planIdPending = null
                            Constants.selectedPlanId = null
                            onSuccess()
                        }
                    }
                } else {
                    Log.e("PayPal", "Capture failed: ${response.message}")
                }
            }
        }
    }
}