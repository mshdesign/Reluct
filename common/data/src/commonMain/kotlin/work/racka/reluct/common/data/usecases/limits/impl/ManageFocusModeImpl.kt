package work.racka.reluct.common.data.usecases.limits.impl

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.withContext
import work.racka.reluct.common.data.usecases.limits.ManageFocusMode
import work.racka.reluct.common.settings.MultiplatformSettings
import work.racka.reluct.common.system_service.haptics.HapticFeedback

internal class ManageFocusModeImpl(
    private val settings: MultiplatformSettings,
    private val haptics: HapticFeedback,
    private val backgroundDispatcher: CoroutineDispatcher
) : ManageFocusMode {
    override val isFocusModeOn: Flow<Boolean>
        get() = settings.focusMode.flowOn(backgroundDispatcher)

    override val isDoNotDisturbOn: Flow<Boolean>
        get() = settings.doNoDisturb.flowOn(backgroundDispatcher)

    override suspend fun toggleFocusMode(isFocusMode: Boolean) {
        withContext(backgroundDispatcher) {
            settings.saveFocusMode(isFocusMode)
            haptics.tick()
        }
    }

    override suspend fun toggleDoNoDisturb(isDnd: Boolean) {
        withContext(backgroundDispatcher) {
            settings.saveDoNotDisturb(isDnd)
            haptics.tick()
        }
    }
}