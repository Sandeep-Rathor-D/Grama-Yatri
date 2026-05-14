plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
    id("org.jetbrains.kotlin.plugin.compose")
    id("com.google.gms.google-services")
}

android {
    namespace = "com.mindmatrix.gramayatri"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.mindmatrix.gramayatri"
        minSdk = 26
        targetSdk = 35
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
        sourceCompatibility = JavaVersion.VERSION_21
        targetCompatibility = JavaVersion.VERSION_21
    }

    kotlinOptions {
        jvmTarget = "21"
    }

    buildFeatures {
        compose = true
    }

    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }
}

dependencies {

    // ── Compose BOM ──────────────────────────────────────────────
    // BOM manages ALL compose library versions automatically
    // No need to specify versions for individual compose libraries
    val composeBom = platform("androidx.compose:compose-bom:2025.04.01")
    implementation(composeBom)
    implementation("androidx.compose.ui:ui")
    implementation("androidx.compose.ui:ui-graphics")
    implementation("androidx.compose.ui:ui-tooling-preview")
    implementation("androidx.compose.material3:material3")
    implementation("androidx.compose.material:material-icons-extended")

    // ── Activity ─────────────────────────────────────────────────
    implementation("androidx.activity:activity-compose:1.10.1")

    // ── Lifecycle + ViewModel ────────────────────────────────────
    implementation("androidx.lifecycle:lifecycle-runtime-ktx:2.9.0")
    implementation("androidx.lifecycle:lifecycle-viewmodel-compose:2.9.0")
    implementation("androidx.lifecycle:lifecycle-runtime-compose:2.9.0")

    // ── Navigation ───────────────────────────────────────────────
    implementation("androidx.navigation:navigation-compose:2.9.0")

    // ── Firebase BOM ─────────────────────────────────────────────
    val firebaseBom = platform("com.google.firebase:firebase-bom:33.13.0")
    implementation(firebaseBom)
    implementation("com.google.firebase:firebase-database-ktx")
    implementation("com.google.firebase:firebase-messaging-ktx")

    // ── DataStore ────────────────────────────────────────────────
    implementation("androidx.datastore:datastore-preferences:1.1.4")

    // ── Coroutines ───────────────────────────────────────────────
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-android:1.10.2")

    // ── Core ─────────────────────────────────────────────────────
    implementation("androidx.core:core-ktx:1.16.0")

    // ── Testing ──────────────────────────────────────────────────
    testImplementation("junit:junit:4.13.2")
    androidTestImplementation("androidx.test.ext:junit:1.2.1")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.6.1")
    androidTestImplementation(composeBom)
    androidTestImplementation("androidx.compose.ui:ui-test-junit4")
    debugImplementation("androidx.compose.ui:ui-tooling")
    debugImplementation("androidx.compose.ui:ui-test-manifest")
}