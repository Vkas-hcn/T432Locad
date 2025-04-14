package com.passionate.annoyed.ruthlessness.utils.show

import android.content.Context
import com.tradplus.ads.base.bean.TPAdError
import com.tradplus.ads.base.bean.TPAdInfo
import com.tradplus.ads.open.interstitial.InterstitialAdListener
import com.tradplus.ads.open.interstitial.TPInterstitial

// 广告状态管理接口
interface AdStateManager {
    fun canShowAd(checkClickLimit: Boolean = true): Boolean
    fun recordAdShown()
    fun recordAdClicked()
}

// 广告加载策略接口
interface AdLoadingStrategy {
    fun shouldLoadAd(currentTime: Long): Boolean
    fun handleLoadSuccess()
    fun handleLoadFailure(errorMsg: String)
}

// 广告展示条件检查策略
interface DisplayConditionStrategy {
    fun checkConditions(): Boolean
}

// 广告事件监听器
interface AdEventListener {
    fun onAdEvent(eventType: String, data: Map<String, Any> = emptyMap())
}

// 广告加载器（核心功能封装）
class AdLoader(
    private val context: Context,
    private val adState: AdStateManager,
    private val loadingStrategy: AdLoadingStrategy,
    private val eventListener: AdEventListener
) : InterstitialAdListener {

    private var tpInterstitial: TPInterstitial? = null
    private var lastLoadTime = 0L
    var hasValidAd = false

    fun initialize(adId: String) {
        if (tpInterstitial == null) {
            tpInterstitial = TPInterstitial(context, adId).apply {
                setAdListener(this@AdLoader)
            }
        }
    }

    fun loadAd() {
        if (!adState.canShowAd()) {
            eventListener.onAdEvent("load_skip", mapOf("reason" to "show_limit"))
            return
        }

        val currentTime = System.currentTimeMillis()
        if (loadingStrategy.shouldLoadAd(currentTime)) {
            tpInterstitial?.loadAd()
            eventListener.onAdEvent("load_start")
        }
    }

    override fun onAdLoaded(tpAdInfo: TPAdInfo) {
        loadingStrategy.handleLoadSuccess()
        eventListener.onAdEvent("load_success")
    }

    override fun onAdFailed(tpAdError: TPAdError) {
        loadingStrategy.handleLoadFailure(tpAdError.errorMsg)
        eventListener.onAdEvent("load_failed", mapOf("error" to tpAdError.errorMsg))
    }

    override fun onAdImpression(p0: TPAdInfo?) {
        TODO("Not yet implemented")
    }

    override fun onAdClicked(p0: TPAdInfo?) {
        TODO("Not yet implemented")
    }

    override fun onAdClosed(p0: TPAdInfo?) {
        TODO("Not yet implemented")
    }

    override fun onAdVideoError(p0: TPAdInfo?, p1: TPAdError?) {
        TODO("Not yet implemented")
    }

    override fun onAdVideoStart(p0: TPAdInfo?) {
        TODO("Not yet implemented")
    }

    override fun onAdVideoEnd(p0: TPAdInfo?) {
        TODO("Not yet implemented")
    }

    // 其他回调方法实现类似...
}