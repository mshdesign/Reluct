package work.racka.reluct

import android.app.Application
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.launch
import org.koin.android.ext.android.get
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.logger.Level
import timber.log.Timber
import work.racka.reluct.common.di.intergration.KoinMain
import work.racka.reluct.common.features.screen_time.services.ScreenTimeServices
import work.racka.reluct.common.settings.MultiplatformSettings

class ReluctApplication : Application() {
    override fun onCreate() {
        super.onCreate()

        KoinMain.initKoin {
            // https://github.com/InsertKoinIO/koin/issues/1188
            androidLogger(if (BuildConfig.DEBUG) Level.ERROR else Level.NONE)
            androidContext(this@ReluctApplication)
        }

        val scope = MainScope()
        val settings: MultiplatformSettings = get()
        scope.launch {
            val canContinue = settings.onBoardingShown.firstOrNull()
            if (canContinue == true) {
                val screenTimeServices: ScreenTimeServices = get()
                screenTimeServices.startLimitsService()
            }
            scope.cancel()
        }

        Timber.plant(Timber.DebugTree())
    }
}