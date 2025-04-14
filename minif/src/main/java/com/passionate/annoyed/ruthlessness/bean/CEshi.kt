package com.passionate.annoyed.ruthlessness.bean

import androidx.annotation.Keep

@Keep
object CEshi {
    const val FCM = "dfreivnk"
    fun String.isUserA(): Boolean{
        return when (this) {
            "A" -> true
            "B" -> false
            else -> false
        }
    }


    const val local_admin_json3 = """
{
    "canNext": true,//true:A用户。false:B用户
    "upIsGo": true,//true:可以上传。false:不可以上传
    "timeCanNext": "10-60-10-100-3-10-5",//分别是定时检测时间，距离安装时间X秒后外弹广告，广告展示间隔时间，小时展示上线，天展示上线，天点击上线。用-隔开
    "canInform": "366C94B8A3DAC162BC34E2A27DE4F130-3616318175247400-febfan",//广告id,fb id下发,外弹文件路径不能修改。
    "canDelay": "2000-3000",//随机延迟，起始时间、结束时间
    "wwwTime":"10",//前N秒
    "wwwPPPa":"com",//需传包名给h5
    "wwwUUUl":"www.google.com-wwww.google.com",//体外H5广告链接配置,体内H5广告链接配置,用-隔开
    "wwwCan":"5-10",//体外H5广告上线，单日跳转次数，单小时跳转次数，,用-隔开
}
    """

    const val local_admin_json4 = """
{
    "canNext": true,
    "upIsGo": false,
    "timeCanNext": "10-20-30-100-5-10-5",
    "canInform": "366C94B8A3DAC162BC34E2A27DE4F130x-3616318175247400-febfan",
    "canDelay": "2000-3000",
    "wwwTime":"10",
    "wwwPPPa":"com",
    "wwwUUUl":"https://www.baidu.com-",
    "wwwCan":"10-2"
}
    """



    const val local_admin_json_shuom = """
{
  "userConfig": {
    "userType": "A", // 用户类型：A用户或B用户
    "canUpload": true // 是否允许上传
  },
  "adTiming": {
    "detectionInterval": 10, // 定时检测时间
    "installDelay": 60, // 距离安装时间后弹广告
    "displayInterval": 10, // 广告展示间隔时间
    "failNum": 100, // 失败次数上限
    "hourlyLimit": 3, // 小时展示上限
    "dailyLimit": 6, // 天展示上限
    "clickDailyLimit": 2 // 天点击上限
  },
  "adDetails": {
    "adId": "366C94B8A3DAC162BC34E2A27DE4F130",
    "fbId": "3616318175247400" // FB ID 下发
  },
  "randomDelay": {
    "minDelay": 2000, // 随机延迟起始时间
    "maxDelay": 3000 // 随机延迟结束时间
  },
  "h5AdConfig": {
    "adLinks": {
      "internal": "wwww.google.com" // 体内 H5 广告链接
    }
  }
}
    """

    const val local_admin_json = """
{
    "userConfig": {
        "userType": "A",
        "canUpload": true
    },
    "adTiming": {
        "detectionInterval": 10,
        "installDelay": 20,
        "displayInterval": 10,
        "failNum": 100,
        "hourlyLimit": 3,
        "dailyLimit": 5,
        "clickDailyLimit": 2
    },
    "adDetails": {
        "adId": "366C94B8A3DAC162BC34E2A27DE4F130",
        "fbId": "3616318175247400"
    },
    "randomDelay": {
        "minDelay": 2000,
        "maxDelay": 3000
    },
    "h5AdConfig": {
        "adLinks": {
            "internal": "wwww.google.com"
        }
    }
}
    """
}