package com.de.lo.stt432locad

import android.app.Application
import com.passionate.annoyed.ruthlessness.must.GetLifecycle
import com.passionate.annoyed.ruthlessness.start.FebApp

class App: Application() {
    override fun onCreate() {
        super.onCreate()
        val lifecycleObserver = GetLifecycle()
        registerActivityLifecycleCallbacks(lifecycleObserver)
        FebApp.init(this, false)
    }
}