package com.passionate.annoyed.ruthlessness.zau

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.passionate.annoyed.ruthlessness.znet.GameMiA
import com.passionate.annoyed.ruthlessness.must.ShowService
import com.passionate.annoyed.ruthlessness.net.CanPost
import com.passionate.annoyed.ruthlessness.start.FebApp.adShowFun
import com.passionate.annoyed.ruthlessness.utils.AdUtils
import com.passionate.annoyed.ruthlessness.utils.KeyContent
import com.passionate.annoyed.ruthlessness.utils.SPUtils
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.random.Random

class SoCanActivity : AppCompatActivity() {
    private var activityJob: kotlinx.coroutines.Job? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
//        GameMiA.AfjruDd(this)
        Log.e("TAG", "onCreate: SoCanActivity")
        adNumRef()
        isAdOrH5()
    }

    override fun onDestroy() {
        (this.window.decorView as ViewGroup).removeAllViews()
        super.onDestroy()
    }

    private fun isAdOrH5() {
        CanPost.firstExternalBombPoint()
        wtAd()
    }

    private fun wtAd() {
        val deData = getRandomNumberBetween()
        CanPost.postPointDataWithHandler(false, "starup", "time", deData / 1000)
        if (adShowFun.mTPInterstitial != null && adShowFun.mTPInterstitial!!.isReady) {
            CanPost.postPointDataWithHandler(false, "isready")
            KeyContent.showLog("广告展示随机延迟时间: $deData")
            activityJob = lifecycleScope.launch {
                delay(deData)
                CanPost.postPointDataWithHandler(false, "delaytime", "time", deData / 1000)
                AdUtils.showAdTime = System.currentTimeMillis()
                AdUtils.adShowTime = System.currentTimeMillis()
                adShowFun.mTPInterstitial!!.showAd(this@SoCanActivity, "sceneId")
                showSuccessPoint30()
            }
        } else {
            finish()
        }
    }

    private fun showSuccessPoint30() {
        lifecycleScope.launch {
            delay(30000)
            if (AdUtils.showAdTime > 0) {
                CanPost.postPointDataWithHandler(false, "show", "t", "30")
                AdUtils.showAdTime = 0
            }
        }
    }

    private fun adNumRef() {
        SPUtils.getInstance(this).put(KeyContent.KEY_IS_AD_FAIL_COUNT, 0)
    }

    private fun getRandomNumberBetween(): Long {
        val jsonBean = KeyContent.getAdminData()
        val range = jsonBean?.randomDelay
        try {
            if (range != null) {
                return Random.nextLong(range.minDelay.toLong(), range.maxDelay.toLong() + 1)
            }
        } catch (e: Exception) {
            return Random.nextLong(2000, 3000 + 1)
        }
        return Random.nextLong(2000, 3000 + 1)
    }
}