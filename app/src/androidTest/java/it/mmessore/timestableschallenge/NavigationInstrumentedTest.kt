package it.mmessore.timestableschallenge

import android.content.Context
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.test.ExperimentalTestApi
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertIsNotDisplayed
import androidx.compose.ui.test.hasText
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.compose.ComposeNavigator
import androidx.navigation.testing.TestNavHostController
import androidx.test.espresso.Espresso.pressBack
import androidx.test.ext.junit.runners.AndroidJUnit4
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import dagger.hilt.android.testing.UninstallModules
import it.mmessore.timestableschallenge.data.persistency.FakeConstants
import it.mmessore.timestableschallenge.di.ApplicationModule
import it.mmessore.timestableschallenge.ui.screens.AppRootScreen
import it.mmessore.timestableschallenge.ui.screens.RoundViewModel
import it.mmessore.timestableschallenge.ui.theme.AppTheme
import it.mmessore.timestableschallenge.utils.fakeRoundViewModel
import it.mmessore.timestableschallenge.utils.overrideActivityContent
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith


@HiltAndroidTest
@UninstallModules(ApplicationModule::class)
@RunWith(AndroidJUnit4::class)
class NavigationInstrumentedTest {
    private lateinit var navController: TestNavHostController
    private lateinit var context: Context
    private lateinit var fakeConstants: FakeConstants

    @get:Rule(order = 0)
    var hiltRule = HiltAndroidRule(this)

    @get:Rule(order = 1)
    val composeTestRule = createAndroidComposeRule<MainActivity>()

    private fun setAppScreen(viewModel: RoundViewModel? = null, challengeId: String? = null) {
        composeTestRule.overrideActivityContent {
            navController = TestNavHostController(LocalContext.current).apply {
                navigatorProvider.addNavigator(ComposeNavigator())
            }
            AppTheme {
                AppRootScreen(
                    navController = navController,
                    roundViewModel = viewModel ?: hiltViewModel(),
                    challengeId = challengeId
                )
            }
        }
    }

    private fun canNavigateBackToMenu() {
        composeTestRule.onNodeWithContentDescription(context.getString(R.string.back_button)).performClick()
        composeTestRule.onNodeWithText(context.getString(R.string.menu)).assertIsDisplayed()
    }

    @Before
    fun setup() {
        fakeConstants = FakeConstants(ROUND_TIME_SECONDS = 1)
        val viewModel = fakeRoundViewModel(composeTestRule.activity, fakeConstants = fakeConstants)
        hiltRule.inject()
        context = composeTestRule.activity
        setAppScreen(viewModel)
    }

    @Test
    fun navigateToMenuScreen() {
        // Navigate frome Home to Menu
        composeTestRule.onNodeWithText(context.getString(R.string.start_button), ignoreCase = true).performClick()
        composeTestRule.onNodeWithText(context.getString(R.string.menu)).assertIsDisplayed()
    }

    @Test
    fun navigateToNewRoundScreen() {
        navigateToMenuScreen()
        // Navigate from Menu to New Round
        composeTestRule.onNodeWithText(context.getString(R.string.menu_start_new_game), ignoreCase = true).performClick()
        composeTestRule.onNodeWithTag("keyboard").assertIsDisplayed()
    }

    @Test
    fun navigateToLastRoundScreen() {
        navigateToMenuScreen()
        // Navigate from Menu to Last Round
        composeTestRule.onNodeWithText(context.getString(R.string.menu_play_last_game), ignoreCase = true).performClick()
        composeTestRule.onNodeWithTag("keyboard").assertIsDisplayed()
    }

    @Test
    fun checkRoundScreenCannotNavigateBack() {
        navigateToNewRoundScreen()
        // Check cannot navigate back from round screen
        pressBack()
        composeTestRule.onNodeWithTag("keyboard").assertIsDisplayed()
        // Check that back button is not visible in round screen
        composeTestRule.onNodeWithContentDescription(context.getString(R.string.back_button)).assertIsNotDisplayed()
    }

    @OptIn(ExperimentalTestApi::class)
    @Test
    fun navigateToSummaryScreen_afterRoundTimedOut() {
        navigateToNewRoundScreen()
        // Navigate from Round to Summary after round is finished
        composeTestRule.waitUntilAtLeastOneExists(
            hasText(context.getString(R.string.final_score)),
            (fakeConstants.ROUND_TIME_SECONDS + 5).toLong() * 1000
        )
    }

    @Test
    fun navigateToMenuScreen_fromSummaryScreen(){
        navigateToSummaryScreen_afterRoundTimedOut()
        // Navigate from Summary back to Menu
        composeTestRule.onNodeWithText(context.getString(R.string.menu), ignoreCase = true).performClick()
        composeTestRule.onNodeWithText(context.getString(R.string.menu)).assertIsDisplayed()
    }

    @Test
    fun navigateToShareScreen_fromMenuScreen(){
        navigateToMenuScreen()
        // Navigate from Menu to Share Screen
        composeTestRule.onNodeWithText(context.getString(R.string.menu_share_new_game), ignoreCase = true).performClick()
        composeTestRule.onNodeWithText(context.getString(R.string.share_round_desc)).assertIsDisplayed()
        canNavigateBackToMenu()
    }

    @Test
    fun navigateToShareScreen_fromAppLink(){
        setAppScreen(challengeId = "testRoundId")
        composeTestRule.onNodeWithText(context.getString(R.string.share_round_desc)).assertIsDisplayed()
    }

    @Test
    fun navigateToStatsScreen(){
        navigateToMenuScreen()
        // Navigate from Menu to Stats Screen
        composeTestRule.onNodeWithText(context.getString(R.string.menu_your_scores), ignoreCase = true).performClick()
        composeTestRule.onNodeWithText(context.getString(R.string.your_best_round)).assertIsDisplayed()
        canNavigateBackToMenu()
    }

    @Test
    fun navigateToSettingsScreen(){
        navigateToMenuScreen()
        // Navigate from Menu to Settings Screen
        composeTestRule.onNodeWithText(context.getString(R.string.menu_settings), ignoreCase = true).performClick()
        composeTestRule.onNodeWithText(context.getString(R.string.settings_game_group)).assertIsDisplayed()
        canNavigateBackToMenu()
    }
}