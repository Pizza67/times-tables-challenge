package it.mmessore.timestableschallenge.data

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import it.mmessore.timestableschallenge.R
import it.mmessore.timestableschallenge.data.persistency.Constants

data class Level(
    @StringRes val name: Int,
    val minScore: Int,
    val maxScore: Int,
    @StringRes val description: Int,
    @DrawableRes val image: Int
)

object Levels {
    val list = listOf (
        Level(
            R.string.name_score_low,
            0,
            Constants.SCORE_LOW_LVL,
            R.string.desc_score_low,
            R.drawable.img_score_low
        ),
        Level(
            R.string.name_score_medium,
            Constants.SCORE_LOW_LVL + 1,
            Constants.SCORE_MEDIUM_LVL,
            R.string.desc_score_medium,
            R.drawable.img_score_medium
        ),
        Level(
            R.string.name_score_high,
            Constants.SCORE_MEDIUM_LVL + 1,
            Constants.SCORE_HIGH_LVL,
            R.string.desc_score_high,
            R.drawable.img_score_high
        ),
        Level(
            R.string.name_score_top,
            Constants.SCORE_HIGH_LVL + 1,
            Constants.ROUND_QUESTS - 1,
            R.string.desc_score_top,
            R.drawable.img_score_top
        ),
        Level(
            R.string.name_score_max,
            Constants.ROUND_QUESTS,
            Constants.ROUND_QUESTS,
            R.string.desc_score_max,
            R.drawable.img_score_max
        )
    )

    fun getLevelByScore(score: Int): Level {
        return list.first { it.minScore <= score && score <= it.maxScore }
    }
}
