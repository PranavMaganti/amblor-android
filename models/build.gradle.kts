plugins {
    id("com.android.library")
    id("kotlin-android")
    kotlin("plugin.serialization") version Dependencies.Kotlin.version
}

android {
    compileSdkVersion(30)
    buildToolsVersion = "30.0.3"

    defaultConfig {
        minSdkVersion(23)
        targetSdkVersion(30)
        versionCode = 1
        versionName = "1.0"
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }

    kotlinOptions {
        jvmTarget = "1.8"
    }

    packagingOptions.apply {
        exclude("META-INF/LICENSE")
        exclude("META-INF/AL2.0")
        exclude("META-INF/**")
        exclude("META-INF/*.kotlin_module")
    }
}

dependencies {
    implementation(Dependencies.Kotlin.stdlib)
    implementation(Dependencies.Kotlin.Serialization.core)

    implementation(Dependencies.AndroidX.coreKtx)
    implementation(Dependencies.AndroidX.appcompat)
    implementation(Dependencies.material)

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