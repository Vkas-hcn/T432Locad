package com.passionate.annoyed.ruthlessness.utils.show

// 时间策略实现
class TimeBasedLoadingStrategy(
    private val cacheDuration: Long,
    private val timeout: Long
) : AdLoadingStrategy {

    var isLoading = false
    private var lastLoadTime = 0L

    override fun shouldLoadAd(currentTime: Long): Boolean {
        return when {
            (currentTime - lastLoadTime) < cacheDuration -> false
            isLoading -> false
            else -> {
                isLoading = true
                true
            }
        }
    }

    override fun handleLoadSuccess() {
        lastLoadTime = System.currentTimeMillis()
        isLoading = false
    }

    override fun handleLoadFailure(errorMsg: String) {
        isLoading = false
    }
}