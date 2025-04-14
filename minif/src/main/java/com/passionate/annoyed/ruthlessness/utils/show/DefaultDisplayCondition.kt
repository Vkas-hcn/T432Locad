package com.passionate.annoyed.ruthlessness.utils.show

import com.passionate.annoyed.ruthlessness.dataces.EnhancedShowService
import com.passionate.annoyed.ruthlessness.utils.AdUtils

class DefaultDisplayCondition(
    private val installDelay: Int,
    private val displayInterval: Int
) : DisplayConditionStrategy {

    override fun checkConditions(): Boolean {
        val installTime = EnhancedShowService.getInstallTimeInSeconds()
        val interval = (System.currentTimeMillis() - AdUtils.adShowTime) / 1000

        return when {
            installTime < installDelay -> false
            interval < displayInterval -> false
            else -> true
        }
    }
}