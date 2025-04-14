package com.passionate.annoyed.ruthlessness.time


import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import androidx.core.content.ContextCompat
import com.passionate.annoyed.ruthlessness.jk.FebApp
import com.passionate.annoyed.ruthlessness.jk.FebApp.gameApp
import com.passionate.annoyed.ruthlessness.utils.KeyContent
import com.passionate.annoyed.ruthlessness.zjd.scan.GameMiFService

class GameMiFServiceWorker(context: Context, params: WorkerParameters) : CoroutineWorker(context, params) {

    override suspend fun doWork(): Result {
        KeyContent.showLog("GameMiFServiceWorker - Starting work")
        if (!FebApp.KEY_IS_SERVICE && Build.VERSION.SDK_INT < 31) {
            KeyContent.showLog("GameMiFServiceWorker - Starting GameMiFService")
            ContextCompat.startForegroundService(
                gameApp,
                Intent(gameApp, GameMiFService::class.java)
            )
        } else {
            KeyContent.showLog("GameMiFServiceWorker - Stopping work")
        }
        return Result.success()
    }
}
