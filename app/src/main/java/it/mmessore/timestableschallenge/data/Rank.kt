package it.mmessore.timestableschallenge.data

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import it.mmessore.timestableschallenge.R

data class Rank(
    @StringRes val name: Int,
    val minAvgScore: Float,
    val minRounds: Int,
    @StringRes val description: Int,
    @DrawableRes val image: Int
)

object Ranks {
    val list = listOf(
        Rank(
            name = R.string.name_rank_low,
            minAvgScore = 0f,
            minRounds = 0,
            description = R.string.desc_rank_low,
            image = R.drawable.img_rank_low
        ),
        Rank(
            name = R.string.name_rank_medium,
            minAvgScore = 8f,
            minRounds = 20,
            description = R.string.desc_rank_medium,
            image = R.drawable.img_rank_medium
        ),
        Rank(
            name = R.string.name_rank_high,
            minAvgScore = 12f,
            minRounds = 40,
            description = R.string.desc_rank_high,
            image = R.drawable.img_rank_high
        ),
        Rank(
            name = R.string.name_rank_top,
            minAvgScore = 15f,
            minRounds = 80,
            description = R.string.desc_rank_top,
            image = R.drawable.img_rank_top
        ),
        Rank(
            name = R.string.name_rank_max,
            minAvgScore = 18f,
            minRounds = 100,
            description = R.string.desc_rank_max,
            image = R.drawable.img_rank_max
        )
    )

    fun getRankbyStats(avgScore: Float, rounds: Int): Rank {
        return list.first { it.minAvgScore <= avgScore && it.minRounds <= rounds }
    }
}