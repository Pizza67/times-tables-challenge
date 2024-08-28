package it.mmessore.timestableschallenge.data.persistency

class FakeConstants(
    override val ROUND_QUESTS: Int = 10,
    override val ROUND_TIME_SECONDS: Int = 10,
    override val SCORE_LOW_LVL: Int = 6,
    override val SCORE_MEDIUM_LVL: Int = 12,
    override val SCORE_HIGH_LVL: Int = 17,
    override val CUSTOM_URI_SCHEME: String = "timestablesrace",
    override val QUERY_PARAM_ROUND_ID: String = "roundId"
) : Constants