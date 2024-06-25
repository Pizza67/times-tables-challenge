package it.mmessore.timestableschallenge.data

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes

data class BadgeInfo(
    @StringRes val nameStrId: Int,
    @StringRes val description: Int,
    @DrawableRes val image: Int,
    val timestamp: Long,
    val avgScore: Double,
    val numRounds: Int,
)
