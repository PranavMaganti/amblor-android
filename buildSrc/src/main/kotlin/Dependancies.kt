object Libs {
    const val androidGradlePlugin = "com.android.tools.build:gradle:4.2.0-alpha12"
    const val material = "com.google.android.material:material:1.2.1"
    const val googleServicesPlugin = "com.google.gms:google-services:4.3.3"

    object Accompanist {
        private const val version = "0.2.2.ui-6824694-SNAPSHOT"
        const val coil = "dev.chrisbanes.accompanist:accompanist-coil:$version"
    }

    object PlayServices {
        const val auth = "com.google.android.gms:play-services-auth:18.1.0"
        const val coroutines = "org.jetbrains.kotlinx:kotlinx-coroutines-play-services:1.1.1"
    }

    object Firebase {
        const val analytics = "com.google.firebase:firebase-analytics:17.5.0"
        const val authKtx = "com.google.firebase:firebase-auth-ktx:19.3.2"
    }

    object Kotlin {
        const val version = "1.4.10"
        const val stdlib = "org.jetbrains.kotlin:kotlin-stdlib-jdk8:$version"
        const val gradlePlugin = "org.jetbrains.kotlin:kotlin-gradle-plugin:$version"
        const val serializationPlugin = "org.jetbrains.kotlin:kotlin-gradle-plugin:$version"
        const val extensions = "org.jetbrains.kotlin:kotlin-android-extensions:$version"
        const val datetime = "org.jetbrains.kotlinx:kotlinx-datetime:0.1.0"

        object Serialization {
            private const val version = "1.0.0-RC"
            const val core = "org.jetbrains.kotlinx:kotlinx-serialization-core:$version"
        }
    }

    object Ktor {
        private const val version = "1.4.0"

        const val cio = "io.ktor:ktor-client-cio:$version"
        const val json = "io.ktor:ktor-client-json:$version"
        const val android = "io.ktor:ktor-client-android:$version"
        const val serialization = "io.ktor:ktor-client-serialization:$version"
    }

    object Coroutines {
        private const val version = "1.3.9"
        const val core = "org.jetbrains.kotlinx:kotlinx-coroutines-core:$version"
        const val android = "org.jetbrains.kotlinx:kotlinx-coroutines-android:$version"
        const val test = "org.jetbrains.kotlinx:kotlinx-coroutines-test:$version"
    }

    object AndroidX {
        const val appcompat = "androidx.appcompat:appcompat:1.2.0-rc01"
        const val coreKtx = "androidx.core:core-ktx:1.5.0-alpha02"
        const val preference = "androidx.preference:preference-ktx:1.1.1"

        object Compose {
            const val version = "1.0.0-SNAPSHOT"
            const val snapshot = "6839429"

            const val ui = "androidx.compose.ui:ui:$version"
            const val tooling = "androidx.ui:ui-tooling:$version"
            const val foundation = "androidx.compose.foundation:foundation:$version"
            const val foundationLayout = "androidx.compose.foundation:foundation-layout:$version"
            const val material = "androidx.compose.material:material:$version"
            const val materialIconsExtended = "androidx.compose.material:material-icons-extended:$version"
            const val navigation = "androidx.compose.navigation:navigation:0.1.0-SNAPSHOT"
        }

        object Room {
            private const val version = "2.2.5"
            const val runtime = "androidx.room:room-runtime:$version"
            const val ktx = "androidx.room:room-ktx:$version"
            const val compiler = "androidx.room:room-compiler:$version"
        }

        object Lifecycle {
            private const val version = "2.3.0-alpha07"
            const val runtime = "androidx.lifecycle:lifecycle-runtime-ktx:$version"
            const val viewmodel = "androidx.lifecycle:lifecycle-viewmodel-ktx:$version"
        }
    }
}