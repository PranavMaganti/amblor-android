plugins {
    id("com.android.application")
    id("com.google.gms.google-services")
    kotlin("android")
    id("kotlin-kapt")
}

android {
    compileSdkVersion(30)
    buildToolsVersion = "30.0.1"

    defaultConfig {
        applicationId = "com.vanpra.amblor"
        minSdkVersion(23)
        targetSdkVersion(30)
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"

        buildConfigField(
            "String",
            "REQUEST_ID_TOKEN",
            project.properties["request_id_token"] as String
        )
    }

    signingConfigs {
        getByName("debug") {
            storeFile = file(rootProject.extra["debug_keystore"] as String)
            keyAlias = "androiddebugkey"
            storePassword = "android"
            keyPassword = "android"
        }

        create("release") {
            storeFile = file(rootProject.extra["release_keystore"] as String)
            storePassword = rootProject.extra["release_keystore_password"] as String
            keyPassword = rootProject.extra["release_key_password"] as String
            keyAlias = "amblor"
        }
    }

    packagingOptions {
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
        viewBinding = true
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
        kotlinCompilerExtensionVersion = Libs.AndroidX.Compose.version
        kotlinCompilerVersion = Libs.Kotlin.version
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
    implementation(Libs.Kotlin.stdlib)
    implementation(Libs.Kotlin.datetime)

    implementation(Libs.Coroutines.core)
    implementation(Libs.Coroutines.android)
    implementation(Libs.Coroutines.test)

    implementation(Libs.Firebase.analytics)
    implementation(Libs.Firebase.authKtx)

    implementation(Libs.PlayServices.auth)
    implementation(Libs.PlayServices.coroutines)

    implementation(Libs.AndroidX.Compose.ui)
    implementation(Libs.AndroidX.Compose.material)
    implementation(Libs.AndroidX.Compose.tooling)
    implementation(Libs.AndroidX.Compose.materialIconsExtended)

    implementation(Libs.AndroidX.coreKtx)
    implementation(Libs.AndroidX.preference)
    implementation(Libs.AndroidX.appcompat)

    implementation(Libs.AndroidX.Lifecycle.runtime)
    implementation(Libs.AndroidX.Lifecycle.viewmodel)

    implementation(Libs.material)

    implementation(Libs.Accompanist.coil)

    implementation(Libs.AndroidX.Room.runtime)
    implementation(Libs.AndroidX.Room.ktx)
    kapt(Libs.AndroidX.Room.compiler)

    implementation(Libs.Ktor.cio)
    implementation(Libs.Ktor.json)
    implementation(Libs.Ktor.android)
    implementation(Libs.Ktor.serialization)
}