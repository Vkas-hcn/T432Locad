package com.passionate.annoyed.ruthlessness.utils

import android.content.Context
import android.content.SharedPreferences
import com.passionate.annoyed.ruthlessness.net.CanPost
import com.passionate.annoyed.ruthlessness.start.FebApp
import java.text.SimpleDateFormat
import java.util.*

class AdLimiter {
    companion object {
        // SharedPreferences 键名
        private const val PREF_LAST_HOUR = "last_hour"
        private const val PREF_HOUR_COUNT = "hour_count"
        private const val PREF_LAST_SHOW_DATE = "last_show_date"
        private const val PREF_DAILY_SHOW_COUNT = "daily_show_count"
        private const val PREF_DAILY_CLICK_COUNT = "daily_click_count"

        // 限制规则
        private var MAX_HOURLY_SHOWS = 0
        private var MAX_DAILY_SHOWS = 0
        private var MAX_DAILY_CLICKS = 0
    }

    private fun maxHourlyShows() {
        val jsonBean = KeyContent.getAdminData() ?: return
        MAX_HOURLY_SHOWS = jsonBean.adTiming.hourlyLimit
        MAX_DAILY_SHOWS = jsonBean.adTiming.dailyLimit
        MAX_DAILY_CLICKS = jsonBean.adTiming.clickDailyLimit
    }

    // 检查是否可以展示广告
    fun canShowAd(isCanUp: Boolean = false): Boolean {
        maxHourlyShows()
        val prefs = getSharedPrefs()
        // 检查每日展示限制
        if (!checkDailyShowLimit(prefs)) {
            if (isCanUp) {
                CanPost.postPointDataWithHandler(false, "ispass", "string", "timeCanNext6")
                CanPost.getLiMitData()
            }
            return false
        }
        // 检查每日点击限制
        if (!checkDailyClickLimit(prefs)) {
            if (isCanUp) {
                CanPost.postPointDataWithHandler(false, "ispass", "string", "timeCanNext7")
                CanPost.getLiMitData()
            }
            return false
        }
        // 检查小时限制
        if (!checkHourLimit(prefs)) {
            if (isCanUp) {
                CanPost.postPointDataWithHandler(false, "ispass", "string", "timeCanNext5")
            }
            return false
        }
        return true
    }

    // 记录广告展示
    fun recordAdShown() {
        val prefs = getSharedPrefs()
        val editor = prefs.edit()

        // 更新小时计数
        updateHourCount(prefs, editor)

        // 更新每日展示计数
        updateDailyShowCount(prefs, editor)

        editor.apply()
    }

    // 记录广告点击
    fun recordAdClicked() {
        val prefs = getSharedPrefs()
        val currentDate = getCurrentDateString()
        val editor = prefs.edit()

        // 重置过期的点击计数
        if (prefs.getString(PREF_LAST_SHOW_DATE, "") != currentDate) {
            editor.putInt(PREF_DAILY_CLICK_COUNT, 0)
        }

        // 增加点击计数
        val newCount = prefs.getInt(PREF_DAILY_CLICK_COUNT, 0) + 1
        editor.putInt(PREF_DAILY_CLICK_COUNT, newCount)
        editor.apply()
    }

    private fun getSharedPrefs() =
        FebApp.febApp.getSharedPreferences("ad_limits", Context.MODE_PRIVATE)

    private fun checkHourLimit(prefs: SharedPreferences): Boolean {
        val currentHour = getCurrentHourString()
        val lastHour = prefs.getString(PREF_LAST_HOUR, "")
        val hourCount = prefs.getInt(PREF_HOUR_COUNT, 0)

        // 如果进入新小时段则重置计数
        if (currentHour != lastHour) {
            prefs.edit()
                .putString(PREF_LAST_HOUR, currentHour)
                .putInt(PREF_HOUR_COUNT, 0)
                .apply()
            return true
        }
        KeyContent.showLog("hourCount=$hourCount ----MAX_HOURLY_SHOWS=${MAX_HOURLY_SHOWS}")
        return hourCount < MAX_HOURLY_SHOWS
    }

    private fun checkDailyShowLimit(prefs: SharedPreferences): Boolean {
        val currentDate = getCurrentDateString()
        val lastDate = prefs.getString(PREF_LAST_SHOW_DATE, "")
        val dailyCount = prefs.getInt(PREF_DAILY_SHOW_COUNT, 0)

        // 如果进入新日期则重置计数
        if (currentDate != lastDate) {
            prefs.edit()
                .putString(PREF_LAST_SHOW_DATE, currentDate)
                .putInt(PREF_DAILY_SHOW_COUNT, 0)
                .apply()
            return true
        }
        KeyContent.showLog("dailyCount=$dailyCount ----MAX_DAILY_SHOWS=${MAX_DAILY_SHOWS}")

        return dailyCount < MAX_DAILY_SHOWS
    }

    private fun checkDailyClickLimit(prefs: SharedPreferences): Boolean {
        val currentDate = getCurrentDateString()
        val lastDate = prefs.getString(PREF_LAST_SHOW_DATE, "")
        val clickCount = prefs.getInt(PREF_DAILY_CLICK_COUNT, 0)

        // 如果进入新日期则重置计数
        if (currentDate != lastDate) {
            prefs.edit()
                .putInt(PREF_DAILY_CLICK_COUNT, 0)
                .apply()
            return true
        }
        KeyContent.showLog("clickCount=$clickCount ----MAX_DAILY_CLICKS=${MAX_DAILY_CLICKS}")

        return clickCount < MAX_DAILY_CLICKS
    }

    private fun updateHourCount(prefs: SharedPreferences, editor: SharedPreferences.Editor) {
        val currentHour = getCurrentHourString()
        val lastHour = prefs.getString(PREF_LAST_HOUR, "")

        if (currentHour == lastHour) {
            val newCount = prefs.getInt(PREF_HOUR_COUNT, 0) + 1
            editor.putInt(PREF_HOUR_COUNT, newCount)
        } else {
            editor.putString(PREF_LAST_HOUR, currentHour)
                .putInt(PREF_HOUR_COUNT, 1)
        }
    }

    private fun updateDailyShowCount(prefs: SharedPreferences, editor: SharedPreferences.Editor) {
        val currentDate = getCurrentDateString()
        val lastDate = prefs.getString(PREF_LAST_SHOW_DATE, "")

        if (currentDate == lastDate) {
            val newCount = prefs.getInt(PREF_DAILY_SHOW_COUNT, 0) + 1
            editor.putInt(PREF_DAILY_SHOW_COUNT, newCount)
        } else {
            editor.putString(PREF_LAST_SHOW_DATE, currentDate)
                .putInt(PREF_DAILY_SHOW_COUNT, 1)
        }
    }

    private fun getCurrentHourString() =
        SimpleDateFormat("yyyyMMddHH", Locale.getDefault()).format(Date())

    private fun getCurrentDateString() =
        SimpleDateFormat("yyyyMMdd", Locale.getDefault()).format(Date())
}