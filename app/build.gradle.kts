plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)

    alias(libs.plugins.googleServices)
}

android {
    namespace = "com.example.myfloraapp"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.example.myfloraapp"
        minSdk = 29
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
        kotlinCompilerExtensionVersion = "1.5.1"
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
    //Navigation
    implementation(libs.androidx.navigation.compose)
    //Firebase
    implementation(platform(libs.firebase.bom))
    //Firebase Auth
    implementation(libs.firebase.auth)
    // ViewModel
    implementation("androidx.lifecycle:lifecycle-viewmodel-ktx:2.5.1")
    // LiveData
    implementation("androidx.compose.runtime:runtime-livedata:1.3.2")
    implementation(libs.google.firebase.firestore.ktx)
    //Firebase Auth
    //implementation(libs.firebase.auth.ktx)
    // Trasnform JSON
    implementation("com.google.code.gson:gson:2.10.1")
    //hoy
    implementation ("com.google.android.gms:play-services-base:18.1.0")
    implementation ("com.google.firebase:firebase-core:21.1.1")
    //localización
    implementation("com.google.android.gms:play-services-location:21.0.1")

    // Retrofit + Gson
    implementation ("com.squareup.retrofit2:retrofit:2.9.0")
    // Retrofit + Gson
    implementation ("com.squareup.retrofit2:converter-gson:2.9.0")
    // Coroutines
    implementation ("org.jetbrains.kotlinx:kotlinx-coroutines-android:1.6.4")
    //Manejo de fechas
    implementation("org.jetbrains.kotlinx:kotlinx-datetime:0.4.1")
    //iconos meteorologicos
    implementation("androidx.compose.material:material-icons-extended:1.6.0")
    //Coil para cargar imagenes
    implementation ("io.coil-kt:coil-compose:2.6.0")

    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.ui.test.junit4)
    debugImplementation(libs.androidx.ui.tooling)
    debugImplementation(libs.androidx.ui.test.manifest)
    //Hilt
    implementation("androidx.hilt:hilt-navigation-compose:1.2.0")
    //Iconos extendidos
    implementation("androidx.compose.material:material-icons-extended:1.7.8")
    implementation ("com.google.android.gms:play-services-base:18.2.0")
    implementation ("com.google.android.gms:play-services-auth:20.7.0")
}