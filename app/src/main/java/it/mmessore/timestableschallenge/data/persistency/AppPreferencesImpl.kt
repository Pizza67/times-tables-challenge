package it.mmessore.timestableschallenge.data.persistency

import android.content.Context
import android.content.SharedPreferences
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject

class AppPreferencesImpl @Inject constructor(@ApplicationContext val context: Context): AppPreferences {

    companion object {
        private const val PREFS_NAME = "app_preferences"
        private const val NUM_QUESTIONS = "num_questions"
        private const val MIN_TABLE = "min_table"
        private const val MAX_TABLE = "max_table"
        private const val PLAY_SOUNDS = "play_sounds"
        private const val EXTENDED_MODE = "extended_mode"
        private const val AUTO_CONFIRM = "auto_confirm"
        private const val OVERWRITE_BEST_SCORES = "overwrite_best_scores"
        private const val USE_TIME_LEFT = "use_time_left"
        private const val THEME_STYLE = "theme_style"
    }

    private val sharedPreferences: SharedPreferences = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)

    override var numQuestions: Int
        get() = sharedPreferences.getInt(NUM_QUESTIONS, 20)
        set(value) = sharedPreferences.edit().putInt(NUM_QUESTIONS, value).apply()

    override var minTable: Int
        get() = sharedPreferences.getInt(MIN_TABLE, 1)
        set(value) = sharedPreferences.edit().putInt(MIN_TABLE, value).apply()

    override var maxTable: Int
        get() = sharedPreferences.getInt(MAX_TABLE, 10)
        set(value) = sharedPreferences.edit().putInt(MAX_TABLE, value).apply()

    override var playSounds: Boolean
        get() = sharedPreferences.getBoolean(PLAY_SOUNDS, true)
        set(value) = sharedPreferences.edit().putBoolean(PLAY_SOUNDS, value).apply()

    override var extendedMode: Boolean
        get() = sharedPreferences.getBoolean(EXTENDED_MODE, false)
        set(value) = sharedPreferences.edit().putBoolean(EXTENDED_MODE, value).apply()

    override var autoConfirm: Boolean
        get() = sharedPreferences.getBoolean(AUTO_CONFIRM, false)
        set(value) = sharedPreferences.edit().putBoolean(AUTO_CONFIRM, value).apply()

    override var overwriteBestScores: Boolean
        get() = sharedPreferences.getBoolean(OVERWRITE_BEST_SCORES, false)
        set(value) = sharedPreferences.edit().putBoolean(OVERWRITE_BEST_SCORES, value).apply()

    override var useTimeLeft: Boolean
        get() = sharedPreferences.getBoolean(USE_TIME_LEFT, false)
        set(value) = sharedPreferences.edit().putBoolean(USE_TIME_LEFT, value).apply()

    override var themeStyle: AppPreferences.AppThemeStyle
        get() = sharedPreferences.getString(THEME_STYLE, AppPreferences.AppThemeStyle.SYSTEM.name)?.let {
            AppPreferences.AppThemeStyle.valueOf(it)
        } ?: AppPreferences.AppThemeStyle.SYSTEM
        set(value) = sharedPreferences.edit().putString(THEME_STYLE, value.name).apply()

}