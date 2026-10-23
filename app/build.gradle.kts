plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.compose)
    //aqui
    alias(libs.plugins.ksp)  // <--- Incorporar
}

android {
    namespace = "com.example.appventas"
    compileSdk {
        version = release(37)
    }

    defaultConfig {
        applicationId = "com.example.appventas"
        minSdk = 24
        targetSdk = 37
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        release {
            optimization {
                enable = true
                packageScope = setOf("androidx.**", "kotlin.**", "kotlinx.**")
            }
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
    buildFeatures {
        compose = true
    }
}

dependencies {
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.activity.compose)
    implementation(libs.androidx.compose.material3)
    implementation(libs.androidx.compose.ui)
    implementation(libs.androidx.compose.ui.graphics)
    implementation(libs.androidx.compose.ui.tooling.preview)
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)

    //Se agregan mismas dependencias que pasamos en clases

    // Dependencia para viewModel
    implementation(libs.androidx.lifecycle.runtime.ktx)

    // Dependencia para la navegación con Jetpack Compose
    implementation("androidx.navigation:navigation-compose:2.7.7")

    // Íconos clásicos de Compose
    implementation("androidx.compose.material:material-icons-core:1.6.8")
    implementation("androidx.compose.material:material-icons-extended:1.6.8")
    // Material Symbols (para íconos nuevos como Fastfood, Grass, Cheese, etc.)
    implementation("androidx.compose.material:material-icons-extended:1.7.0-beta01")

// Dependencias Room
    val roomVersion = "2.8.4"

    implementation("androidx.room:room-runtime:$roomVersion")
    implementation("androidx.room:room-ktx:$roomVersion")
    ksp("androidx.room:room-compiler:$roomVersion")  // <-- KSP

    testImplementation(libs.junit)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.compose.ui.test.junit4)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(libs.androidx.junit)
    debugImplementation(libs.androidx.compose.ui.test.manifest)
    debugImplementation(libs.androidx.compose.ui.tooling)
}