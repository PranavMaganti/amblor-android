import java.io.FileInputStream
import java.io.InputStreamReader
import java.util.Properties

plugins {
    id("com.android.application")
    kotlin("android")
    kotlin("plugin.serialization") version Dependencies.Kotlin.version
    id("com.google.gms.google-services")
    id("kotlin-kapt")
    id("kotlin-parcelize")
    id("org.jmailen.kotlinter") version "3.3.0"
}

val secretsProperties = Properties()
val propertiesFile = rootProject.file("./secret.properties")
InputStreamReader(FileInputStream(propertiesFile), Charsets.UTF_8).use { reader ->
    secretsProperties.load(reader)
}

android {
    compileSdkVersion(30)
    buildToolsVersion = "30.0.3"

    defaultConfig {
        applicationId = "com.vanpra.amblor"
        minSdkVersion(26)
        targetSdkVersion(30)
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "com.vanpra.amblor.ComposeInstrumentTestRunner"
        // testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"

        buildConfigField(
            "String",
            "REQUEST_ID_TOKEN",
            secretsProperties["request_id_token"] as String
        )
    }

    signingConfigs {
        getByName("debug") {
            storeFile = rootProject.file(secretsProperties["debug_keystore"] as String)
            keyAlias = "androiddebugkey"
            storePassword = "android"
            keyPassword = "android"
        }

        create("release") {
            storeFile = rootProject.file(secretsProperties["release_keystore"] as String)
            storePassword = secretsProperties["release_keystore_password"] as String
            keyPassword = secretsProperties["release_key_password"] as String
            keyAlias = "release"
        }
    }

    packagingOptions.excludes.addAll(
        listOf(
            "META-INF/LICENSE",
            "META-INF/AL2.0",
            "META-INF/**",
            "META-INF/*.kotlin_module"
        )
    )

    buildTypes {
        getByName("release") {
            signingConfig = signingConfigs.getByName("release")
            isMinifyEnabled = false
        }
    }

    buildFeatures {
        compose = true
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }

    kotlinOptions {
        jvmTarget = "1.8"
        useIR = true
    }

    composeOptions {
        kotlinCompilerExtensionVersion = Dependencies.AndroidX.Compose.version
    }
}

tasks.withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile>().all {
    kotlinOptions {
        jvmTarget = "1.8"
        freeCompilerArgs = listOf(
            "-Xskip-prerelease-check",
            "-Xopt-in=kotlinx.coroutines.ExperimentalCoroutinesApi",
            "-Xopt-in=kotlinx.coroutines.FlowPreview",
            "-Xopt-in=kotlin.Experimental",
            "-Xopt-in=androidx.compose.animation.ExperimentalAnimationApi"
        )
    }
}

dependencies {
    implementation(Dependencies.Kotlin.stdlib)
    implementation(Dependencies.Kotlin.datetime)

    implementation(Dependencies.Coroutines.core)
    implementation(Dependencies.Coroutines.android)
    implementation(Dependencies.Coroutines.test)

    implementation(Dependencies.Firebase.analytics)
    implementation(Dependencies.Firebase.authKtx)

    implementation(Dependencies.PlayServices.auth)
    implementation(Dependencies.PlayServices.coroutines)

    implementation(Dependencies.AndroidX.Compose.ui)
    implementation(Dependencies.AndroidX.Compose.material)
    implementation(Dependencies.AndroidX.Compose.tooling)
    implementation(Dependencies.AndroidX.Compose.constraintLayout)
    implementation(Dependencies.AndroidX.Compose.activity)
    implementation(Dependencies.AndroidX.Compose.materialIconsExtended)
    implementation(Dependencies.AndroidX.Compose.navigation)

    implementation(Dependencies.AndroidX.coreKtx)
    implementation(Dependencies.AndroidX.preferenceKtx)
    implementation(Dependencies.AndroidX.appcompat)
    implementation(Dependencies.AndroidX.activityKtx)
    implementation(Dependencies.AndroidX.fragmentKtx)

    implementation(Dependencies.AndroidX.Lifecycle.runtime)
    implementation(Dependencies.AndroidX.Lifecycle.viewmodel)

    implementation(Dependencies.material)

    implementation(Dependencies.Accompanist.coil)
    implementation(Dependencies.Accompanist.insets)

    implementation(Dependencies.AndroidX.Room.runtime)
    implementation(Dependencies.AndroidX.Room.ktx)
    kapt(Dependencies.AndroidX.Room.compiler)

    implementation(Dependencies.Ktor.cio)
    implementation(Dependencies.Ktor.json)
    implementation(Dependencies.Ktor.android)
    implementation(Dependencies.Ktor.serialization)
    implementation(Dependencies.Ktor.auth)

    implementation(Dependencies.Koin.core)
    // implementation(Dependencies.Koin.compose)
    implementation(Dependencies.Koin.ext)
    implementation(Dependencies.Koin.test)

    testImplementation(Dependencies.Testing.junit)
    testImplementation(Dependencies.Mockk.unit)
    testImplementation(Dependencies.AndroidX.Testing.core)

    androidTestImplementation(Dependencies.AndroidX.Testing.ext)
    androidTestImplementation(Dependencies.AndroidX.Compose.testJunit)
    androidTestImplementation(Dependencies.AndroidX.Compose.ui)
    androidTestImplementation(Dependencies.Mockk.android)
    androidTestImplementation(Dependencies.AndroidX.Testing.runner)
    androidTestImplementation("androidx.test.espresso:espresso-core:3.3.0")
}