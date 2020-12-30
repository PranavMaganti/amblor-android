package com.vanpra.amblor

import android.app.Application
import androidx.room.Room
import com.vanpra.amblor.auth.AuthenticationApi
import com.vanpra.amblor.auth.FirebaseAuthRepository
import com.vanpra.amblor.data.AmblorDatabase
import com.vanpra.amblor.repositories.AmblorApiRepository
import com.vanpra.amblor.repositories.NotificationRepository
import com.vanpra.amblor.ui.layouts.auth.AuthViewModel
import org.koin.android.ext.koin.androidApplication
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.core.context.startKoin
import org.koin.dsl.module

class AmblorApplication : Application() {
    private val appModule = module {
        single { AmblorApiRepository() }
        single { NotificationRepository(get()) }
        single {
            Room.databaseBuilder(
                androidApplication(),
                AmblorDatabase::class.java,
                "scrobble_database"
            )
                .build()
        }
        single<AuthenticationApi> { FirebaseAuthRepository() }

        viewModel { AuthViewModel(androidApplication(), get(), get()) }
        viewModel { MainViewModel(androidApplication(), get()) }
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
