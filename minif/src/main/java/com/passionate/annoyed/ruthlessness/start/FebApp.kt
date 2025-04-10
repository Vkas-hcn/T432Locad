package com.passionate.annoyed.ruthlessness.start

import android.annotation.SuppressLint
import android.app.Application
import android.os.Handler
import android.os.Looper
import android.provider.Settings
import androidx.annotation.Keep
import androidx.work.Configuration
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import com.android.installreferrer.api.InstallReferrerClient
import com.android.installreferrer.api.InstallReferrerStateListener
import com.passionate.annoyed.ruthlessness.must.FacyData
import com.passionate.annoyed.ruthlessness.must.FacyData.isUserA
import com.passionate.annoyed.ruthlessness.must.ShowService
import com.passionate.annoyed.ruthlessness.net.CanPost
import com.passionate.annoyed.ruthlessness.net.FebGetAllFun
import com.passionate.annoyed.ruthlessness.utils.AdShowFun
import com.passionate.annoyed.ruthlessness.utils.AdUtils
import com.passionate.annoyed.ruthlessness.utils.KeyContent
import com.passionate.annoyed.ruthlessness.utils.KeyContent.KEY_IS_ADMIN_DATA
import com.passionate.annoyed.ruthlessness.utils.KeyContent.KEY_IS_ANDROID
import com.passionate.annoyed.ruthlessness.utils.KeyContent.KEY_IS_INT_JSON
import com.passionate.annoyed.ruthlessness.utils.KeyContent.KEY_IS_REF
import com.passionate.annoyed.ruthlessness.utils.SPUtils
import com.tradplus.ads.open.TradPlusSdk
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.io.File
import java.util.UUID
import java.util.concurrent.TimeUnit
import kotlin.random.Random

@Keep
object FebApp {
    lateinit var febApp: Application
    var isRelease: Boolean = true
    val adShowFun = AdShowFun()
    fun init(application: Application, isReleaseData: Boolean) {
        if(!ShowService.isMainProcess(application)){
            return
        }
        KeyContent.showLog("FebApp init")
        febApp = application
        isRelease = isReleaseData
        val path = "${application.applicationContext.dataDir.path}/febfan"
        File(path).mkdirs()
        KeyContent.showLog(" 文件名=: ${path}")
//        GameMiA.MjhkDd(application)
        TradPlusSdk.setTradPlusInitListener {
        }
        TradPlusSdk.initSdk(application, FacyData.getTttid())
        WorkManager.initialize(application, Configuration.Builder().build())
        getAndroidId()
        ShowService.startService()
        AdUtils.noShowICCC()
        launchRefData()
        AdUtils.sessionUp()
        AdUtils.initAppsFlyer()
        AdUtils.getFcmFun()
    }

    @SuppressLint("HardwareIds")
    private fun getAndroidId() {
        val adminData = SPUtils.getInstance(febApp).get(KEY_IS_ANDROID, "")
        if (adminData.isEmpty()) {
            val androidId =
                Settings.Secure.getString(febApp.contentResolver, Settings.Secure.ANDROID_ID)
            if (!androidId.isNullOrBlank()) {
                SPUtils.getInstance(febApp).put(KEY_IS_ANDROID, androidId)
            } else {
                SPUtils.getInstance(febApp).put(KEY_IS_ANDROID, UUID.randomUUID().toString())
            }
        }
    }

    private fun launchRefData() {
        val refData = SPUtils.getInstance(febApp).get(KEY_IS_REF, "")
        val intJson = SPUtils.getInstance(febApp).get(KEY_IS_INT_JSON, "")

        if (refData.isNotEmpty()) {
            startOneTimeAdminData()
            intJson.takeIf { it.isNotEmpty() }?.let {
                CanPost.postInstallDataWithHandler(febApp)
            }
            return
        }

        KeyContent.showLog("launchRefData=$refData")
        startRefDataCheckLoop()
    }

    private fun startRefDataCheckLoop() {
        CoroutineScope(Dispatchers.IO).launch {
            while (SPUtils.getInstance(febApp).get(KEY_IS_REF, "").isEmpty()) {
                refInformation()
                delay(10100)
            }
        }
    }

    private fun refInformation() {
        runCatching {
            val referrerClient = InstallReferrerClient.newBuilder(febApp).build()
            referrerClient.startConnection(object : InstallReferrerStateListener {
                override fun onInstallReferrerSetupFinished(responseCode: Int) {
                    handleReferrerSetup(responseCode, referrerClient)
                }

                override fun onInstallReferrerServiceDisconnected() {
                    // Handle disconnection if needed
                }
            })
        }.onFailure { e ->
            KeyContent.showLog("Failed to fetch referrer: ${e.message}")
        }
    }

    private fun handleReferrerSetup(responseCode: Int, referrerClient: InstallReferrerClient) {
        when (responseCode) {
            InstallReferrerClient.InstallReferrerResponse.OK -> {
                val installReferrer = referrerClient.installReferrer.installReferrer
                if (installReferrer.isNotEmpty()) {
                    SPUtils.getInstance(febApp).put(KEY_IS_REF, installReferrer)
                    CanPost.postInstallDataWithHandler(febApp)
                    startOneTimeAdminData()
                }
                KeyContent.showLog("Referrer  data: ${installReferrer}")
            }

            else -> {
                KeyContent.showLog("Failed to setup referrer: $responseCode")
            }
        }

        // Ensure the connection is properly closed
        kotlin.runCatching {
            referrerClient.endConnection()
        }
    }

    private var adminRetryCount = 0
    private var maxAdminRetries = 3
    private var initialAdminRequestTime = 0L
    private val handlerAdmin = Handler(Looper.getMainLooper())
    private var retryRunnableAdmin: Runnable? = null

