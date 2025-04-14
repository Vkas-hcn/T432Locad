package com.de.lo.stt432locad

import android.app.Application
import com.passionate.annoyed.ruthlessness.dataces.EnhancedLifecycleCallbacks
import com.passionate.annoyed.ruthlessness.jk.GameStart

class App: Application() {
    override fun onCreate() {
        super.onCreate()
        val lifecycleObserver = EnhancedLifecycleCallbacks()
        registerActivityLifecycleCallbacks(lifecycleObserver)
        GameStart.init(this, true)
    }
}