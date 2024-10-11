package com.techzo.cambiazo.common

import com.techzo.cambiazo.data.remote.auth.AuthService
import com.techzo.cambiazo.data.remote.auth.UserService
import com.techzo.cambiazo.data.remote.exchanges.ExchangeService
import com.techzo.cambiazo.data.remote.products.ProductCategoryService
import com.techzo.cambiazo.data.remote.products.ProductService
import com.techzo.cambiazo.data.repository.AuthRepository
import com.techzo.cambiazo.data.repository.ExchangeRepository
import com.techzo.cambiazo.data.repository.ProductCategoryRepository
import com.techzo.cambiazo.data.repository.ProductRepository
import com.techzo.cambiazo.data.repository.UserRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object CambiazoModule {

    @Provides
    @Singleton
    fun provideAuthInterceptor(): AuthInterceptor {
        return AuthInterceptor{Constants.token?:""}
    }

    @Provides
    @Singleton
    fun provideOkHttpClient(authInterceptor: AuthInterceptor): OkHttpClient {
        return OkHttpClient
            .Builder()
            .addInterceptor(authInterceptor)
            .build()
    }

    @Provides
    @Singleton
    fun provideRetrofit(okHttpClient: OkHttpClient): Retrofit {
        return Retrofit.Builder()
            .baseUrl(Constants.BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .client(okHttpClient)
            .build()
    }



    // AQUI SOLO AGREGAR LOS PROVIDES DE LOS SERVICIOS

    @Provides
    @Singleton
    fun provideAuthService(retrofit: Retrofit): AuthService{
        return retrofit.create(AuthService::class.java)
    }


    @Provides
    @Singleton
    fun provideProductService(retrofit: Retrofit): ProductService{
        return retrofit.create(ProductService::class.java)
    }

    @Provides
    @Singleton
    fun provideProductCategoryService(retrofit: Retrofit): ProductCategoryService {
        return retrofit.create(ProductCategoryService::class.java)
    }

    @Provides
    @Singleton
    fun provideExchangeService(retrofit: Retrofit): ExchangeService {
        return retrofit.create(ExchangeService::class.java)
    }

    @Provides
    @Singleton
    fun provideUserService(retrofit: Retrofit): UserService {
        return retrofit.create(UserService::class.java)
    }


    // AQUI SOLO AGREGAR LOS PROVIDES DE LOS REPOSITORIOS

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


    @Provides
    @Singleton
    fun provideExchangeRepository(service: ExchangeService, productRepository: ProductRepository, userRepository: UserRepository): ExchangeRepository {
        return ExchangeRepository(service, productRepository, userRepository)
    }

    @Provides
    @Singleton
    fun provideUserRepository(service: UserService): UserRepository {
        return UserRepository(service)
    }

}