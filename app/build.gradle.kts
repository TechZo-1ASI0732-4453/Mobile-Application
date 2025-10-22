plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.jetbrains.kotlin.android)
    alias(libs.plugins.ksp)
    id("com.google.dagger.hilt.android")
    id("com.google.gms.google-services")
    id("kotlin-parcelize")
    kotlin("kapt")
}

android {
    namespace = "com.techzo.cambiazo"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.techzo.cambiazo"
        minSdk = 24
        targetSdk = 34
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        vectorDrawables {
            useSupportLibrary = true
        }
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
    kotlinOptions {
        jvmTarget = "1.8"
    }
    buildFeatures {
        compose = true
    }
    composeOptions {
        kotlinCompilerExtensionVersion = "1.5.11"
    }
    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }
}

dependencies {
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.activity.compose)
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.ui)
    implementation(libs.androidx.ui.graphics)
    implementation(libs.androidx.ui.tooling.preview)
    implementation(libs.androidx.material3)
    implementation(libs.androidx.navigation.compose)
    implementation(libs.glide)
    implementation (libs.accompanist.swiperefresh)
    implementation(libs.androidx.appcompat)
    implementation(libs.firebase.appcheck.debug)
    implementation(libs.play.services.location)
    implementation(libs.accompanist.permissions)
    implementation(libs.maps.compose)
    implementation(libs.stompprotocolandroid)
    implementation(libs.rxjava2.rxjava)
    implementation(libs.rxjava2.rxandroid)
    annotationProcessor(libs.room.compiler)
    implementation("org.jetbrains.kotlin:kotlin-stdlib")
    implementation("com.google.ai.client.generativeai:generativeai:0.9.0")

    implementation("com.squareup.retrofit2:retrofit:2.11.0")
    implementation("com.squareup.retrofit2:converter-scalars:2.11.0")
    implementation("com.squareup.okhttp3:okhttp:4.12.0")
    implementation("com.squareup.okhttp3:logging-interceptor:4.12.0")

    implementation(platform("com.google.firebase:firebase-bom:33.5.1"))
    implementation("com.google.firebase:firebase-auth-ktx")
    implementation("com.google.android.gms:play-services-auth:20.7.0")
    implementation("com.squareup.okhttp3:logging-interceptor:4.9.0")
    implementation ("com.google.firebase:firebase-functions-ktx:20.2.0")


    // Hilt dependencies
    implementation("com.google.dagger:hilt-android:2.52")
    implementation(libs.androidx.runtime.livedata)
    kapt("com.google.dagger:hilt-compiler:2.52")

    // Firebase additional libraries
    implementation(libs.firebase.storage)
    implementation(libs.firebase.database)
    implementation(libs.firebase.auth)
    implementation(libs.firebase.storage.ktx)

    // Room
    ksp(libs.room.compiler)
    implementation(libs.room.ktx)
    implementation(libs.room.runtime)

    // Retrofit
    implementation(libs.retrofit)
    implementation(libs.converter.gson)

    // Coroutines, icons, and Coil
    implementation(libs.coroutines)
    implementation(libs.androidx.material.icons.extended)
    implementation("io.coil-kt:coil-compose:2.2.2")

    // Hilt Navigation Compose
    implementation(libs.androidx.hilt.navigation.compose)

    // EmailJS
    implementation("com.squareup.okhttp3:okhttp:4.9.3")
    implementation("org.json:json:20211205")

    // Testing
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.ui.test.junit4)
    debugImplementation(libs.androidx.ui.tooling)
    debugImplementation(libs.androidx.ui.test.manifest)
    implementation(libs.hilt.android)
    kapt(libs.hilt.compiler)
    implementation(libs.androidx.hilt.navigation.compose)
    implementation(libs.datastore.preferences)
    implementation ("androidx.core:core-splashscreen:1.0.1")
    implementation("com.google.firebase:firebase-appcheck-debug:17.1.1")
    implementation("com.google.android.play:integrity:1.3.0")

    //Paypal
    implementation ("com.paypal.android:paypal-web-payments:1.5.0")
    implementation("com.google.firebase:firebase-appcheck-debug:17.1.1")

}
