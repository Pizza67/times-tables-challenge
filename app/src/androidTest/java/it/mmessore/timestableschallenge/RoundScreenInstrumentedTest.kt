package it.mmessore.timestableschallenge

import androidx.activity.ComponentActivity
import androidx.compose.ui.test.ExperimentalTestApi
import androidx.compose.ui.test.assertTextEquals
import androidx.compose.ui.test.hasText
import androidx.compose.ui.test.junit4.AndroidComposeTestRule
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.performClick
import androidx.lifecycle.lifecycleScope
import androidx.test.ext.junit.rules.ActivityScenarioRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import it.mmessore.timestableschallenge.data.FakeRoundGenerator
import it.mmessore.timestableschallenge.data.FakeSummaryRepository
import it.mmessore.timestableschallenge.data.Quest
import it.mmessore.timestableschallenge.data.RoundGeneratorImpl
import it.mmessore.timestableschallenge.data.persistency.FakeAppPreferences
import it.mmessore.timestableschallenge.data.persistency.FakeConstants
import it.mmessore.timestableschallenge.ui.screens.RoundScreen
import it.mmessore.timestableschallenge.ui.screens.RoundViewModel
import it.mmessore.timestableschallenge.ui.theme.AppTheme
import kotlinx.coroutines.CoroutineScope
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@HiltAndroidTest
@RunWith(AndroidJUnit4::class)
class RoundScreenInstrumentedTest {
    @get:Rule(order = 0)
    var hiltRule = HiltAndroidRule(this)

    @get:Rule(order = 1)
    val composeTestRule = createAndroidComposeRule<ComponentActivity>()
    private lateinit var fakeConstants: FakeConstants
    private lateinit var fakePreferences: FakeAppPreferences
    private lateinit var fakeRepository: FakeSummaryRepository
    private lateinit var quests: List<Quest>
    private lateinit var fakeRoundGenerator: FakeRoundGenerator
    private lateinit var viewModel: RoundViewModel
    private lateinit var coroutineScope: CoroutineScope

    @Before
    fun setup() {
        hiltRule.inject()
        fakeConstants = FakeConstants()
        fakePreferences = FakeAppPreferences()
        fakeRepository = FakeSummaryRepository(composeTestRule.activity)
        fakeRepository.setCurrentAchievement()
        quests = RoundGeneratorImpl(fakePreferences).generate()
        fakeRoundGenerator = FakeRoundGenerator(quests)
        coroutineScope = composeTestRule.activity.lifecycleScope
        viewModel = RoundViewModel(fakeRepository, fakeRoundGenerator, fakePreferences, fakeConstants, coroutineScope)
    }

    @OptIn(ExperimentalTestApi::class)
    fun testRound(
        composeTestRule: AndroidComposeTestRule<ActivityScenarioRule<ComponentActivity>, ComponentActivity>,
        viewModel: RoundViewModel,
        quests: List<Quest>,
        errorsBeforeAnswer: Int
    ) {
        composeTestRule.setContent {
            AppTheme {
                RoundScreen(viewModel = viewModel)}
        }
        val answers = mutableListOf<Quest>()
        quests.forEach { quest ->
            repeat(errorsBeforeAnswer + 1) {
                answers.add(quest)
            }
        }
        var score = 0
        answers.forEachIndexed { idx, q ->
            val answerCorrect = if (errorsBeforeAnswer > 0) {
                if (idx % (errorsBeforeAnswer + 1) != 0) 1 else 0
            } else 1
            composeTestRule.onNodeWithTag("score").assertTextEquals(score.toString())
            score += answerCorrect
            composeTestRule.waitUntilAtLeastOneExists(hasText("${q.op1} x ${q.op2} = ?"), 5000)
            val charArray = q.answer().times(answerCorrect).toString().toCharArray()
            charArray.forEach {
                composeTestRule.onNodeWithTag("numberButton_$it").performClick()
            }
            composeTestRule.onNodeWithContentDescription("Next").performClick()
        }
    }

    @OptIn(ExperimentalTestApi::class)
    @Test
    fun roundCompleted_allQuestionsAnswered() {
        testRound(composeTestRule, viewModel, quests, 0)
    }

    @OptIn(ExperimentalTestApi::class)
    @Test
    fun roundCompleted_allQuestionsAnsweredWithHalfErrors() {
        testRound(composeTestRule, viewModel, quests, 1)
    }

}