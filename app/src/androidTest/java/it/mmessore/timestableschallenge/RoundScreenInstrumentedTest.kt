package it.mmessore.timestableschallenge

import androidx.activity.ComponentActivity
import androidx.compose.ui.semantics.SemanticsProperties
import androidx.compose.ui.test.ExperimentalTestApi
import androidx.compose.ui.test.assertTextEquals
import androidx.compose.ui.test.hasText
import androidx.compose.ui.test.junit4.AndroidComposeTestRule
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.performClick
import androidx.test.ext.junit.rules.ActivityScenarioRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import it.mmessore.timestableschallenge.data.FakeRoundGenerator
import it.mmessore.timestableschallenge.data.FakeRepository
import it.mmessore.timestableschallenge.data.Quest
import it.mmessore.timestableschallenge.data.RoundGeneratorImpl
import it.mmessore.timestableschallenge.data.persistency.FakeAppPreferences
import it.mmessore.timestableschallenge.data.persistency.FakeConstants
import it.mmessore.timestableschallenge.ui.screens.RoundScreen
import it.mmessore.timestableschallenge.ui.screens.RoundViewModel
import it.mmessore.timestableschallenge.ui.theme.AppTheme
import it.mmessore.timestableschallenge.utils.fakeRoundViewModel
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
    private lateinit var fakeRepository: FakeRepository
    private lateinit var fakeRoundGenerator: FakeRoundGenerator
    private lateinit var viewModel: RoundViewModel

    @Before
    fun setup() {
        hiltRule.inject()
        fakeRepository = FakeRepository(composeTestRule.activity).apply {
            setCurrentAchievement()
        }
        fakeRoundGenerator = FakeRoundGenerator(RoundGeneratorImpl(FakeAppPreferences()).generate())
        viewModel = fakeRoundViewModel(
            activity = composeTestRule.activity,
            quests = fakeRoundGenerator.quests,
            fakeRepository = fakeRepository
        )
    }

    @Test
    fun roundCompleted_allQuestionsAnswered() {
        testRound(composeTestRule, viewModel, fakeRoundGenerator.quests, 0)
    }

    @Test
    fun roundCompleted_allQuestionsAnsweredWithHalfErrors() {
        testRound(composeTestRule, viewModel, fakeRoundGenerator.quests, 1)
    }

    @Test
    fun roundCompleted_halfQuestionsAnswered() {
        testRound(composeTestRule, viewModel, fakeRoundGenerator.quests, 0, fakeRoundGenerator.quests.size / 2)
    }

    @Test
    fun roundCompleted_halfQuestionsAnsweredWithHalfErrors() {
        testRound(composeTestRule, viewModel, fakeRoundGenerator.quests, 1, fakeRoundGenerator.quests.size / 2)
    }

    @Test
    fun roundTimedUp_allQuestionsAnswered() {
        val fakeConstants = FakeConstants(ROUND_TIME_SECONDS = 5)
        viewModel = fakeRoundViewModel(
            activity = composeTestRule.activity,
            quests = fakeRoundGenerator.quests,
            fakeConstants = fakeConstants,
            fakeRepository = fakeRepository
        )
        testRound(composeTestRule, viewModel, fakeRoundGenerator.quests, 0)
    }

    @OptIn(ExperimentalTestApi::class)
    fun testRound(
        composeTestRule: AndroidComposeTestRule<ActivityScenarioRule<ComponentActivity>, ComponentActivity>,
        viewModel: RoundViewModel,
        quests: List<Quest>,
        errorsBeforeAnswer: Int,
        targetScore: Int = quests.size
    ) {
        composeTestRule.setContent {
            AppTheme {
                RoundScreen(viewModel = viewModel)
            }
        }
        val answers = mutableListOf<Quest>()
        quests.forEach { quest ->
            repeat(errorsBeforeAnswer + 1) {
                answers.add(quest)
            }
        }
        var score = 0
        var idx = 0
        while (idx < answers.size && score < targetScore) {
            val q = answers[idx]
            val timeLeft = viewModel.timeLeft.value

            // Place the correct answer only if time is not too close to end (for slow emulators)
            if (timeLeft > 2) {
                val answerCorrect = if (errorsBeforeAnswer > 0) {
                    if (idx % (errorsBeforeAnswer + 1) != 0) 1 else 0
                } else 1
                composeTestRule.onNodeWithTag("score").assertTextEquals(score.toString())
                composeTestRule.waitUntilAtLeastOneExists(hasText("${q.op1} x ${q.op2} = ?"), 5000)
                val charArray= q.answer().times(answerCorrect).toString().toCharArray()
                charArray.forEach {
                    composeTestRule.onNodeWithTag("numberButton_$it").performClick()
                }
                composeTestRule.onNodeWithContentDescription("Next").performClick()
                score += answerCorrect
            }
            idx++
        }
        // Check that the game is over
        composeTestRule.waitUntilAtLeastOneExists(
            hasText(composeTestRule.activity.getString(
                if (score < quests.size)
                    R.string.game_over
                else
                    R.string.round_complete
            )),
            (viewModel.timeLeft.value.toLong() + 5) * 1000
        )
        // Check round info into viewmodel
        assert(RoundGeneratorImpl.serialize(quests) == viewModel.finishedRound?.roundId)
        assert(score == viewModel.finishedRound?.score)
    }
}