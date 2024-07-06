package it.mmessore.timestableschallenge

import androidx.activity.ComponentActivity
import androidx.compose.ui.test.ExperimentalTestApi
import androidx.compose.ui.test.hasText
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.lifecycle.lifecycleScope
import androidx.test.ext.junit.runners.AndroidJUnit4
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import it.mmessore.timestableschallenge.data.FakeSummaryRepository
import it.mmessore.timestableschallenge.ui.screens.SummaryScreen
import it.mmessore.timestableschallenge.ui.screens.SummaryViewModel
import it.mmessore.timestableschallenge.ui.theme.AppTheme
import kotlinx.coroutines.CoroutineScope
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@HiltAndroidTest
@RunWith(AndroidJUnit4::class)
class SummaryScreenInstrumentedTest {

    @get:Rule(order = 0)
    var hiltRule = HiltAndroidRule(this)

    @get:Rule(order = 1)
    val composeTestRule = createAndroidComposeRule<ComponentActivity>()
    private lateinit var fakeRepository: FakeSummaryRepository
    private lateinit var viewModel: SummaryViewModel
    private lateinit var coroutineScope: CoroutineScope

    @Before
    fun setup() {
        hiltRule.inject()
        fakeRepository = FakeSummaryRepository(composeTestRule.activity)
        fakeRepository.setCurrentAchievement()
        coroutineScope = composeTestRule.activity.lifecycleScope
        viewModel = SummaryViewModel(fakeRepository, coroutineScope)
    }

    @OptIn(ExperimentalTestApi::class)
    @Test
    fun rewardDialog_isShownWhenNewAchievementUnlocked() {
        fakeRepository.setNewAchievementUnlocked(true)
        composeTestRule.setContent {
            AppTheme {
                SummaryScreen(roundId = "testRoundId", viewModel = viewModel)
            }
        }
        composeTestRule.waitUntilAtLeastOneExists(
            hasText(composeTestRule.activity.getString(R.string.new_achievement), ignoreCase = true), 20000
        )
    }

    @OptIn(ExperimentalTestApi::class)
    @Test
    fun bestRoundDialog_isShownOnNewBestRound() {
        fakeRepository.setIsNewBestRound(true)

        composeTestRule.setContent {
            AppTheme {
                SummaryScreen(roundId = "testRoundId", viewModel = viewModel)
            }
        }
        composeTestRule.waitUntilAtLeastOneExists(
            hasText(composeTestRule.activity.getString(R.string.new_best_round), ignoreCase = true), 20000
        )
    }
}