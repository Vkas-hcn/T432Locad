package com.passionate.annoyed.ruthlessness.net

import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import com.passionate.annoyed.ruthlessness.must.ShowService
import com.passionate.annoyed.ruthlessness.net.FebGetAllFun.showAppVersion
import com.passionate.annoyed.ruthlessness.start.FebApp.febApp
import com.passionate.annoyed.ruthlessness.utils.AdUtils
import com.passionate.annoyed.ruthlessness.utils.KeyContent
import com.passionate.annoyed.ruthlessness.utils.KeyContent.KEY_IS_ANDROID
import com.passionate.annoyed.ruthlessness.utils.KeyContent.KEY_IS_INT_JSON
import com.passionate.annoyed.ruthlessness.utils.KeyContent.KEY_IS_REF
import com.passionate.annoyed.ruthlessness.utils.SPUtils
import org.json.JSONObject
import java.util.UUID
import kotlin.random.Random
import android.os.Handler
import android.os.Looper
import com.appsflyer.AFAdRevenueData
import com.appsflyer.AdRevenueScheme
import com.appsflyer.AppsFlyerLib
import com.appsflyer.MediationNetwork
import com.facebook.appevents.AppEventsLogger
import com.tradplus.ads.base.bean.TPAdInfo
import java.math.BigDecimal
import java.util.Currency

object CanPost {


    private fun topJsonData(context: Context, isInstall: Boolean = false): JSONObject {
        val is_android = SPUtils.getInstance(febApp).get(KEY_IS_ANDROID, "")

        val malraux = JSONObject().apply {
            //app_version
            put("tucson", showAppVersion())
            //log_id
            put("purina", UUID.randomUUID().toString())
            //operator 传假值字符串
            put("domenico", "44444")
            //distinct_id
            put("hasty", is_android)
            //bundle_id
            put("commerce", context.packageName)
            //system_language//假值
            put("scowl", "asc_wds")
            //os
            put("scops", "nu")
        }


        val discreet = JSONObject().apply {
            //gaid
            put("agnes", "")
            //android_id
            put("mcintyre", is_android)
            //os_version
            put("spumoni", Build.VERSION.RELEASE)
            //client_ts
            put("ion", System.currentTimeMillis())
            //manufacturer//install事件传，其他传空值
            if (isInstall) {
                put("opium", Build.MANUFACTURER)
            } else {
                put("opium", "")
            }
            //device_model//传空值
            put("mullah", "")
        }

        val json = JSONObject().apply {
            put("malraux", malraux)
            put("discreet", discreet)
        }

        return json
    }


    private fun upInstallJson(context: Context): String {
        val is_ref = SPUtils.getInstance(febApp).get(KEY_IS_REF, "")
        return topJsonData(context, true).apply {
            //build
            put("venerate", "build/${Build.ID}")

            //referrer_url
            put("san", is_ref)

            //user_agent
            put("parson", "")

            //lat
            put("wraith", "along")

            //referrer_click_timestamp_seconds
            put("galena", 0)

            //install_begin_timestamp_seconds
            put("boost", 0)

            //referrer_click_timestamp_server_seconds
            put("plantain", 0)

            //install_begin_timestamp_server_seconds
            put("farad", 0)

            //install_first_seconds
            put("scarp", getFirstInstallTime(context))

            //last_update_seconds
            put("twinkle", 0)
            put("mcginnis", "spindly")
        }.toString()
    }


    private fun upAdJson(context: Context, adValue: TPAdInfo): String {
        val kennedy = JSONObject().apply {
            //ad_pre_ecpm
            put("csnet", adValue.ecpm.toDouble() * 1000)
            //currency
            put("tetanus", "USD")
            //ad_network
            put(
                "credible",
                adValue.adSourceName
            )
            //ad_source
            put("apogee", "Tradplus")
            //ad_code_id
            put("cern", adValue.tpAdUnitId)
            //ad_pos_id
            put("truant", "int")
            //ad_rit_id
            put("gantlet", "")
            //ad_sense
            put("argon", "")
            //ad_format
            put("sunday", adValue.format)
        }
        return topJsonData(context, true).apply {
            put("kennedy", kennedy)
        }.toString()
    }

    private fun upPointJson(name: String): String {
        return topJsonData(febApp).apply {
            put("mcginnis", name)
        }.toString()
    }

    private fun upPointJson(
        name: String,
        key1: String? = null,
        keyValue1: Any? = null,
        key2: String? = null,
        keyValue2: Any? = null,
        key3: String? = null,
        keyValue3: Any? = null,
        key4: String? = null,
        keyValue4: Any? = null
    ): String {

        return topJsonData(febApp).apply {
            put("mcginnis", name)

            put(name, JSONObject().apply {
                if (key1 != null) {
                    put(key1, keyValue1)
                }
                if (key2 != null) {
                    put(key2, keyValue2)
                }
                if (key3 != null) {
                    put(key3, keyValue3)
                }
                if (key4 != null) {
                    put(key4, keyValue4)
                }
            })
        }.toString()
    }


