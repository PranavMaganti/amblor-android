object Dependencies {
    const val androidGradlePlugin = "com.android.tools.build:gradle:7.0.0-alpha06"
    const val material = "com.google.android.material:material:1.3.0-rc01"
    const val googleServicesPlugin = "com.google.gms:google-services:4.3.5"

    object Testing {
        const val junit = "junit:junit:4.13.1"
    }

    object Mockk {
        private const val version = "1.10.6"
        const val unit = "io.mockk:mockk:$version"
        const val android = "io.mockk:mockk-android:$version"
    }

    object Koin {
        private const val version = "2.2.2"
        const val core  ="org.koin:koin-android:$version"
        const val compose = "org.koin:koin-androidx-compose:$version"
        const val ext = "org.koin:koin-androidx-ext:$version"
        const val test = "org.koin:koin-test:$version"
    }

    object Accompanist {
        private const val version = "0.5.1"
        const val coil = "dev.chrisbanes.accompanist:accompanist-coil:$version"
        const val insets = "dev.chrisbanes.accompanist:accompanist-insets:$version"
    }

    object PlayServices {
        const val auth = "com.google.android.gms:play-services-auth:19.0.0"
        const val coroutines = "org.jetbrains.kotlinx:kotlinx-coroutines-play-services:1.4.2"
    }

    object Firebase {
        const val analytics = "com.google.firebase:firebase-analytics:18.0.2"
        const val authKtx = "com.google.firebase:firebase-auth-ktx:20.0.2"
    }

    object Kotlin {
        const val version = "1.4.30"
        const val stdlib = "org.jetbrains.kotlin:kotlin-stdlib-jdk8:$version"
        const val gradlePlugin = "org.jetbrains.kotlin:kotlin-gradle-plugin:$version"
        const val datetime = "org.jetbrains.kotlinx:kotlinx-datetime:0.1.1"
    }

    object Ktor {
        private const val version = "1.5.1"

        const val cio = "io.ktor:ktor-client-cio:$version"
        const val json = "io.ktor:ktor-client-json:$version"
        const val android = "io.ktor:ktor-client-android:$version"
        const val serialization = "io.ktor:ktor-client-serialization:$version"
        const val auth = "io.ktor:ktor-client-auth:$version"
    }

    object Coroutines {
        private const val version = "1.4.2"
        const val core = "org.jetbrains.kotlinx:kotlinx-coroutines-core:$version"
        const val android = "org.jetbrains.kotlinx:kotlinx-coroutines-android:$version"
        const val test = "org.jetbrains.kotlinx:kotlinx-coroutines-test:$version"
    }

    object AndroidX {
        const val appcompat = "androidx.appcompat:appcompat:1.3.0-beta01"
        const val coreKtx = "androidx.core:core-ktx:1.5.0-beta01"
        const val preferenceKtx = "androidx.preference:preference-ktx:1.1.1"
        const val activityKtx = "androidx.activity:activity-ktx:1.2.0-rc01"
        const val fragmentKtx = "androidx.fragment:fragment-ktx:1.3.0-rc01"

        object Compose {
            const val version = "1.0.0-alpha12"

            const val ui = "androidx.compose.ui:ui:$version"
            const val tooling = "androidx.compose.ui:ui-tooling:$version"
            const val constraintLayout = "androidx.constraintlayout:constraintlayout-compose:1.0.0-alpha02"
            const val activity = "androidx.activity:activity-compose:1.3.0-alpha02"
            const val material = "androidx.compose.material:material:$version"
            const val materialIconsExtended = "androidx.compose.material:material-icons-extended:$version"
            const val navigation = "androidx.navigation:navigation-compose:1.0.0-alpha07"
            const val testJunit = "androidx.compose.ui:ui-test-junit4:$version"
        }

        object Room {
            private const val version = "2.3.0-beta01"
            const val runtime = "androidx.room:room-runtime:$version"
            const val ktx = "androidx.room:room-ktx:$version"
            const val compiler = "androidx.room:room-compiler:$version"
        }

        object Lifecycle {
            private const val version = "2.3.0-rc01"
            const val runtime = "androidx.lifecycle:lifecycle-runtime-ktx:$version"
            const val viewmodel = "androidx.lifecycle:lifecycle-viewmodel-ktx:$version"
            const val compose = "androidx.lifecycle:lifecycle-viewmodel-compose:1.0.0-alpha01"
        }

        object Testing {
            private const val version = "1.4.0-alpha04"
            const val core = "androidx.test:core:$version"
            const val runner = "androidx.test:runner:$version"
            const val ext = "androidx.test.ext:junit:1.1.3-alpha04"
        }
    }
}