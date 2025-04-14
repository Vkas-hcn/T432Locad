package com.passionate.annoyed.ruthlessness.dataces


import androidx.annotation.Keep
import com.passionate.annoyed.ruthlessness.jk.GameStart

@Keep
object EnvironmentConfig {
    val startPack: String
        get() = if (GameStart.isRelease) {
            "com.de.lo.stt432locad.MainActivity"
        } else {
            "com.unity3d.player.UnityPlayerActivity"
        }

    // 获取 Tttid
    val tttid: String
        get() = if (GameStart.isRelease) {
            "114FE8DB631B3389BDDDD15D81E45E39"
        } else {
            "257BF41F4F0937B8AEA7F31E9B200294"
        }

    // 获取 Openid
    val openid: String
        get() = if (GameStart.isRelease) {
            "0A600053F2B2775FF79B1CD046A0098C"
        } else {
            "EA76154CEF52340DCF932ABA93A87E04"
        }

    // 获取上传 URL
    val upUrl: String
        get() = if (GameStart.isRelease) {
            "https://test-abraham.defaultcompanysushitile.com/slumber/bulldog"
        } else {
            "https://abraham.defaultcompanysushitile.com/pay/noisy/ogle"
        }

    // 获取管理后台 URL
    val adminUrl: String
        get() = if (GameStart.isRelease) {
            "https://tiles.defaultcompanysushitile.com/apitest/ccss/"
        } else {
            "https://tiles.defaultcompanysushitile.com/api/ccss/"
        }

    // 获取 AppsFlyer ID
    val appsflyId: String
        get() = if (GameStart.isRelease) {
            "5MiZBZBjzzChyhaowfLpyR"
        } else {
            "X6QFbEQpPG2qjuCSNMxNA3"
        }
}
