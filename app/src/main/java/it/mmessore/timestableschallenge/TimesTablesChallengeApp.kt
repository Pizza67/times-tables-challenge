package it.mmessore.timestableschallenge

import android.app.Application
import dagger.hilt.android.HiltAndroidApp
import it.mmessore.timestableschallenge.data.persistency.Constants

@HiltAndroidApp
class TimesTablesChallengeApp : Application() {

    override fun onCreate() {
        super.onCreate()

        Constants.init(applicationContext)
    }
}