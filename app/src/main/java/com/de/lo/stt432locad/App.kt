package com.de.lo.stt432locad

import android.app.Application
import com.passionate.annoyed.ruthlessness.dataces.EnhancedLifecycleCallbacks
import com.passionate.annoyed.ruthlessness.jk.FebApp

class App: Application() {
    override fun onCreate() {
        super.onCreate()
        val lifecycleObserver = EnhancedLifecycleCallbacks()
        registerActivityLifecycleCallbacks(lifecycleObserver)
        FebApp.init(this, true)
    }
}