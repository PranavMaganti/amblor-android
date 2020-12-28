object Libs {
    const val androidGradlePlugin = "com.android.tools.build:gradle:7.0.0-alpha03"
    const val material = "com.google.android.material:material:1.3.0-beta01"
    const val googleServicesPlugin = "com.google.gms:google-services:4.3.4"

    object Accompanist {
        private const val version = "0.4.1"
        const val coil = "dev.chrisbanes.accompanist:accompanist-coil:$version"
        const val insets = "dev.chrisbanes.accompanist:accompanist-insets:$version"
    }

    object PlayServices {
        const val auth = "com.google.android.gms:play-services-auth:19.0.0"
        const val coroutines = "org.jetbrains.kotlinx:kotlinx-coroutines-play-services:1.4.2"
    }

    object Firebase {
        const val analytics = "com.google.firebase:firebase-analytics:18.0.0"
        const val authKtx = "com.google.firebase:firebase-auth-ktx:20.0.1"
    }

    object Kotlin {
        const val version = "1.4.21"
        const val stdlib = "org.jetbrains.kotlin:kotlin-stdlib-jdk8:$version"
        const val gradlePlugin = "org.jetbrains.kotlin:kotlin-gradle-plugin:$version"
        const val datetime = "org.jetbrains.kotlinx:kotlinx-datetime:0.1.0"

        object Serialization {
            private const val version = "1.0.1"
            const val core = "org.jetbrains.kotlinx:kotlinx-serialization-core:$version"
        }
    }

    object Ktor {
        private const val version = "1.5.0"

        const val cio = "io.ktor:ktor-client-cio:$version"
        const val json = "io.ktor:ktor-client-json:$version"
        const val android = "io.ktor:ktor-client-android:$version"
        const val serialization = "io.ktor:ktor-client-serialization:$version"
    }

    object Coroutines {
        private const val version = "1.4.2"
        const val core = "org.jetbrains.kotlinx:kotlinx-coroutines-core:$version"
        const val android = "org.jetbrains.kotlinx:kotlinx-coroutines-android:$version"
        const val test = "org.jetbrains.kotlinx:kotlinx-coroutines-test:$version"
    }

    object AndroidX {
        const val appcompat = "androidx.appcompat:appcompat:1.3.0-alpha02"
        const val coreKtx = "androidx.core:core-ktx:1.5.0-alpha05"
        const val preference = "androidx.preference:preference-ktx:1.1.1"

        object Compose {
            const val version = "1.0.0-alpha09"

            const val ui = "androidx.compose.ui:ui:$version"
            const val tooling = "androidx.compose.ui:ui-tooling:$version"
            const val foundation = "androidx.compose.foundation:foundation:$version"
            const val foundationLayout = "androidx.compose.foundation:foundation-layout:$version"
            const val material = "androidx.compose.material:material:$version"
            const val materialIconsExtended = "androidx.compose.material:material-icons-extended:$version"
            const val navigation = "androidx.navigation:navigation-compose:1.0.0-alpha04"
            const val test = "androidx.compose.ui:ui-test:$version"
        }

        object Room {
            private const val version = "2.3.0-alpha04"
            const val runtime = "androidx.room:room-runtime:$version"
            const val ktx = "androidx.room:room-ktx:$version"
            const val compiler = "androidx.room:room-compiler:$version"
        }

        object Lifecycle {
            private const val version = "2.3.0-rc01"
            const val runtime = "androidx.lifecycle:lifecycle-runtime-ktx:$version"
            const val viewmodel = "androidx.lifecycle:lifecycle-viewmodel-ktx:$version"
        }
    }
}