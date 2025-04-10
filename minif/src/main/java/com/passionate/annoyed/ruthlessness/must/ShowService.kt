package com.passionate.annoyed.ruthlessness.must

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageInfo
import android.content.pm.PackageManager
import android.os.Build
import android.os.Handler
import android.os.Looper
import android.os.Process
import androidx.core.content.ContextCompat
import com.passionate.annoyed.ruthlessness.zjd.scan.GameMiFService
import com.passionate.annoyed.ruthlessness.start.FebApp
import com.passionate.annoyed.ruthlessness.start.FebApp.febApp
import com.passionate.annoyed.ruthlessness.utils.KeyContent
import java.util.ArrayList

object ShowService {
    var KEY_IS_SERVICE = false
    var activityList = ArrayList<Activity>()
    fun closeAllActivities() {
        KeyContent.showLog("closeAllActivities")
        for (activity in activityList) {
            activity.finishAndRemoveTask()
        }
        activityList.clear()
    }

    fun addActivity(activity: Activity) {
        activityList.add(activity)
    }

    fun removeActivity(activity: Activity) {
        activityList.remove(activity)
    }
    fun getInstallTimeDataFun(): Long {
        try {
            val packageManager: PackageManager = febApp.packageManager
            val packageInfo: PackageInfo = packageManager.getPackageInfo(FebApp.febApp.packageName, 0)
            val firstInstallTime: Long = packageInfo.firstInstallTime
            return (System.currentTimeMillis() - firstInstallTime) / 1000
        } catch (e: PackageManager.NameNotFoundException) {
            e.printStackTrace()
            return 0L
        }
    }

    fun getInstallFast(): Long {
        try {
            val packageManager: PackageManager = febApp.packageManager
            val packageInfo: PackageInfo = packageManager.getPackageInfo(FebApp.febApp.packageName, 0)
            return packageInfo.firstInstallTime
        } catch (e: PackageManager.NameNotFoundException) {
            e.printStackTrace()
            return 0L
        }
    }

    private val handlerService = Handler(Looper.getMainLooper())
    private var runnableService: Runnable? = null

     fun startService() {
         stopService()
        runnableService = object : Runnable {
            override fun run() {
                KeyContent.showLog("FebFiveFffService-startService---1-----$KEY_IS_SERVICE")
                if (!KEY_IS_SERVICE && Build.VERSION.SDK_INT < 31) {
                    KeyContent.showLog("FebFiveFffService-startService---2-----$KEY_IS_SERVICE")
                    ContextCompat.startForegroundService(
                        febApp,
                        Intent(febApp, GameMiFService::class.java)
                    )
                } else {
                    KeyContent.showLog("FebFiveFffService-startService---3-----$KEY_IS_SERVICE")
                    stopService()
                    return
                }

                handlerService.postDelayed(this, 1020)
            }
        }
        handlerService.postDelayed(runnableService!!,1020)
    }

    private fun stopService() {
        runnableService?.let {
            handlerService.removeCallbacks(it)
            runnableService = null
        }
    }

    fun isMainProcess(context: Context): Boolean {
        val currentProcessName = getCurrentProcessName(context)
        return currentProcessName == context.packageName
    }

    private fun getCurrentProcessName(context: Context): String? {
        val pid = Process.myPid()
        val activityManager = context.getSystemService(Context.ACTIVITY_SERVICE) as android.app.ActivityManager
        for (processInfo in activityManager.runningAppProcesses) {
            if (processInfo.pid == pid) {
                return processInfo.processName
            }
        }
        return null
    }
}