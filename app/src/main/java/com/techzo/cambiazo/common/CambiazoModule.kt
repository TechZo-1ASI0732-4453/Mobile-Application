package com.techzo.cambiazo.common

import com.techzo.cambiazo.data.remote.auth.AuthService
import com.techzo.cambiazo.data.remote.auth.UserService
import com.techzo.cambiazo.data.remote.exchanges.ExchangeService
import com.techzo.cambiazo.data.remote.location.DepartmentService
import com.techzo.cambiazo.data.remote.location.DistrictService
import com.techzo.cambiazo.data.remote.products.FavoriteProductService
import com.techzo.cambiazo.data.remote.location.CountryService
import com.techzo.cambiazo.data.remote.location.DepartmentService
import com.techzo.cambiazo.data.remote.location.DistrictService
import com.techzo.cambiazo.data.remote.products.ProductCategoryService
import com.techzo.cambiazo.data.remote.products.ProductService
import com.techzo.cambiazo.data.remote.reviews.ReviewService
import com.techzo.cambiazo.data.repository.AuthRepository
import com.techzo.cambiazo.data.repository.DetailsRepository
import com.techzo.cambiazo.data.repository.ExchangeRepository
import com.techzo.cambiazo.data.repository.LocationRepository
import com.techzo.cambiazo.data.repository.ProductCategoryRepository
import com.techzo.cambiazo.data.repository.ProductRepository
import com.techzo.cambiazo.data.repository.ReviewRepository
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

    @Provides
    @Singleton
    fun provideReviewService(retrofit: Retrofit): ReviewService {
        return retrofit.create(ReviewService::class.java)
    }

    @Provides
    @Singleton
    fun provideDistrictService(retrofit: Retrofit): DistrictService {
        return retrofit.create(DistrictService::class.java)

    @Provides
    @Singleton
    fun provideCountryService(retrofit: Retrofit): CountryService {
        return retrofit.create(CountryService::class.java)
    }

    @Provides
    @Singleton
    fun provideDepartmentService(retrofit: Retrofit): DepartmentService {
        return retrofit.create(DepartmentService::class.java)
    }

    @Provides
    @Singleton
    fun provideFavoriteProductService(retrofit: Retrofit): FavoriteProductService {
        return retrofit.create(FavoriteProductService::class.java)
        
    @Provides
    @Singleton    
    fun provideDistrictService(retrofit: Retrofit): DistrictService {
        return retrofit.create(DistrictService::class.java)
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

    @Provides
    @Singleton
    fun provideDetailsRepository(productService: ProductService, userService: UserService, reviewService: ReviewService, categoryService: ProductCategoryService, districtService: DistrictService, departmentService: DepartmentService, favoriteProductService: FavoriteProductService): DetailsRepository {
        return DetailsRepository(productService, userService, reviewService, categoryService, districtService, departmentService, favoriteProductService)
    }

    @Provides
    @Singleton
    fun provideReviewRepository(service: ReviewService): ReviewRepository {
        return ReviewRepository(service)
    }
    
    @Provides
    @Singleton
    fun provideLocationRepository(countryService: CountryService,
                                 departmentService: DepartmentService,
                                 districtService: DistrictService): LocationRepository {
        return LocationRepository(countryService,departmentService,districtService)
    }







}