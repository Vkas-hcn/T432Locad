package com.passionate.annoyed.ruthlessness.zjd.scan

import android.app.Service
import android.content.Intent
import android.os.IBinder



class GameMiAService:Service() {

    override fun onBind(intent: Intent?): IBinder? {
        return null
    }

    override fun onCreate() {
        super.onCreate()
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        super.onStartCommand(intent, flags, startId)
        return START_STICKY
    }
}