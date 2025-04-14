package com.passionate.annoyed.ruthlessness.net

import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import com.passionate.annoyed.ruthlessness.dataces.EnhancedShowService
import com.passionate.annoyed.ruthlessness.net.GamNetUtils.showAppVersion
import com.passionate.annoyed.ruthlessness.jk.GameStart.gameApp
import com.passionate.annoyed.ruthlessness.utils.AdUtils
import com.passionate.annoyed.ruthlessness.utils.KeyContent
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
import com.passionate.annoyed.ruthlessness.jk.GameStart.dataAppBean
import com.tradplus.ads.base.bean.TPAdInfo
import java.math.BigDecimal
import java.util.Currency

object GameCanPost {


    private fun topJsonData(context: Context, isInstall: Boolean = false): JSONObject {
        val sao = JSONObject().apply {
            //bundle_id
            put("region", context.packageName)
            //gaid
            put("agnes", "")
            //device_model//传空值
            put("stun", "")
            //operator 传假值字符串
            put("michele", "555555")
            //log_id
            put("atheism", UUID.randomUUID().toString())

        }

        val flour = JSONObject().apply {
            //manufacturer
            put("memo", Build.MANUFACTURER)
            //android_id
            put("chimera", dataAppBean.appiddata)
            //client_ts
            put("satyr", System.currentTimeMillis())

        }
        val bawl = JSONObject().apply {
            //app_version
            put("dyne", showAppVersion())
            //system_language//假值
            put("stagnate", "asc_wds")
            //os
            put("tendon", "diatribe")
        }
        val debate = JSONObject().apply {
            //distinct_id
            put("aesthete", dataAppBean.appiddata)
            //os_version
            put("surreal", Build.VERSION.RELEASE)
            //brand
            put("supreme", "xxx")
        }
        val json = JSONObject().apply {
            put("sao", sao)
            put("flour", flour)
            put("bawl", bawl)
            put("debate", debate)
        }

        return json
    }


    private fun upInstallJson(context: Context): String {
        val grotto = JSONObject().apply {
            //build
            put("pagan", "build/${Build.ID}")

            //referrer_url
            put("orr", dataAppBean.refdata)

            //user_agent
            put("recipe", "")

            //lat
            put("terre", "assassin")

            //referrer_click_timestamp_seconds
            put("marque", 0)

            //install_begin_timestamp_seconds
            put("siam", 0)

            //referrer_click_timestamp_server_seconds
            put("armpit", 0)

            //install_begin_timestamp_server_seconds
            put("callisto", 0)

            //install_first_seconds
            put("suds", getFirstInstallTime(context))

            //last_update_seconds
            put("headwall", 0)
        }
        return topJsonData(context, true).apply {
            put("grotto", grotto)
        }.toString()
    }


    private fun upAdJson(context: Context, adValue: TPAdInfo): String {
        val specie = JSONObject().apply {
            //ad_pre_ecpm
            put("susie", adValue.ecpm.toDouble() * 1000)
            //currency
            put("workbook", "USD")
            //ad_network
            put("furlong", adValue.adSourceName)
            //ad_source
            put("vary", "Tradplus")
            //ad_code_id
            put("lamprey", adValue.tpAdUnitId)
            //ad_pos_id
            put("upstate", "int")
            //ad_rit_id
            put("bogging", "")
            //ad_sense
            put("ban", "")
            //ad_format
            put("stair", adValue.format)
        }
        return topJsonData(context, true).apply {
            put("specie", specie)
        }.toString()
    }

    private fun upPointJson(name: String): String {
        return topJsonData(gameApp).apply {
            put("femoral", name)
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

        return topJsonData(gameApp).apply {
            put("femoral", name)

            put("yah", JSONObject().apply {
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
        val is_int_ref = dataAppBean.IS_INT_JSON
        val data = is_int_ref.ifEmpty {
            val newData = upInstallJson(context)
            dataAppBean.IS_INT_JSON = newData
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
                        GamNetUtils.postPutData(data, object : GamNetUtils.CallbackMy {
                            override fun onSuccess(response: String) {
                                KeyContent.showLog("Install-请求成功: $response")
                                dataAppBean.IS_INT_JSON = ""
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
        val data = upAdJson(gameApp, adValue)
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
                        GamNetUtils.postPutData(data, object : GamNetUtils.CallbackMy {
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

        if (!isAdMinCon && (adminBean!=null && !adminBean.userConfig.canUpload)) {
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
                        GamNetUtils.postPutData(data, object : GamNetUtils.CallbackMy {
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
                AppEventsLogger.newLogger(gameApp).logPurchase(
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
        if (dataAppBean.firstPoint) {
            return
        }
        val instalTime = EnhancedShowService.getInstallTimeInSeconds()
        postPointDataWithHandler(true, "first_start", "time", instalTime)
        dataAppBean.firstPoint= true
    }

    fun pointInstallAf(data: String) {
        if (data.contains("non_organic", true) && !dataAppBean.adOrgPoint) {
            postPointDataWithHandler(true, "non_organic")
            dataAppBean.adOrgPoint=true
        }
    }

    fun getLiMitData() {
        if (!dataAppBean.getlimit) {
            postPointDataWithHandler(false, "getlimit")
            dataAppBean.getlimit = true
        }
    }
}