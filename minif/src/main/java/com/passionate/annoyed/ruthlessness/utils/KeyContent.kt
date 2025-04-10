package com.passionate.annoyed.ruthlessness.utils

import android.util.Log
import androidx.annotation.Keep
import com.google.gson.Gson
import com.passionate.annoyed.ruthlessness.must.AllDataBean
import com.passionate.annoyed.ruthlessness.must.FacyData
import com.passionate.annoyed.ruthlessness.start.FebApp
import com.passionate.annoyed.ruthlessness.start.FebApp.febApp

@Keep
object KeyContent {
    const val KEY_IS_ADMIN_DATA = "KEY_IS_ADMIN_DATA"
    const val KEY_IS_ANDROID = "KEY_IS_ANDROID"
    const val KEY_IS_REF = "KEY_IS_REF"
    const val KEY_IS_INT_JSON = "KEY_IS_INT_JSON"
    const val KEY_IS_FCM = "KEY_IS_FCM"
    const val KEY_IS_AD_FAIL_COUNT = "KEY_IS_AD_FAIL_COUNT"
    const val FIRST_EXTERNAL_POINT = "FIRST_EXTERNAL_POINT"
    const val KEY_IS_AD_ORG = "KEY_IS_AD_ORG"
    const val KEY_IS_LAST_REPORT_TIME = "KEY_IS_LAST_REPORT_TIME"
    const val KEY_IS_GET_LIMIT = "KEY_IS_GET_LIMIT"
    const val KEY_IS_AD_WT_TIME = "KEY_IS_AD_WT_TIME"
    fun showLog(msg: String) {
        if (FebApp.isRelease) {
            return
        }
        Log.e("FebFive", msg)
    }

    fun getAdminData(): AllDataBean? {
        val adminData = SPUtils.getInstance(febApp).get(KEY_IS_ADMIN_DATA, "")
        val adminBean = runCatching {
            Gson().fromJson(adminData, AllDataBean::class.java)
        }.getOrNull()
        return if (adminBean != null && isValidAdminBean(adminBean)) {
            adminBean
        } else {
            null
        }
    }

    private fun isValidAdminBean(bean: AllDataBean): Boolean {
        return bean.userConfig != null && bean.userConfig.userType.isNotEmpty()
    }



    fun putAdminData(adminBean: String) {
//        SPUtils.getInstance(febApp).put(KEY_IS_ADMIN_DATA, adminBean)
        SPUtils.getInstance(febApp).put(KEY_IS_ADMIN_DATA, FacyData.local_admin_json)
    }
}