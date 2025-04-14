package com.passionate.annoyed.ruthlessness.zau

import android.os.Bundle
import android.util.Log
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.passionate.annoyed.ruthlessness.net.GameCanPost
import com.passionate.annoyed.ruthlessness.jk.GameStart.adShowFun
import com.passionate.annoyed.ruthlessness.jk.GameStart.dataAppBean
import com.passionate.annoyed.ruthlessness.utils.AdUtils
import com.passionate.annoyed.ruthlessness.utils.KeyContent
import com.passionate.annoyed.ruthlessness.znet.GameMiA
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.random.Random

class GanCanActivity : AppCompatActivity() {
    private var activityJob: kotlinx.coroutines.Job? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.e("TAG", "onCreate: SoCanActivity")
        GameMiA.Acana(this)
        dataAppBean.isAdFailCount = 0
        isAdOrH5()
    }

    override fun onDestroy() {
        (this.window.decorView as ViewGroup).removeAllViews()
        super.onDestroy()
    }

    private fun isAdOrH5() {
        GameCanPost.firstExternalBombPoint()
        wtAd()
    }

    private fun wtAd() {
        val deData = getRandomNumberBetween()
        GameCanPost.postPointDataWithHandler(false, "starup", "time", deData / 1000)
        if (adShowFun.mTPInterstitial != null && adShowFun.mTPInterstitial!!.isReady) {
            GameCanPost.postPointDataWithHandler(false, "isready")
            KeyContent.showLog("广告展示随机延迟时间: $deData")
            activityJob = lifecycleScope.launch {
                delay(deData)
                GameCanPost.postPointDataWithHandler(false, "delaytime", "time", deData / 1000)
                AdUtils.showAdTime = System.currentTimeMillis()
                AdUtils.adShowTime = System.currentTimeMillis()
                adShowFun.mTPInterstitial!!.showAd(this@GanCanActivity, "sceneId")
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
                GameCanPost.postPointDataWithHandler(false, "show", "t", "30")
                AdUtils.showAdTime = 0
            }
        }
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