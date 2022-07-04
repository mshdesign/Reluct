package work.racka.reluct.common.model.domain.limits

import work.racka.reluct.common.model.domain.app_info.AppInfo

data class AppLimits(
    val appInfo: AppInfo,
    val timeLimit: Long,
    val isADistractingAp: Boolean,
    val isPaused: Boolean,
    val overridden: Boolean
)