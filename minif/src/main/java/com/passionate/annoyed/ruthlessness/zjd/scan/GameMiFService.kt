package com.passionate.annoyed.ruthlessness.zjd.scan

import android.annotation.SuppressLint
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.Context
import android.content.Intent
import android.os.IBinder
import androidx.core.app.NotificationCompat
import android.widget.RemoteViews
import com.passionate.annoyed.ruthlessness.R
import com.passionate.annoyed.ruthlessness.jk.GameStart.KEY_IS_SERVICE
import com.passionate.annoyed.ruthlessness.utils.KeyContent

class GameMiFService : Service() {
    @SuppressLint("ForegroundServiceType", "RemoteViewLayout")
    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        KeyContent.showLog("FebFiveFffService onStartCommand-1=${KEY_IS_SERVICE}")
        if (!KEY_IS_SERVICE) {
            KEY_IS_SERVICE =true
            val channel = NotificationChannel("gamencan", "gamencan", NotificationManager.IMPORTANCE_MIN)
            channel.setSound(null, null)
            channel.enableLights(false)
            channel.enableVibration(false)
            (application.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager).run {
                if (getNotificationChannel(channel.toString()) == null) createNotificationChannel(channel)
            }
            runCatching {
                startForeground(
                    8888,
                    NotificationCompat.Builder(this, "gamencan").setSmallIcon(R.drawable.shape_no)
                        .setContentText("")
                        .setContentTitle("")
                        .setOngoing(true)
                        .setCustomContentView(RemoteViews(packageName, R.layout.layout_no))
                        .build()
                )
            }
            KeyContent.showLog("FebFiveFffService onStartCommand-2=${KEY_IS_SERVICE}")
        }
        return super.onStartCommand(intent, flags, startId)
    }


    override fun onBind(intent: Intent?): IBinder? {
        return null
    }


}
