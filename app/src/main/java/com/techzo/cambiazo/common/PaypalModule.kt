package com.techzo.cambiazo.common

import com.techzo.cambiazo.data.remote.paypal.PaypalConfig
import com.techzo.cambiazo.data.remote.paypal.PaypalRemoteDataSource
import com.techzo.cambiazo.data.remote.paypal.PaypalService
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Named
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object PaypalModule {

    @Provides @Singleton
    @Named("paypal")
    fun providePaypalRetrofit(): Retrofit =
        Retrofit.Builder()
            .baseUrl("https://api-m.sandbox.paypal.com/")
            .client(OkHttpClient.Builder().build())
            .addConverterFactory(GsonConverterFactory.create())
            .build()

    @Provides @Singleton
    fun providePaypalService(
        @Named("paypal") retrofit: Retrofit
    ): PaypalService =
        retrofit.create(PaypalService::class.java)

    @Provides @Singleton
    fun providePaypalRemoteDataSource(
        paypalService: PaypalService
    ): PaypalRemoteDataSource =
        PaypalRemoteDataSource(
            service  = paypalService,
            clientId = PaypalConfig.CLIENT_ID,
            secret   = PaypalConfig.SECRET
        )
}