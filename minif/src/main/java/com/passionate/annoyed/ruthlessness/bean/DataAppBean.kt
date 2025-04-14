package com.passionate.annoyed.ruthlessness.bean


import androidx.annotation.Keep
import com.passionate.annoyed.ruthlessness.jk.GameStart.gameApp

@Keep
class DataAppBean {
    var firstPoint: Boolean by DataStoreDelegate(gameApp, "firstPoint", false)
    var adOrgPoint: Boolean by DataStoreDelegate(gameApp, "adOrgPoint", false)
    var getlimit: Boolean by DataStoreDelegate(gameApp, "getlimit", false)
    var fcmState: Boolean by DataStoreDelegate(gameApp, "fcmState", false)
    var admindata: String by DataStoreDelegate(gameApp, "admindata", "")
    var refdata: String by DataStoreDelegate(gameApp, "refdata", "")
    var appiddata: String by DataStoreDelegate(gameApp, "appiddata", "")
    var IS_INT_JSON: String by DataStoreDelegate(gameApp, "IS_INT_JSON", "")
    var isAdFailCount: Int by DataStoreDelegate(gameApp, "isAdFailCount", 0)
    var adFailPost: Boolean by DataStoreDelegate(gameApp, "adFailPost", false)


    var adHourShowNum: Int by DataStoreDelegate(gameApp, "adHourShowNum", 0)
    var adHourShowDate: String by DataStoreDelegate(gameApp, "adHourShowDate", "")
    var adDayShowNum: Int by DataStoreDelegate(gameApp, "adDayShowNum", 0)
    var adDayShowDate: String by DataStoreDelegate(gameApp, "adDayShowDate", "")

    var adClickNum: Int by DataStoreDelegate(gameApp, "adClickNum", 0)
}