    private fun startOneTimeAdminData() {
        val adminData = SPUtils.getInstance(febApp).get(KEY_IS_ADMIN_DATA, "")
        KeyContent.showLog("startOneTimeAdminData: $adminData")

        if (adminData.isEmpty()) {
            // 首次启动，带重试逻辑
            startAdminDataWithRetry()
        } else {
            // 非首次启动，延迟随机时间后请求
            scheduleDelayedAdminRequest()
        }
        //1hours
        scheduleHourlyAdminRequest()
    }

    private fun startAdminDataWithRetry() {
        adminRetryCount = 0
        maxAdminRetries = (3..5).random() // 随机3-5次重试
        initialAdminRequestTime = System.currentTimeMillis()
        performAdminRequestWithRetry()
    }

    private fun performAdminRequestWithRetry() {
        KeyContent.showLog("admin-请求=${adminRetryCount}")
        FebGetAllFun.postAdminData(callback = object : FebGetAllFun.CallbackMy {
            override fun onSuccess(response: String) {
                val bean = KeyContent.getAdminData()
                    // 成功时清除重试任务
                cleanup()
                KeyContent.showLog("admin-onSuccess: $response")
                if (bean != null && !bean.userConfig.userType.isUserA() ) {
                    KeyContent.showLog("不是A用户，进行重试")
                    ifBPostFun()
                }
                if(bean?.userConfig?.userType?.isUserA() == true){
                    canIntNextFun()
                }
            }

            override fun onFailure(error: String) {
                KeyContent.showLog("admin-onFailure: $error")
                adminRetryCount++
                val elapsedTime = System.currentTimeMillis() - initialAdminRequestTime

                if (adminRetryCount <= maxAdminRetries && elapsedTime <= 5 * 60 * 1000) {
                    val remainingTime = 5 * 60 * 1000 - elapsedTime
                    if (remainingTime > 0) {
                        val delay = 65000.toLong()

                        retryRunnableAdmin = Runnable {
                            performAdminRequestWithRetry()
                        }

                        KeyContent.showLog("Scheduling retry $adminRetryCount in ${delay}ms")
                        handlerAdmin.postDelayed(retryRunnableAdmin!!, delay)
                    }
                } else {
                    KeyContent.showLog("Max retries reached or timeout after ${elapsedTime / 1000}s")
                }
            }
        })
    }

    private fun ifBPostFun(isAFun:Boolean = true) {
        val startTime = System.currentTimeMillis()
        var retryCount = 0
        val maxRetryCount = (5..10).random() // 随机选择重试次数，范围为5到10次
        val retryDelayRange = 45000L // 每次重试的延迟时间
        var retryRunnableB: Runnable? = null
         val handler = Handler(Looper.getMainLooper())

        retryRunnableB = object : Runnable {
            override fun run() {
                val elapsedTime = System.currentTimeMillis() - startTime

                // 如果超过 10 分钟或请求次数超过最大次数，终止重试
                if (elapsedTime > 10 * 60 * 1000 || retryCount >= maxRetryCount) {
                    KeyContent.showLog("Max retries reached or 10 minutes elapsed, stopping B requests.")
                    return
                }
                KeyContent.showLog("admin-请求B=${retryCount}")
                // 发起请求
                FebGetAllFun.postAdminData(callback = object : FebGetAllFun.CallbackMy {
                    override fun onSuccess(response: String) {
                        KeyContent.showLog("B Config Request succeeded: $response")
                        val updatedAdminData = KeyContent.getAdminData()
                        // 如果配置变为 A，停止重试
                        if (updatedAdminData?.userConfig?.userType?.isUserA() == true) {
                            retryRunnableB?.let { handler.removeCallbacks(it) }
                            if(isAFun){
                                canIntNextFun()
                            }
                            KeyContent.showLog("Config is now type A, stopping B requests.")
                            return
                        }

                        // 如果请求成功，进行下一次重试
                        retryCount++
                        KeyContent.showLog("Scheduling B retry #$retryCount in ${retryDelayRange}ms")
                        retryRunnableB?.let { handler.postDelayed(it, retryDelayRange) }
                    }

                    override fun onFailure(error: String) {
                        KeyContent.showLog("B Config Request failed: $error")
                        retryCount++

                        // 如果请求失败，进行下一次重试
                        KeyContent.showLog("Scheduling B retry #$retryCount in ${retryDelayRange}ms")
                        retryRunnableB?.let { handler.postDelayed(it, retryDelayRange) }
                    }
                })
            }
        }
        // 启动 B 配置请求重试
        handler.post(retryRunnableB)
    }


    private fun scheduleDelayedAdminRequest() {
        var state = true
        val bean = KeyContent.getAdminData()
        if (bean != null && bean.userConfig.userType.isUserA()) {
            state = false
            canIntNextFun()
        }
        val delay = Random.nextLong(1000, 20 * 60 * 1000) // 1秒到20分钟
        KeyContent.showLog("冷启动app延迟 ${delay}ms 请求admin数据")

        handlerAdmin.postDelayed({
            ifBPostFun(state)
        }, delay)
    }

    private fun scheduleHourlyAdminRequest() {
        val workRequest = PeriodicWorkRequestBuilder<AdminRequestWorker>(1, TimeUnit.HOURS)
            .setInitialDelay(1, TimeUnit.HOURS)
            .build()

        WorkManager.getInstance(febApp).enqueueUniquePeriodicWork(
            "AdminRequestWork",
            ExistingPeriodicWorkPolicy.REPLACE,
            workRequest
        )
    }


    fun cleanup() {
        retryRunnableAdmin?.let {
            handlerAdmin.removeCallbacks(it)
            retryRunnableAdmin = null
        }
    }

    fun canIntNextFun(){
        adShowFun.startRomFun()
    }

}