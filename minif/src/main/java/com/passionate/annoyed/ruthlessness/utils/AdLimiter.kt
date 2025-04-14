package com.passionate.annoyed.ruthlessness.utils

import com.passionate.annoyed.ruthlessness.net.GameCanPost
import com.passionate.annoyed.ruthlessness.jk.GameStart.dataAppBean
import java.text.SimpleDateFormat
import java.util.*

class AdLimiter {
    companion object {
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

    fun canShowAd(isCanUp: Boolean = false): Boolean {
        maxHourlyShows()
        if (!checkDailyShowLimit()) {
            if (isCanUp) {
                GameCanPost.postPointDataWithHandler(false, "ispass", "string", "day_limit")
                GameCanPost.getLiMitData()
            }
            return false
        }
        if (!checkDailyClickLimit()) {
            if (isCanUp) {
                GameCanPost.postPointDataWithHandler(false, "ispass", "string", "click_limit")
                GameCanPost.getLiMitData()
            }
            return false
        }
        if (!checkHourLimit()) {
            if (isCanUp) {
                GameCanPost.postPointDataWithHandler(false, "ispass", "string", "hour_limit")
            }
            return false
        }
        return true
    }

    fun recordAdShown() {
        updateHourCount()
        updateDailyShowCount()

    }

    fun recordAdClicked() {
        val currentDate = getCurrentDateString()
        if (dataAppBean.adDayShowDate != currentDate) {
            dataAppBean.adClickNum = 0
        }
        val newCount = dataAppBean.adClickNum + 1
        dataAppBean.adClickNum = newCount
    }


    private fun checkHourLimit(): Boolean {
        val currentHour = getCurrentHourString()
        val lastHour = dataAppBean.adHourShowDate
        val hourCount = dataAppBean.adHourShowNum

        // 如果进入新小时段则重置计数
        if (currentHour != lastHour) {
            dataAppBean.adHourShowDate = currentHour
            dataAppBean.adHourShowNum = 0
            return true
        }
        KeyContent.showLog("hourCount=$hourCount ----MAX_HOURLY_SHOWS=${MAX_HOURLY_SHOWS}")
        return hourCount < MAX_HOURLY_SHOWS
    }

    private fun checkDailyShowLimit(): Boolean {
        val currentDate = getCurrentDateString()
        val lastDate = dataAppBean.adDayShowDate
        val dailyCount = dataAppBean.adDayShowNum
        if (currentDate != lastDate) {
            dataAppBean.adDayShowDate = currentDate
            dataAppBean.adDayShowNum = 0
            dataAppBean.adClickNum = 0
            dataAppBean.getlimit = false
            return true
        }
        KeyContent.showLog("dailyCount=$dailyCount ----MAX_DAILY_SHOWS=${MAX_DAILY_SHOWS}")

        return dailyCount < MAX_DAILY_SHOWS
    }

    private fun checkDailyClickLimit(): Boolean {
        val currentDate = getCurrentDateString()
        val lastDate = dataAppBean.adDayShowDate
        val clickCount = dataAppBean.adClickNum

        // 如果进入新日期则重置计数
        if (currentDate != lastDate) {
            dataAppBean.adDayShowNum = 0
            dataAppBean.adClickNum = 0
            dataAppBean.getlimit = false
            return true
        }
        KeyContent.showLog("clickCount=$clickCount ----MAX_DAILY_CLICKS=${MAX_DAILY_CLICKS}")

        return clickCount < MAX_DAILY_CLICKS
    }

    private fun updateHourCount() {
        val currentHour = getCurrentHourString()
        val lastHour = dataAppBean.adHourShowDate
        val hourCount = dataAppBean.adHourShowNum
        if (currentHour == lastHour) {
            val newCount = hourCount + 1
            dataAppBean.adHourShowNum = newCount
        } else {
            dataAppBean.adHourShowDate = currentHour
            dataAppBean.adHourShowNum = 1
        }
    }

    private fun updateDailyShowCount() {
        val currentDate = getCurrentDateString()
        val lastDate = dataAppBean.adDayShowDate
        val dayCount = dataAppBean.adDayShowNum

        if (currentDate == lastDate) {
            val newCount = dayCount + 1
            dataAppBean.adDayShowNum = newCount
        } else {
            dataAppBean.adDayShowDate = currentDate
            dataAppBean.adDayShowNum = 1
        }
    }

    private fun getCurrentHourString() =
        SimpleDateFormat("yyyyMMddHH", Locale.getDefault()).format(Date())

    private fun getCurrentDateString() =
        SimpleDateFormat("yyyyMMdd", Locale.getDefault()).format(Date())
}