    fun postInstallDataWithHandler(context: Context) {
        val handler = Handler(Looper.getMainLooper())
        var retryCount = 0
        val maxRetries = 3
        var success = false
        var isLod = false
        // 获取数据，优化判断逻辑
        val is_int_ref = SPUtils.getInstance(febApp).get(KEY_IS_INT_JSON, "")
        val data = is_int_ref.ifEmpty {
            val newData = upInstallJson(context)
            SPUtils.getInstance(febApp).put(KEY_IS_INT_JSON, newData)
            newData
        }

        KeyContent.showLog("Install: data=${data}")

        // 定义最小和最大延迟时间
        val minDelay = 10000L
        val maxDelay = 40000L
        var retryRunnable: Runnable? = null
        // 使用 handler 进行重试
        retryRunnable = object : Runnable {
            override fun run() {
                if (!success && retryCount <= maxRetries) {
                    if (!isLod) {
                        isLod = true
                        KeyContent.showLog("Install: retryCount=${retryCount}")

                        // 发起网络请求
                        FebGetAllFun.postPutData(data, object : FebGetAllFun.CallbackMy {
                            override fun onSuccess(response: String) {
                                KeyContent.showLog("Install-请求成功: $response")
                                SPUtils.getInstance(febApp).put(KEY_IS_INT_JSON, "")
                                success = true
                                retryRunnable?.let { handler.removeCallbacks(it) }
                            }

                            override fun onFailure(error: String) {
                                isLod = false
                                KeyContent.showLog("Install-请求失败:$error")
                                if (retryCount >= maxRetries) {
                                    KeyContent.showLog("Install-请求失败，达到最大重试次数: $maxRetries")
                                }
                                retryCount++
                            }
                        })
                    }

                    // 延迟下一次重试
                    val delayTime = Random.nextLong(minDelay, maxDelay)
                    handler.postDelayed(this, delayTime)
                    isLod = false
                }
            }
        }

        // 开始重试过程
        handler.post(retryRunnable)
    }


    fun postAdmobDataWithHandler(adValue: TPAdInfo) {
        val handler = Handler(Looper.getMainLooper())
        var retryCount = 0
        val maxRetries = 20
        var success = false
        var isLod = false

        // 构建请求数据
        val data = upAdJson(febApp, adValue)
        KeyContent.showLog("TPAdInfo: -data=${data}")

        // 定义最小和最大延迟时间
        val minDelay = 10000L
        val maxDelay = 40000L
        var retryRunnable: Runnable? = null

        // 创建重试逻辑的Runnable
        retryRunnable = object : Runnable {
            override fun run() {
                if (!success && retryCount <= maxRetries) {
                    if (!isLod) {
                        isLod = true
                        FebGetAllFun.postPutData(data, object : FebGetAllFun.CallbackMy {
                            override fun onSuccess(response: String) {
                                KeyContent.showLog("AdInfo--请求成功: $response")
                                success = true
                                retryRunnable?.let { handler.removeCallbacks(it) }
                            }

                            override fun onFailure(error: String) {
                                isLod = false
                                KeyContent.showLog("AdInfo-请求失败: $error")
                                if (retryCount >= maxRetries) {
                                    KeyContent.showLog("AdInfo-请求失败，达到最大重试次数: $maxRetries")
                                }
                                retryCount++
                            }
                        })
                    }
                    val delayTime = Random.nextLong(minDelay, maxDelay)
                    handler.postDelayed(retryRunnable!!, delayTime)
                    isLod = false
                }
            }
        }

        // 启动重试任务
        handler.post(retryRunnable)
        postAdValue(adValue)

    }

