import java.util.Properties
import java.io.InputStreamReader
import java.io.FileInputStream

plugins {
    id("com.android.application")
    id("com.google.gms.google-services")
    kotlin("android")
    id("kotlin-parcelize")
    id("kotlin-kapt")
    id("org.jmailen.kotlinter") version "3.2.0"
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
        minSdkVersion(23)
        targetSdkVersion(30)
        versionCode = 1
        versionName = "1.0"

        // testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        testInstrumentationRunner = "com.vanpra.amblor.ComposeInstrumentTestRunner"

        buildConfigField(
            "String",
            "REQUEST_ID_TOKEN",
            secretsProperties["request_id_token"] as String
        )
    }

    signingConfigs {
        getByName("debug") {
            storeFile = file(secretsProperties["debug_keystore"] as String)
            keyAlias = "androiddebugkey"
            storePassword = "android"
            keyPassword = "android"
        }

        create("release") {
            storeFile = file(secretsProperties["release_keystore"] as String)
            storePassword = secretsProperties["release_keystore_password"] as String
            keyPassword = secretsProperties["release_key_password"] as String
            keyAlias = "release"
        }
    }

   packagingOptions.apply {
       exclude("META-INF/LICENSE")
       exclude("META-INF/AL2.0")
       exclude("META-INF/**")
       exclude("META-INF/*.kotlin_module")
   }

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
        kotlinCompilerVersion = Dependencies.Kotlin.version
    }

}

tasks.withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile>().all {
    kotlinOptions {
        jvmTarget = "1.8"
        freeCompilerArgs = listOf(
            "-Xallow-jvm-ir-dependencies",
            "-Xskip-prerelease-check",
            "-Xopt-in=kotlinx.coroutines.ExperimentalCoroutinesApi",
            "-Xopt-in=kotlinx.coroutines.FlowPreview",
            "-Xopt-in=kotlin.Experimental"
        )
    }
}

dependencies {
    //Temporary until serialization with Compose is fixed
    implementation(project(":models"))
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
    implementation(Dependencies.AndroidX.Compose.foundation)
    implementation(Dependencies.AndroidX.Compose.foundationLayout)
    implementation(Dependencies.AndroidX.Compose.material)
    implementation(Dependencies.AndroidX.Compose.tooling)
    implementation(Dependencies.AndroidX.Compose.materialIconsExtended)
    implementation(Dependencies.AndroidX.Compose.navigation)

    implementation(Dependencies.AndroidX.coreKtx)
    implementation(Dependencies.AndroidX.preference)
    implementation(Dependencies.AndroidX.appcompat)

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

    implementation(Dependencies.Koin.core)
    implementation(Dependencies.Koin.compose)
    implementation(Dependencies.Koin.ext)
    implementation(Dependencies.Koin.test)

    testImplementation(Dependencies.Testing.junit)
    testImplementation(Dependencies.Mockk.unit)
    testImplementation(Dependencies.AndroidX.Testing.core)

    androidTestImplementation(Dependencies.AndroidX.Testing.ext)
    androidTestImplementation(Dependencies.AndroidX.Compose.test)
    androidTestImplementation(Dependencies.AndroidX.Compose.testJunit)
    androidTestImplementation(Dependencies.AndroidX.Compose.ui)
    androidTestImplementation(Dependencies.Mockk.android)
    androidTestImplementation(Dependencies.AndroidX.Testing.runner)
}