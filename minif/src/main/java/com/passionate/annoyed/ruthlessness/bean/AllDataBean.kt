package com.passionate.annoyed.ruthlessness.bean

import androidx.annotation.Keep

@Keep
data class AllDataBean(
    val adDetails: AdDetails,
    val adTiming: AdTiming,
    val h5AdConfig: H5AdConfig,
    val randomDelay: RandomDelay,
    val userConfig: UserConfig
)

@Keep
data class AdDetails(
    val adId: String,
    val fbId: String
)

@Keep
data class AdTiming(
    val clickDailyLimit: Int,
    val dailyLimit: Int,
    val detectionInterval: Int,
    val displayInterval: Int,
    val hourlyLimit: Int,
    val installDelay: Int,
    val failNum:Int
)

@Keep
data class H5AdConfig(
    val adLinks: AdLinks
)

@Keep
data class RandomDelay(
    val maxDelay: Int,
    val minDelay: Int
)
@Keep
data class UserConfig(
    val canUpload: Boolean,
    val userType: String
)

@Keep
data class AdLinks(
    val `internal`: String
)


