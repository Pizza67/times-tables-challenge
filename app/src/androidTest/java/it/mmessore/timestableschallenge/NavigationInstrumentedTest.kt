package it.mmessore.timestableschallenge

import android.content.Context
import androidx.compose.ui.test.ExperimentalTestApi
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertIsNotDisplayed
import androidx.compose.ui.test.hasText
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.test.espresso.Espresso.pressBack
import androidx.test.ext.junit.runners.AndroidJUnit4
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import it.mmessore.timestableschallenge.data.persistency.Constants
import it.mmessore.timestableschallenge.ui.screens.AppRootScreen
import it.mmessore.timestableschallenge.ui.theme.AppTheme
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import javax.inject.Inject

@HiltAndroidTest
@RunWith(AndroidJUnit4::class)
class NavigationInstrumentedTest {
    private lateinit var context: Context

    @Inject lateinit var constants: Constants

    @get:Rule(order = 0)
    var hiltRule = HiltAndroidRule(this)

    @get:Rule(order = 1)
    val composeTestRule = createAndroidComposeRule<HiltTestActivity>()

    private fun setAppScreen(challengeId: String? = null) {
        composeTestRule.setContent {
            AppTheme {
                AppRootScreen(challengeId = challengeId)
            }
        }
    }

    private fun goToMenu() {
        composeTestRule.onNodeWithText(context.getString(R.string.start_button), ignoreCase = true).performClick()
        composeTestRule.onNodeWithText(context.getString(R.string.menu)).assertIsDisplayed()
    }

    private fun goToNewRound() {
        goToMenu()
        composeTestRule.onNodeWithText(context.getString(R.string.menu_start_new_game), ignoreCase = true).performClick()
        composeTestRule.onNodeWithTag("keyboard").assertIsDisplayed()
    }

    private fun canNavigateBackToMenu() {
        composeTestRule.onNodeWithContentDescription(context.getString(R.string.back_button)).performClick()
        composeTestRule.onNodeWithText(context.getString(R.string.menu)).assertIsDisplayed()
    }

    @Before
    fun setup() {
        hiltRule.inject()
        context = composeTestRule.activity
        setAppScreen()
    }

    @Test
    fun navigateToMenuScreen() {
        goToMenu()
    }

    @Test
    fun navigateToNewRoundScreen() {
        goToNewRound()
    }

    @Test
    fun navigateToLastRoundScreen() {
        goToMenu()
        // Navigate from Menu to Last Round
        composeTestRule.onNodeWithText(context.getString(R.string.menu_play_last_game), ignoreCase = true).performClick()
        composeTestRule.onNodeWithTag("keyboard").assertIsDisplayed()
    }

    @Test
    fun checkRoundScreenCannotNavigateBack() {
        goToNewRound()
        // Check cannot navigate back from round screen
        pressBack()
        composeTestRule.onNodeWithTag("keyboard").assertIsDisplayed()
        // Check that back button is not visible in round screen
        composeTestRule.onNodeWithContentDescription(context.getString(R.string.back_button)).assertIsNotDisplayed()
    }

    @OptIn(ExperimentalTestApi::class)
    @Test
    fun navigateToSummaryScreen_afterRoundTimedOut() {
        goToNewRound()
        // Navigate from Round to Summary after round is finished
        composeTestRule.waitUntilAtLeastOneExists(
            hasText(context.getString(R.string.final_score)),
            (constants.ROUND_TIME_SECONDS + 10).toLong() * 1000
        )
    }

    @OptIn(ExperimentalTestApi::class)
    @Test
    fun navigateToMenuScreen_fromSummaryScreen(){
        goToNewRound()
        // Navigate from Round to Summary after round is finished
        composeTestRule.waitUntilAtLeastOneExists(
            hasText(context.getString(R.string.final_score)),
            (constants.ROUND_TIME_SECONDS + 10).toLong() * 1000
        )
        // Navigate from Summary back to Menu
        composeTestRule.onNodeWithText(context.getString(R.string.menu), ignoreCase = true).performClick()
        composeTestRule.onNodeWithText(context.getString(R.string.menu)).assertIsDisplayed()
    }

    @Test
    fun navigateToShareScreen_fromMenuScreen(){
        goToMenu()
        // Navigate from Menu to Share Screen
        composeTestRule.onNodeWithText(context.getString(R.string.menu_share_new_game), ignoreCase = true).performClick()
        composeTestRule.onNodeWithText(context.getString(R.string.share_round_desc)).assertIsDisplayed()
        canNavigateBackToMenu()
    }

    @Test
    fun navigateToStatsScreen(){
        goToMenu()
        // Navigate from Menu to Stats Screen
        composeTestRule.onNodeWithText(context.getString(R.string.menu_your_scores), ignoreCase = true).performClick()
        composeTestRule.onNodeWithText(context.getString(R.string.your_best_round)).assertIsDisplayed()
        canNavigateBackToMenu()
    }

    @Test
    fun navigateToSettingsScreen(){
        goToMenu()
        // Navigate from Menu to Settings Screen
        composeTestRule.onNodeWithText(context.getString(R.string.menu_settings), ignoreCase = true).performClick()
        composeTestRule.onNodeWithText(context.getString(R.string.settings_game_group)).assertIsDisplayed()
        canNavigateBackToMenu()
    }
}
