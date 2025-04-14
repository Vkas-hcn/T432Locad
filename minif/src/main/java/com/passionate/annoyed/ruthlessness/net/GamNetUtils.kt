package com.passionate.annoyed.ruthlessness.net


import android.annotation.SuppressLint
import com.google.gson.Gson

import okhttp3.*
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONObject
import java.nio.charset.StandardCharsets
import android.util.Base64
import com.passionate.annoyed.ruthlessness.bean.AllDataBean
import com.passionate.annoyed.ruthlessness.bean.CEshi.isUserA
import com.passionate.annoyed.ruthlessness.jk.GameStart.gameApp
import com.passionate.annoyed.ruthlessness.dataces.EnvironmentConfig
import com.passionate.annoyed.ruthlessness.jk.GameStart.dataAppBean
import com.passionate.annoyed.ruthlessness.utils.KeyContent
import java.io.IOException
import java.util.concurrent.TimeUnit

object GamNetUtils {

    interface CallbackMy {
        fun onSuccess(response: String)
        fun onFailure(error: String)
    }
    fun showAppVersion(): String {
        return gameApp.packageManager.getPackageInfo(gameApp.packageName, 0).versionName?:""
    }



    @SuppressLint("HardwareIds")
    fun adminData(): String {
        return JSONObject().apply {
            put("VmtaavRj", "com.DefaultCompany.SushiTile")
            put("jGRdfTcPBV", dataAppBean.appiddata)
            put("elq", dataAppBean.refdata)
//            put("elq", "fb4a")
            put("RmJceLwW", showAppVersion())
        }.toString()
    }

    val client = OkHttpClient.Builder()
        .connectTimeout(60, TimeUnit.SECONDS)
        .readTimeout(60, TimeUnit.SECONDS)
        .writeTimeout(60, TimeUnit.SECONDS)
        .build()
    fun postAdminData(callback: CallbackMy) {
        KeyContent.showLog("postAdminData=${EnvironmentConfig.adminUrl}=${adminData()}")
        val jsonBodyString = JSONObject(adminData()).toString()
        val timestamp = System.currentTimeMillis().toString()
        val xorEncryptedString = jxData(jsonBodyString, timestamp)
        val base64EncodedString = Base64.encodeToString(
            xorEncryptedString.toByteArray(StandardCharsets.UTF_8),
            Base64.NO_WRAP
        )


        val mediaType = "application/json; charset=utf-8".toMediaTypeOrNull()
        val requestBody = base64EncodedString.toRequestBody(mediaType)

        val request = Request.Builder()
            .url(EnvironmentConfig.adminUrl)
            .post(requestBody)
            .addHeader("timestamp", timestamp)
            .build()
        GameCanPost.postPointDataWithHandler(false, "reqadmin")
        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                KeyContent.showLog("admin----Request failed: ${e.message}")

                callback.onFailure("Request failed: ${e.message}")
                GameCanPost.getadmin(false,"timeout")
            }

            override fun onResponse(call: Call, response: Response) {
                if (!response.isSuccessful) {
                    callback.onFailure("Unexpected code $response")
                    GameCanPost.getadmin(false,response.code.toString())
                    return
                }
                try {
                    val timestampResponse = response.header("timestamp")
                        ?: throw IllegalArgumentException("Timestamp missing in headers")

                    val decodedBytes = Base64.decode(response.body?.string() ?: "", Base64.DEFAULT)
                    val decodedString = String(decodedBytes, Charsets.UTF_8)
                    val finalData = jxData(decodedString, timestampResponse)
                    val jsonResponse = JSONObject(finalData)
                    val jsonData = parseAdminRefData(jsonResponse.toString())
                    val adminBean = runCatching {
                        Gson().fromJson(jsonData, AllDataBean::class.java)
                    }.getOrNull()

                    if (adminBean == null) {
                        callback.onFailure("The data is not in the correct format")
                        GameCanPost.getadmin(false,null)

                    } else {
                        if (KeyContent.getAdminData()==null) {
                            KeyContent.putAdminData(jsonData)
                        } else if (adminBean.userConfig.userType.isUserA()) {
                            KeyContent.putAdminData(jsonData)
                        }
                        GameCanPost.getadmin(adminBean.userConfig.userType.isUserA(),response.code.toString())

                        callback.onSuccess(jsonData)
                    }
                } catch (e: Exception) {
                    callback.onFailure("Decryption failed: ${e.message}")
                }
            }
        })

    }

    private fun jxData(text: String, timestamp: String): String {
        val cycleKey = timestamp.toCharArray()
        val keyLength = cycleKey.size
        return text.mapIndexed { index, char ->
            char.toInt().xor(cycleKey[index % keyLength].toInt()).toChar()
        }.joinToString("")
    }

    private fun parseAdminRefData(jsonString: String): String {
        try {
            val confString = JSONObject(jsonString).getJSONObject("YOPGuQvgAg").getString("conf")
            return confString
        } catch (e: Exception) {
            return ""
        }
    }

    fun postPutData(body: Any, callbackData: CallbackMy) {
        val jsonBodyString = JSONObject(body.toString()).toString()
        val requestBody = RequestBody.create(
            "application/json; charset=utf-8".toMediaTypeOrNull(),
            jsonBodyString
        )

        val request = Request.Builder()
            .url(EnvironmentConfig.upUrl)
            .post(requestBody)
            .addHeader("Content-Type", "application/json")
            .build()

        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                KeyContent.showLog("admin-Error: ${e.message}")
                callbackData.onFailure( e.message ?: "Unknown error")
            }

            override fun onResponse(call: Call, response: Response) {
                response.use {
                    if (!response.isSuccessful) {
                        callbackData.onFailure( "Unexpected code $response")
                    } else {
                        val responseData = response.body?.string() ?: ""
                        callbackData.onSuccess( responseData)
                    }
                }
            }
        })
    }

}
