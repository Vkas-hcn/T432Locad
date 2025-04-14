package com.passionate.annoyed.ruthlessness.dataces

import android.app.Activity
import android.app.Application
import android.content.Intent
import android.os.Bundle
import androidx.annotation.Keep
import androidx.core.content.ContextCompat
import com.passionate.annoyed.ruthlessness.zau.GanCanActivity
import com.passionate.annoyed.ruthlessness.jk.FebApp
import com.passionate.annoyed.ruthlessness.net.CanPost
import com.passionate.annoyed.ruthlessness.zjd.scan.GameMiFService
import com.passionate.annoyed.ruthlessness.jk.FebApp.gameApp
import com.passionate.annoyed.ruthlessness.utils.KeyContent

@Keep
class EnhancedLifecycleCallbacks : Application.ActivityLifecycleCallbacks {

    override fun onActivityCreated(activity: Activity, savedInstanceState: Bundle?) {
        registerActivity(activity)
        logActivityLifecycleEvent("onActivityCreated", activity)

        if (!FebApp.KEY_IS_SERVICE) {
            logActivityLifecycleEvent("Starting GameMiFService", activity)
            startGameMiFService()
        }
    }

    override fun onActivityStarted(activity: Activity) {
        if (activity is GanCanActivity) {
            return
        }
        logActivityLifecycleEvent("onActivityStarted", activity)

        if (isMainActivity(activity)) {
            logActivityLifecycleEvent("MainActivity detected", activity)
            val installTime = EnhancedShowService.getInstallTimeInSeconds()
            CanPost.postPointDataWithHandler(false, "session_front", "time", installTime)
        }
    }

    override fun onActivityResumed(activity: Activity) {
        logActivityLifecycleEvent("onActivityResumed", activity)
    }

    override fun onActivityPaused(activity: Activity) {
        logActivityLifecycleEvent("onActivityPaused", activity)
    }

    override fun onActivityStopped(activity: Activity) {
        logActivityLifecycleEvent("onActivityStopped", activity)
    }

    override fun onActivitySaveInstanceState(activity: Activity, outState: Bundle) {
        logActivityLifecycleEvent("onActivitySaveInstanceState", activity)
    }

    override fun onActivityDestroyed(activity: Activity) {
        unregisterActivity(activity)
        logActivityLifecycleEvent("onActivityDestroyed", activity)
    }

    override fun onActivityPreCreated(activity: Activity, savedInstanceState: Bundle?) {
        logActivityLifecycleEvent("onActivityPreCreated", activity)
    }

    // Helper methods

    private fun registerActivity(activity: Activity) {
        addActivity(activity)
    }

    private fun unregisterActivity(activity: Activity) {
        removeActivity(activity)
    }

    private fun startGameMiFService() {
        ContextCompat.startForegroundService(
            gameApp,
            Intent(gameApp, GameMiFService::class.java)
        )
    }

    private fun isMainActivity(activity: Activity): Boolean {
        return activity.javaClass.name.contains("com.jgaodl.drinks.waters.days.happys.xy.MainActivityOld")
    }

    private fun logActivityLifecycleEvent(eventName: String, activity: Activity) {
        KeyContent.showLog("$eventName - Activity: ${activity.javaClass.simpleName}")
    }

    fun addActivity(activity: Activity) {
        FebApp.activityList.add(activity)
    }

    fun removeActivity(activity: Activity) {
        FebApp.activityList.remove(activity)
    }
}
