import java.util.Properties

plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.google.dagger.hilt.android)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.compose)
    kotlin("kapt")

}

android {
    namespace = "com.example.horizon"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.example.horizon"
        minSdk = 24
        targetSdk = 35
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        vectorDrawables {
            useSupportLibrary = true
        }

        buildFeatures {
            compose = true
            buildConfig = true
        }

        // Load the API Key form local.properties file and make it available as a BuildConfig Field
        val properties = Properties()
        properties.load(project.rootProject.file("local.properties").inputStream())
        val apiKey = properties.getProperty("HORIZON_API_KEY")
        buildConfigField(
            type = "String",
            name = "OPEN_WEATHER_MAP_API_KEY",
            value = "\"$apiKey\""
        )
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

    composeOptions {
        kotlinCompilerExtensionVersion = "1.5.3"
    }

    buildTypes {
        getByName("release") {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }
}

dependencies {
    // Core
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)

    // Compose
    implementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(platform(libs.androidx.compose.bom))

    implementation(libs.androidx.material3)
    implementation(libs.androidx.activity.compose)
    implementation(libs.androidx.ui.tooling.preview)
    implementation(libs.androidx.lifecycle.viewmodel.compose)
    implementation(libs.androidx.navigation.compose)
    debugImplementation(libs.androidx.ui.tooling)
    debugImplementation(libs.androidx.ui.test.manifest)
    androidTestImplementation(libs.androidx.ui.test.junit4)

    // Play Location Services
    implementation(libs.play.services.location)

    // Kotlin Coroutines Play Services
    implementation(libs.kotlinx.coroutines.play.services)

    // Hilt
    implementation(libs.google.dagger.hilt.android)
    implementation(libs.androidx.hilt.navigation.compose)
    kapt(libs.google.dagger.hilt.compiler)

    // Logging
    implementation(libs.jakewharton.timber)

    implementation(libs.androidx.ui)
    implementation(libs.androidx.ui.graphics)

    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)



}