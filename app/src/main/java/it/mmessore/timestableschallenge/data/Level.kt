package it.mmessore.timestableschallenge.data

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import it.mmessore.timestableschallenge.R
import it.mmessore.timestableschallenge.data.persistency.Constants
import it.mmessore.timestableschallenge.data.persistency.ConstantsImpl

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
            ConstantsImpl.SCORE_LOW_LVL,
            R.string.desc_score_low,
            R.drawable.img_score_low
        ),
        Level(
            R.string.name_score_medium,
            ConstantsImpl.SCORE_LOW_LVL + 1,
            ConstantsImpl.SCORE_MEDIUM_LVL,
            R.string.desc_score_medium,
            R.drawable.img_score_medium
        ),
        Level(
            R.string.name_score_high,
            ConstantsImpl.SCORE_MEDIUM_LVL + 1,
            ConstantsImpl.SCORE_HIGH_LVL,
            R.string.desc_score_high,
            R.drawable.img_score_high
        ),
        Level(
            R.string.name_score_top,
            ConstantsImpl.SCORE_HIGH_LVL + 1,
            ConstantsImpl.ROUND_QUESTS - 1,
            R.string.desc_score_top,
            R.drawable.img_score_top
        ),
        Level(
            R.string.name_score_max,
            ConstantsImpl.ROUND_QUESTS,
            ConstantsImpl.ROUND_QUESTS,
            R.string.desc_score_max,
            R.drawable.img_score_max
        )
    )

    fun getLevelByScore(score: Int): Level {
        return list.first { it.minScore <= score && score <= it.maxScore }
    }
}
