package com.passionate.annoyed.ruthlessness.utils

import android.util.Log
import androidx.annotation.Keep
import com.google.gson.Gson
import com.passionate.annoyed.ruthlessness.bean.AllDataBean
import com.passionate.annoyed.ruthlessness.bean.CEshi
import com.passionate.annoyed.ruthlessness.jk.FebApp
import com.passionate.annoyed.ruthlessness.jk.FebApp.dataAppBean

@Keep
object KeyContent {
    fun showLog(msg: String) {
        if (!FebApp.isRelease) {
            return
        }
        Log.e("FebFive", msg)
    }

    fun getAdminData(): AllDataBean? {
//        dataAppBean.admindata =  CEshi.local_admin_json
        val adminData = dataAppBean.admindata
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
        dataAppBean.admindata = adminBean
//        dataAppBean.admindata =  CEshi.local_admin_json
    }
}