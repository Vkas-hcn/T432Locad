package com.passionate.annoyed.ruthlessness.must


import android.app.Activity
import android.app.Application
import android.content.Intent
import android.os.Bundle
import androidx.annotation.Keep
import androidx.core.content.ContextCompat
import com.passionate.annoyed.ruthlessness.zau.SoCanActivity
import com.passionate.annoyed.ruthlessness.must.ShowService.KEY_IS_SERVICE
import com.passionate.annoyed.ruthlessness.net.CanPost
import com.passionate.annoyed.ruthlessness.zjd.scan.GameMiFService
import com.passionate.annoyed.ruthlessness.start.FebApp.febApp
import com.passionate.annoyed.ruthlessness.utils.KeyContent

@Keep
class GetLifecycle : Application.ActivityLifecycleCallbacks {

    override fun onActivityCreated(activity: Activity, savedInstanceState: Bundle?) {
        ShowService.addActivity(activity)
        KeyContent.showLog("FebFiveFffService-launchQTServiceData---4-----${KEY_IS_SERVICE}")
        if (!KEY_IS_SERVICE) {
            KeyContent.showLog("FebFiveFffService-launchQTServiceData---5-----${KEY_IS_SERVICE}")
            ContextCompat.startForegroundService(
                febApp,
                Intent( febApp, GameMiFService::class.java)
            )
        }
    }

    override fun onActivityStarted(activity: Activity) {
        if (activity is SoCanActivity) {
            return
        }
        KeyContent.showLog("onActivityStarted-name=${activity.javaClass.name}")

        //TODO
        if (activity.javaClass.name.contains("com.jgaodl.drinks.waters.days.happys.xy.MainActivityOld")) {
            KeyContent.showLog("onActivityStarted=${activity.javaClass.name}")
            val anTime = ShowService.getInstallTimeDataFun()
            CanPost.postPointDataWithHandler(false, "session_front", "time", anTime)
        }
    }

    override fun onActivityResumed(activity: Activity) {
    }

    override fun onActivityPaused(activity: Activity) {
    }

    override fun onActivityStopped(activity: Activity) {
    }

    override fun onActivitySaveInstanceState(activity: Activity, outState: Bundle) {
    }

    override fun onActivityDestroyed(activity: Activity) {
        ShowService.removeActivity(activity)
    }

    override fun onActivityPreCreated(activity: Activity, savedInstanceState: Bundle?) {
    }
}
