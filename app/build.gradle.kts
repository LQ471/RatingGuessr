plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
}

android {
    namespace = "com.example.ratingguessr"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.example.ratingguessr"
        minSdk = 24
        targetSdk = 35
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
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
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
    kotlinOptions {
        jvmTarget = "11"
    }
    buildFeatures {
        viewBinding = true
    }
}

dependencies {
    // Jetpack Compose integration
    implementation(libs.androidx.navigation.compose)
    // Feature module support for Fragments
    implementation(libs.androidx.navigation.dynamic.features.fragment)

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.material)
    implementation(libs.androidx.activity)
    implementation(libs.androidx.constraintlayout)
    implementation(libs.androidx.navigation.fragment.ktx)
    implementation(libs.androidx.navigation.ui.ktx)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)

    // Retrofit - For making HTTP network requests
    implementation(libs.retrofit)

    // Moshi Kotlin - For converting JSON to Kotlin data classes
    implementation(libs.moshi.kotlin)

    // Retrofit Moshi Converter - Lets Retrofit use Moshi for JSON parsing
    implementation(libs.converter.moshi)

    // Coil - For loading and displaying images efficiently in ImageViews
    implementation(libs.coil)
}