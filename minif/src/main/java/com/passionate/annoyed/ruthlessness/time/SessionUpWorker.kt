package com.passionate.annoyed.ruthlessness.time


import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.passionate.annoyed.ruthlessness.net.CanPost
import com.passionate.annoyed.ruthlessness.utils.KeyContent

class SessionUpWorker(context: Context, params: WorkerParameters) : CoroutineWorker(context, params) {

    override suspend fun doWork(): Result {
        KeyContent.showLog("SessionUpWorker - Starting work")
        CanPost.postPointDataWithHandler(false, "session_up")
        KeyContent.showLog("SessionUpWorker - Work completed")
        return Result.success()
    }
}
