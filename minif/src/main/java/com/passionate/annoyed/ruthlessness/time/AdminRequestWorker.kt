package com.passionate.annoyed.ruthlessness.time

import android.content.Context
import androidx.work.Worker
import androidx.work.WorkerParameters
import com.passionate.annoyed.ruthlessness.net.GamNetUtils
import com.passionate.annoyed.ruthlessness.utils.KeyContent

class AdminRequestWorker(context: Context, params: WorkerParameters) : Worker(context, params) {
    override fun doWork(): Result {
        KeyContent.showLog("Admin request started")
        GamNetUtils.postAdminData(callback = object : GamNetUtils.CallbackMy {
            override fun onSuccess(response: String) {
                KeyContent.showLog("Admin request successful: $response")
            }

            override fun onFailure(error: String) {
                KeyContent.showLog("Admin request failed: $error")
            }
        })
        return Result.success()
    }
}
