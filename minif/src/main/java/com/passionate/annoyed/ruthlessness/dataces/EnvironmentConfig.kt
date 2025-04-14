package com.passionate.annoyed.ruthlessness.dataces


import androidx.annotation.Keep
import com.passionate.annoyed.ruthlessness.jk.FebApp

@Keep
object EnvironmentConfig {

    // 获取 Tttid
    val tttid: String
        get() = if (FebApp.isRelease) {
            "114FE8DB631B3389BDDDD15D81E45E39"
        } else {
            "257BF41F4F0937B8AEA7F31E9B200294"
        }

    // 获取 Openid
    val openid: String
        get() = if (FebApp.isRelease) {
            "0A600053F2B2775FF79B1CD046A0098C"
        } else {
            "EA76154CEF52340DCF932ABA93A87E04"
        }

    // 获取上传 URL
    val upUrl: String
        get() = if (FebApp.isRelease) {
            "https://test-abraham.defaultcompanysushitile.com/slumber/bulldog"
        } else {
            "https://abraham.defaultcompanysushitile.com/pay/noisy/ogle"
        }

    // 获取管理后台 URL
    val adminUrl: String
        get() = if (FebApp.isRelease) {
            "https://tiles.defaultcompanysushitile.com/apitest/ccss/"
        } else {
            "https://tiles.defaultcompanysushitile.com/api/ccss/"
        }

    // 获取 AppsFlyer ID
    val appsflyId: String
        get() = if (FebApp.isRelease) {
            "5MiZBZBjzzChyhaowfLpyR"
        } else {
            "X6QFbEQpPG2qjuCSNMxNA3"
        }

    val packnameStart: String
        get() = if (FebApp.isRelease) {
            "com.de.lo.stt432locad"
        } else {
            ""
        }
}
