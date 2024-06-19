package it.mmessore.timestableschallenge.data.persistency

import android.content.Context
import android.content.SharedPreferences

class AppPreferences(context: Context) {

    companion object {
        private const val PREFS_NAME = "app_preferences"
        private const val NUM_QUESTIONS = "num_questions"
        private const val MIN_TABLE = "min_table"
        private const val MAX_TABLE = "max_table"
        private const val PLAY_SOUNDS = "play_sounds"
    }

    private val sharedPreferences: SharedPreferences = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)

    var numQuestions: Int
        get() = sharedPreferences.getInt(NUM_QUESTIONS, 20) // Default value
        set(value) = sharedPreferences.edit().putInt(NUM_QUESTIONS, value).apply()

    var minTable: Int
        get() = sharedPreferences.getInt(MIN_TABLE, 1) // Default value
        set(value) = sharedPreferences.edit().putInt(MIN_TABLE, value).apply()

    var maxTable: Int
        get() = sharedPreferences.getInt(MAX_TABLE, 10) // Default value
        set(value) = sharedPreferences.edit().putInt(MAX_TABLE, value).apply()

    var playSounds: Boolean
        get() = sharedPreferences.getBoolean(PLAY_SOUNDS, true) // Default value
        set(value) = sharedPreferences.edit().putBoolean(PLAY_SOUNDS, value).apply()
}