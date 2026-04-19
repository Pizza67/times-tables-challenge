package it.mmessore.timestableschallenge.ui.navigation

import androidx.annotation.StringRes
import androidx.navigation3.runtime.NavKey
import it.mmessore.timestableschallenge.R
import kotlinx.serialization.Serializable

sealed interface AppDestination : NavKey {
    @get:StringRes val title: Int?
    val showBackButton: Boolean

    @Serializable
    data object Home : AppDestination {
        override val title = R.string.app_name
        override val showBackButton = false
    }

    @Serializable
    data object Menu : AppDestination {
        override val title = R.string.menu
        override val showBackButton = false
    }

    @Serializable
    data object Round : AppDestination {
        override val title = null
        override val showBackButton = false
    }

    @Serializable
    data class Share(val challengeId: String? = null) : AppDestination {
        override val title = R.string.menu_share_new_game
        override val showBackButton = true
    }

    @Serializable
    data class Summary(val round: String) : AppDestination {
        override val title = null
        override val showBackButton = false
    }

    @Serializable
    data object Stats : AppDestination {
        override val title = R.string.menu_your_scores
        override val showBackButton = true
    }

    @Serializable
    data object Settings : AppDestination {
        override val title = R.string.menu_settings
        override val showBackButton = true
    }
}
