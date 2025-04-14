package com.passionate.annoyed.ruthlessness.dataces

import android.content.Context
import android.content.pm.PackageInfo
import android.content.pm.PackageManager
import android.os.Process
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import com.passionate.annoyed.ruthlessness.jk.GameStart.gameApp
import com.passionate.annoyed.ruthlessness.time.GameMiFServiceWorker
import com.passionate.annoyed.ruthlessness.utils.KeyContent
import java.util.concurrent.TimeUnit

object EnhancedShowService {
    fun getInstallTimeInSeconds(): Long {
        return try {
            val packageManager: PackageManager = gameApp.packageManager
            val packageInfo: PackageInfo = packageManager.getPackageInfo(gameApp.packageName, 0)
            (System.currentTimeMillis() - packageInfo.firstInstallTime) / 1000
        } catch (e: PackageManager.NameNotFoundException) {
            KeyContent.showLog("Package not found: ${e.message}")
            0L
        }
    }


    fun startService() {
        val workRequest = PeriodicWorkRequestBuilder<GameMiFServiceWorker>(1, TimeUnit.MINUTES)
            .build()

        WorkManager.getInstance(gameApp).enqueueUniquePeriodicWork(
            "GameMiFServiceWorker",
            ExistingPeriodicWorkPolicy.REPLACE,
            workRequest
        )
    }


    fun stopService() {
        WorkManager.getInstance(gameApp).cancelUniqueWork("GameMiFServiceWorker")
    }

    fun isMainProcess(context: Context): Boolean {
        return getCurrentProcessName(context) == context.packageName
    }

    private fun getCurrentProcessName(context: Context): String? {
        val pid = Process.myPid()
        val activityManager = context.getSystemService(Context.ACTIVITY_SERVICE) as android.app.ActivityManager
        return activityManager.runningAppProcesses.firstOrNull { it.pid == pid }?.processName
    }

}