package com.vanpra.amblor

import android.app.Application
import android.app.Instrumentation
import android.content.Context
import androidx.test.runner.AndroidJUnitRunner

class ComposeInstrumentTestRunner: AndroidJUnitRunner() {
    override fun newApplication(
        cl: ClassLoader?, className: String?, context: Context?
    ): Application {
        return Instrumentation.newApplication(AmblorApplication::class.java, context)
    }
}