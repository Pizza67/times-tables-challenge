package it.mmessore.timestableschallenge.data.persistency

import android.content.Context
import dagger.hilt.android.qualifiers.ApplicationContext
import it.mmessore.timestableschallenge.R

object ConstantsImpl: Constants {
    @ApplicationContext lateinit var context: Context

    fun init(applicationContext: Context) {
        this.context = applicationContext
    }

    override val ROUND_QUESTS = 20
    override val ROUND_TIME_SECONDS = 30
    override val SCORE_LOW_LVL = 6
    override val SCORE_MEDIUM_LVL = 12
    override val SCORE_HIGH_LVL = 17

    override val CUSTOM_URI_SCHEME: String
        get() = context.getString(R.string.uri_custom_scheme)
    override val QUERY_PARAM_ROUND_ID = "roundId"
}