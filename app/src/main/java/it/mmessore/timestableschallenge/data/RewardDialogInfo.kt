package it.mmessore.timestableschallenge.data

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes

data class RewardDialogInfo(
    @StringRes val title: Int,
    @StringRes val message: Int,
    @DrawableRes val image: Int,
    @StringRes val contentDescription: Int
)