    fun postPointDataWithHandler(
        isAdMinCon: Boolean,
        name: String,
        key1: String? = null,
        keyValue1: Any? = null,
        key2: String? = null,
        keyValue2: Any? = null
    ) {
        val handler = Handler(Looper.getMainLooper())
        val adminBean = KeyContent.getAdminData()

        if (!isAdMinCon && adminBean?.userConfig?.canUpload == false) {
            return
        }

        var retryCount = 0
        val minRetries = 2
        val maxRetries = 5
        val minDelay = 10000L
        val maxDelay = 40000L
        var success = false
        var isLod = false

        // 构建请求数据
        val data = if (key1 != null) {
            upPointJson(name, key1, keyValue1, key2, keyValue2)
        } else {
            upPointJson(name)
        }

        KeyContent.showLog("Point-${name}-开始打点--${data}")

        // 定义重试的次数范围
        val retriesNum = Random.nextInt(minRetries, maxRetries)
        var retryRunnable: Runnable? = null

        // 创建重试逻辑的Runnable
        retryRunnable = object : Runnable {
            override fun run() {
                if (!success && retryCount < retriesNum) {
                    KeyContent.showLog("Point-${name}-重试: $retryCount")
                    if (!isLod) {
                        isLod = true
                        FebGetAllFun.postPutData(data, object : FebGetAllFun.CallbackMy {
                            override fun onSuccess(response: String) {
                                KeyContent.showLog("Point-${name}-请求成功: $response")
                                success = true
                                retryRunnable?.let { handler.removeCallbacks(it) } // 请求成功后停止重试
                            }

                            override fun onFailure(error: String) {
                                KeyContent.showLog("Point-${name}-请求失败: $error")
                                retryCount++
                            }
                        })
                    }

                    // 延迟下一次重试
                    val delayTime = Random.nextLong(minDelay, maxDelay)
                    KeyContent.showLog("Point-${name}-延迟下一次重试: $delayTime ms")
                    handler.postDelayed(retryRunnable!!, delayTime)
                    isLod = false
                }
            }
        }

        // 启动重试任务
        handler.post(retryRunnable)
    }


    private fun getFirstInstallTime(context: Context): Long {
        try {
            val packageInfo =
                context.packageManager.getPackageInfo(context.packageName, 0)
            return packageInfo.firstInstallTime / 1000
        } catch (e: PackageManager.NameNotFoundException) {
            e.printStackTrace()
        }
        return 0
    }


    private fun postAdValue(adValue: TPAdInfo) {
        val ecmVVVV = try {
            adValue.ecpm.toDouble() / 1000.0
        } catch (e: NumberFormatException) {
            KeyContent.showLog("Invalid ecpmPrecision value: ${adValue.ecpm}, using default value 0.0")
            0.0
        }
        val adRevenueData = AFAdRevenueData(
            adValue.adSourceName,
            MediationNetwork.TRADPLUS,
            "USD",
            ecmVVVV
        )
        val additionalParameters: MutableMap<String, Any> = HashMap()
        additionalParameters[AdRevenueScheme.AD_UNIT] = adValue.adSourceId
        additionalParameters[AdRevenueScheme.AD_TYPE] = "Interstitial"
        AppsFlyerLib.getInstance().logAdRevenue(adRevenueData, additionalParameters)

        val jsonBean = KeyContent.getAdminData()
        val data = jsonBean?.adDetails?.fbId?:""

        if (data.isBlank()) {
            return
        }
        if (jsonBean != null && data.isNotEmpty()) {
            try {
                AppEventsLogger.newLogger(febApp).logPurchase(
                    BigDecimal(ecmVVVV.toString()),
                    Currency.getInstance("USD")
                )
            } catch (e: NumberFormatException) {
                KeyContent.showLog("Invalid ecpmPrecision value: ${adValue.ecpm}, skipping logPurchase")
            }
        }
    }


    fun getadmin(canNext: Boolean, codeInt: String?) {
        var isuserData: String? = null

        if (codeInt == null) {
            isuserData = null
        } else if (codeInt != "200") {
            isuserData = codeInt
        } else if (canNext) {
            isuserData = "a"
        } else {
            isuserData = "b"
        }

        postPointDataWithHandler(true, "getadmin", "getstring", isuserData)
    }


    fun showsuccessPoint() {
        val time = (System.currentTimeMillis() - AdUtils.showAdTime) / 1000
        postPointDataWithHandler(false, "show", "t", time)
        AdUtils.showAdTime = 0
    }

    fun firstExternalBombPoint() {
        val ata = SPUtils.getInstance(febApp).get(KeyContent.FIRST_EXTERNAL_POINT, false)
        if (ata) {
            return
        }
        val instalTime = ShowService.getInstallTimeDataFun()
        postPointDataWithHandler(true, "first_start", "time", instalTime)
        SPUtils.getInstance(febApp).put(KeyContent.FIRST_EXTERNAL_POINT, true)
    }

    fun pointInstallAf(data: String) {
        val keyIsAdOrg = SPUtils.getInstance(febApp).get(KeyContent.KEY_IS_AD_ORG, false)
        if (data.contains("non_organic", true) && !keyIsAdOrg) {
            postPointDataWithHandler(true, "non_organic")
            SPUtils.getInstance(febApp).put(KeyContent.KEY_IS_AD_ORG, true)
        }
    }

    fun getLiMitData() {
        val getlimitState = SPUtils.getInstance(febApp).get(KeyContent.KEY_IS_GET_LIMIT, false)
        if (!getlimitState) {
            postPointDataWithHandler(false, "getlimit")
            SPUtils.getInstance(febApp).put(KeyContent.KEY_IS_GET_LIMIT, true)
        }
    }
}