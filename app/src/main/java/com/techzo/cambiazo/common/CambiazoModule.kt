package com.techzo.cambiazo.common

import com.techzo.cambiazo.data.remote.auth.AuthService
import com.techzo.cambiazo.data.remote.products.ProductCategoryService
import com.techzo.cambiazo.data.remote.products.ProductService
import com.techzo.cambiazo.data.repository.AuthRepository
import com.techzo.cambiazo.data.repository.ProductCategoryRepository
import com.techzo.cambiazo.data.repository.ProductRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object CambiazoModule {


    @Provides
    @Singleton
    fun provideAuthService(): AuthService{
        return Retrofit.Builder().baseUrl(Constants.BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build().create(AuthService::class.java)
    }


    @Provides
    @Singleton
    fun provideProductService(): ProductService{
        return Retrofit.Builder().baseUrl(Constants.BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build().create(ProductService::class.java)
    }

    @Provides
    @Singleton
    fun provideProductCategoryService(): ProductCategoryService {
        return Retrofit.Builder().baseUrl(Constants.BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build().create(ProductCategoryService::class.java)
    }

    @Provides
    @Singleton
    fun provideAuthRepository(service: AuthService): AuthRepository {
        return AuthRepository(service)
    }

    @Provides
    @Singleton
    fun provideProductRepository(service: ProductService): ProductRepository {
        return ProductRepository(service)
    }

    @Provides
    @Singleton
    fun provideProductCategoryRepository(service: ProductCategoryService): ProductCategoryRepository {
        return ProductCategoryRepository(service)
    }

}