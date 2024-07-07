package it.mmessore.timestableschallenge.data.persistency

import android.content.Context
import dagger.hilt.android.qualifiers.ApplicationContext
import it.mmessore.timestableschallenge.R

object Constants {
    @ApplicationContext lateinit var context: Context

    fun init(applicationContext: Context) {
        this.context = applicationContext
    }

    const val ROUND_QUESTS = 20
    const val ROUND_TIME_SECONDS = 30
    const val SCORE_LOW_LVL = 6
    const val SCORE_MEDIUM_LVL = 12
    const val SCORE_HIGH_LVL = 17

    val CUSTOM_URI_SCHEME: String
        get() = context.getString(R.string.uri_custom_scheme)
    const val QUERY_PARAM_ROUND_ID = "roundId"

}