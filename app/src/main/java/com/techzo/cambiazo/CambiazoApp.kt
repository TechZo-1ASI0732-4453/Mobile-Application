package com.techzo.cambiazo

import android.app.Application
import com.paypal.checkout.Checkout
import com.paypal.checkout.config.CheckoutConfig
import com.paypal.checkout.config.Environment
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class CambiazoApp : Application() {
    override fun onCreate() {
        super.onCreate()
        Checkout.setConfig(
            CheckoutConfig(
                application = this,
                clientId = "TU_CLIENT_ID",
                environment = Environment.SANDBOX,
                returnUrl = "com.techzo.cambiazo://paypalpay"
            )
        )
    }
}
