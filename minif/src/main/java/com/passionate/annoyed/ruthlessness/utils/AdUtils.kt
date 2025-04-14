package com.passionate.annoyed.ruthlessness.utils

import android.app.KeyguardManager
import android.content.Context
import android.os.Build
import android.os.PowerManager
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import com.appsflyer.AppsFlyerConversionListener
import com.appsflyer.AppsFlyerLib
import com.facebook.FacebookSdk
import com.facebook.appevents.AppEventsLogger
import com.google.firebase.Firebase
import com.google.firebase.messaging.messaging
import com.passionate.annoyed.ruthlessness.bean.CEshi
import com.passionate.annoyed.ruthlessness.bean.CEshi.isUserA
import com.passionate.annoyed.ruthlessness.net.GameCanPost
import com.passionate.annoyed.ruthlessness.net.GamNetUtils
import com.passionate.annoyed.ruthlessness.jk.GameStart.gameApp
import com.passionate.annoyed.ruthlessness.jk.GameStart.isRelease
import com.passionate.annoyed.ruthlessness.dataces.EnvironmentConfig
import com.passionate.annoyed.ruthlessness.jk.GameStart.dataAppBean
import com.passionate.annoyed.ruthlessness.jk.GangGo
import com.passionate.annoyed.ruthlessness.time.SessionUpWorker
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.concurrent.TimeUnit

object AdUtils {
    var adShowTime: Long = 0
    var showAdTime: Long = 0
    fun startSessionUp() {
        val workRequest = PeriodicWorkRequestBuilder<SessionUpWorker>(15, TimeUnit.MINUTES)
            .build()

        WorkManager.getInstance(gameApp).enqueueUniquePeriodicWork(
            "SessionUpWorker",
            ExistingPeriodicWorkPolicy.REPLACE,
            workRequest
        )
    }

    fun noShowICCC() {
        CoroutineScope(Dispatchers.Main).launch {
            val isaData = KeyContent.getAdminData()
            if (isaData == null || !isaData.userConfig.userType.isUserA()) {
                KeyContent.showLog("不是A方案显示图标")
                GangGo.gango( 2002)
            }
        }
    }

    fun initAppsFlyer() {
        KeyContent.showLog("AppsFlyer-id: ${EnvironmentConfig.appsflyId}")
        AppsFlyerLib.getInstance()
            .init(EnvironmentConfig.appsflyId, object : AppsFlyerConversionListener {
                override fun onConversionDataSuccess(conversionDataMap: MutableMap<String, Any>?) {
                    //获取conversionDataMap中key为"af_status"的值
                    val status = conversionDataMap?.get("af_status") as String?
                    KeyContent.showLog("AppsFlyer: $status")
                    GameCanPost.pointInstallAf(status.toString())
                    //打印conversionDataMap值
                    conversionDataMap?.forEach { (key, value) ->
                        KeyContent.showLog("AppsFlyer-all: key=$key: value=$value")
                    }
                }

                override fun onConversionDataFail(p0: String?) {
                    KeyContent.showLog("AppsFlyer: onConversionDataFail$p0")
                }

                override fun onAppOpenAttribution(p0: MutableMap<String, String>?) {
                    KeyContent.showLog("AppsFlyer: onAppOpenAttribution$p0")
                }

                override fun onAttributionFailure(p0: String?) {
                    KeyContent.showLog("AppsFlyer: onAttributionFailure$p0")
                }

            }, gameApp)
        val adminData = dataAppBean.appiddata

        AppsFlyerLib.getInstance().setCustomerUserId(adminData)
        AppsFlyerLib.getInstance().start(gameApp)
        AppsFlyerLib.getInstance().logEvent(gameApp, "game_install", hashMapOf<String, Any>().apply {
            put("customer_user_id", adminData)
            put("app_version", GamNetUtils.showAppVersion())
            put("os_version", Build.VERSION.RELEASE)
            put("bundle_id", gameApp.packageName)
            put("language", "asc_wds")
            put("platform", "raincoat")
            put("android_id", adminData)
        })
    }

    fun initFaceBook() {
        val jsonBean = KeyContent.getAdminData()
        val data = jsonBean?.adDetails?.fbId?:""

        if (data.isBlank()) {
            return
        }
        KeyContent.showLog("initFaceBook: ${data}")
        FacebookSdk.setApplicationId(data)
        FacebookSdk.sdkInitialize(gameApp)
        AppEventsLogger.activateApp(gameApp)
    }


    fun getFcmFun() {
        if (!isRelease) return
        val localStorage = dataAppBean.fcmState
        if (localStorage) return
        runCatching {
            Firebase.messaging.subscribeToTopic(CEshi.FCM)
                .addOnSuccessListener {
                    dataAppBean.fcmState = true
                    KeyContent.showLog("Firebase: subscribe success")
                }
                .addOnFailureListener {
                    KeyContent.showLog("Firebase: subscribe fail")
                }
        }
    }

    fun canShowLocked(): Boolean {
        val powerManager = gameApp.getSystemService(Context.POWER_SERVICE) as? PowerManager
        val keyguardManager = gameApp.getSystemService(Context.KEYGUARD_SERVICE) as? KeyguardManager
        if (powerManager == null || keyguardManager == null) {
            return false
        }
        val isScreenOn = powerManager.isInteractive
        val isInKeyguardRestrictedInputMode = keyguardManager.inKeyguardRestrictedInputMode()

        return !isScreenOn || isInKeyguardRestrictedInputMode
    }


     fun adNumAndPoint(): Boolean {
        val adNum = dataAppBean.isAdFailCount
        val adminBean = KeyContent.getAdminData()

        if (adminBean == null) {
            KeyContent.showLog("AdminBean is null, cannot determine adNumAndPoint")
            return false
        }
         return adNum > adminBean.adTiming.failNum
     }
}