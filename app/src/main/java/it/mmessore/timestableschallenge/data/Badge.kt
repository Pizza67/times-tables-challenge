package it.mmessore.timestableschallenge.data

import android.util.Log
import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import it.mmessore.timestableschallenge.R

data class Badge(
    @StringRes val nameStrId: Int,
    val minAvgScore: Float,
    val minRounds: Int,
    @StringRes val description: Int,
    @DrawableRes val image: Int
)

object Badges {
    val list = listOf(
        Badge(
            nameStrId = R.string.name_rank_low,
            minAvgScore = 0f,
            minRounds = 0,
            description = R.string.desc_rank_low,
            image = R.drawable.img_rank_low
        ),
        Badge(
            nameStrId = R.string.name_rank_medium,
            minAvgScore = 8f,
            minRounds = 20,
            description = R.string.desc_rank_medium,
            image = R.drawable.img_rank_medium
        ),
        Badge(
            nameStrId = R.string.name_rank_high,
            minAvgScore = 12f,
            minRounds = 40,
            description = R.string.desc_rank_high,
            image = R.drawable.img_rank_high
        ),
        Badge(
            nameStrId = R.string.name_rank_top,
            minAvgScore = 15f,
            minRounds = 80,
            description = R.string.desc_rank_top,
            image = R.drawable.img_rank_top
        ),
        Badge(
            nameStrId = R.string.name_rank_max,
            minAvgScore = 18f,
            minRounds = 100,
            description = R.string.desc_rank_max,
            image = R.drawable.img_rank_max
        )
    )

    fun getBadgebyStats(avgScore: Double, rounds: Int): Badge {
        Log.d("StatsViewModel", "avgScore: $avgScore, rounds: $rounds")
        return list.last { avgScore >= it.minAvgScore && rounds >= it.minRounds }
    }
}