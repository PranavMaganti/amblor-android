package com.vanpra.amblor.di

import android.app.Application
import android.content.Context
import android.content.SharedPreferences
import androidx.navigation.NavHostController
import androidx.room.Room
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.vanpra.amblor.BuildConfig
import com.vanpra.amblor.MainViewModel
import com.vanpra.amblor.data.AmblorDatabase
import com.vanpra.amblor.interfaces.AmblorApi
import com.vanpra.amblor.interfaces.AuthenticationApi
import com.vanpra.amblor.repositories.AmblorApiRepository
import com.vanpra.amblor.repositories.FirebaseAuthRepository
import com.vanpra.amblor.repositories.NotificationRepository
import com.vanpra.amblor.ui.layouts.auth.AuthViewModel
import com.vanpra.amblor.ui.layouts.profile.ProfileViewModel
import com.vanpra.amblor.ui.layouts.scrobble.ScrobbleViewModel
import com.vanpra.amblor.ui.layouts.stats.StatsViewModel
import org.koin.android.ext.koin.androidApplication
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.core.context.startKoin
import org.koin.core.qualifier.named
import org.koin.dsl.module

class AmblorApplication : Application() {
    companion object {
        val appModule = module {
            single(named("apiPrefs")) { provideApiPreferences(androidApplication()) }
            single<AmblorApi> { AmblorApiRepository(get(named("apiPrefs"))) }
            single<AuthenticationApi> { FirebaseAuthRepository(get()) }

            single { NotificationRepository(get(), get(), get()) }
            single {
                Room.databaseBuilder(
                    androidApplication(),
                    AmblorDatabase::class.java,
                    "scrobble_database"
                ).build()
            }
            single { provideGoogleSignInClient(androidApplication()) }

            viewModel { (controller: NavHostController) ->
                AuthViewModel(
                    androidApplication(),
                    get(),
                    get(),
                    controller
                )
            }
            viewModel { MainViewModel(androidApplication(), get()) }
            viewModel { ScrobbleViewModel(androidApplication(), get(), get(), get()) }
            viewModel { StatsViewModel(androidApplication()) }
            viewModel { ProfileViewModel(androidApplication(), get(), get(), get(named("apiPrefs"))) }
        }

        private fun provideGoogleSignInClient(application: Application): GoogleSignInClient {
            val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(BuildConfig.REQUEST_ID_TOKEN)
                .requestEmail()
                .build()
            return GoogleSignIn.getClient(application.applicationContext, gso)
        }

        private fun provideApiPreferences(app: Application): SharedPreferences =
            app.getSharedPreferences("com.vanpra.amblor.api_preferences", Context.MODE_PRIVATE)
    }

    override fun onCreate() {
        super.onCreate()
        startKoin {
            androidLogger()
            androidContext(this@AmblorApplication)
            modules(appModule)
        }
    }
}
