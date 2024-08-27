package it.mmessore.timestableschallenge.data.persistency

import androidx.annotation.StringRes
import it.mmessore.timestableschallenge.R

interface AppPreferences {

    enum class AppThemeStyle(@StringRes val label: Int) {
        LIGHT(R.string.theme_light),
        DARK(R.string.theme_dark),
        SYSTEM(R.string.theme_system)
    }

    var numQuestions: Int
    var minTable: Int
    var maxTable: Int
    var playSounds: Boolean
    var extendedMode: Boolean
    var autoConfirm: Boolean
    var overwriteBestScores: Boolean
    var useTimeLeft: Boolean
    var themeStyle: AppThemeStyle
}