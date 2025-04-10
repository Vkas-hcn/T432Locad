package com.passionate.annoyed.ruthlessness.zjd.rkiu

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Parcelable
import androidx.annotation.Keep

@Keep
class GameMiMRecent: BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        if (intent.hasExtra("f")) {
            val eIntent = intent.getParcelableExtra<Parcelable>("f") as Intent?
            if (eIntent != null) {
                try {
                    context.startActivity(eIntent)
                    return
                } catch (e: Exception) {
                }
            }
        }
    }
}