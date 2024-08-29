package it.mmessore.timestableschallenge

import android.util.Log
import androidx.activity.ComponentActivity
import androidx.compose.ui.test.ExperimentalTestApi
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertIsNotEnabled
import androidx.compose.ui.test.assertTextEquals
import androidx.compose.ui.test.hasText
import androidx.compose.ui.test.isDisplayed
import androidx.compose.ui.test.junit4.AndroidComposeTestRule
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.test.ext.junit.rules.ActivityScenarioRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import dagger.hilt.android.testing.HiltAndroidTest
import it.mmessore.timestableschallenge.data.FakeRepository
import it.mmessore.timestableschallenge.data.FakeRoundGenerator
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
    @get:Rule
    val composeTestRule = createAndroidComposeRule<ComponentActivity>()
    private lateinit var viewModel: RoundViewModel

    private lateinit var fakePreferences: FakeAppPreferences
    private lateinit var fakeRoundGenerator: FakeRoundGenerator

    private fun getViewModelBySettings(
        roundQuests: Int = 10,
        roundTimeSeconds: Int = FakeConstants().ROUND_TIME_SECONDS,
        extendedMode: Boolean = false,
        autoConfirm: Boolean = false
    ): RoundViewModel {
        fakePreferences = FakeAppPreferences(numQuestions = roundQuests, extendedMode = extendedMode,autoConfirm = autoConfirm)
        fakeRoundGenerator = FakeRoundGenerator(fakePreferences)
        return fakeRoundViewModel(
            activity = composeTestRule.activity,
            fakeRoundGenerator = fakeRoundGenerator,
            fakeRepository = FakeRepository(composeTestRule.activity),
            fakeConstants = FakeConstants(ROUND_TIME_SECONDS = roundTimeSeconds),
            fakePreferences = fakePreferences
        )
    }

    @Before
    fun setup() {
        viewModel = getViewModelBySettings()
    }

    @Test
    fun roundCompleted_allQuestionsAnswered() {
        testRound(composeTestRule, viewModel, fakeRoundGenerator.quests)
    }

    @Test
    fun roundCompleted_allQuestionsAnsweredWithHalfErrors() {
        testRound(composeTestRule, viewModel, fakeRoundGenerator.quests, .5f)
    }

    @Test
    fun roundCompleted_autoConfirm_allQuestionsAnswered() {
        testRound(composeTestRule, getViewModelBySettings(autoConfirm = true), fakeRoundGenerator.quests)
    }

    @Test
    fun roundCompleted_extendedMode_allQuestionsAnswered() {
        testRound(composeTestRule, getViewModelBySettings(extendedMode = true), fakeRoundGenerator.quests)
    }

    @Test
    fun roundCompleted_halfQuestionsAnswered() {
        testRound(
            composeTestRule = composeTestRule,
            viewModel = viewModel,
            quests = fakeRoundGenerator.quests,
            targetScore = fakeRoundGenerator.quests.size / 2
        )
    }

    @Test
    fun roundCompleted_allQuestionsAnsweredButLast() {
        testRound(
            composeTestRule = composeTestRule,
            viewModel = viewModel,
            quests = fakeRoundGenerator.quests,
            targetScore = fakeRoundGenerator.quests.size.minus(1)
        )
    }

    @Test
    fun roundCompleted_autoConfirm_allQuestionsAnsweredButLast() {
        testRound(
            composeTestRule = composeTestRule,
            viewModel = getViewModelBySettings(autoConfirm = true),
            quests = fakeRoundGenerator.quests,
            targetScore = fakeRoundGenerator.quests.size.minus(1)
        )
    }

    @Test
    fun roundCompleted_halfQuestionsAnsweredWithHalfErrors() {
        testRound(composeTestRule, viewModel, fakeRoundGenerator.quests, .5f, fakeRoundGenerator.quests.size / 2)
    }

    @Test
    fun roundTimedUp_allQuestionsAnswered() {
        testRound(composeTestRule, getViewModelBySettings(roundTimeSeconds = 5), fakeRoundGenerator.quests)
    }

    @Test
    fun roundTimedUp_autoConfirm_allQuestionsAnswered() {
        testRound(composeTestRule, getViewModelBySettings(roundTimeSeconds = 5, autoConfirm = true), fakeRoundGenerator.quests)
    }

    @OptIn(ExperimentalTestApi::class)
    fun testRound(
        composeTestRule: AndroidComposeTestRule<ActivityScenarioRule<ComponentActivity>, ComponentActivity>,
        viewModel: RoundViewModel,
        quests: List<Quest>,
        errorRatio: Float = 0f,
        targetScore: Int = quests.size
    ) {
        composeTestRule.setContent {
            AppTheme {
                RoundScreen(viewModel = viewModel)
            }
        }
        val tentativePerAnswer = if (viewModel.isAutoConfirmEnabled() || errorRatio == 0f) 1 else (1f/errorRatio).toInt()
        // answers size is the number of answers that will be given in the whole round
        val answers = mutableListOf<Quest>()
        quests.forEach { quest ->
            repeat(tentativePerAnswer) {
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
                val answerCorrect = idx % tentativePerAnswer == tentativePerAnswer.minus(1)
                Log.d("testRoundLog", "answerCorrect: $answerCorrect - ${q.op1} x ${q.op2}")

                composeTestRule.onNodeWithTag("score").assertTextEquals(score.toString())
                composeTestRule.waitUntilAtLeastOneExists(hasText("${q.op1} x ${q.op2} = ?"), 5000)
                val answer = if (answerCorrect) q.answer() else q.answer() + 1
                val charArray = answer.toString().toCharArray()
                Log.d("testRoundLog", "charArray: ${charArray.contentToString()}")
                charArray.forEach {
                    composeTestRule.onNodeWithTag("numberButton_$it").performClick()
                }
                if (viewModel.isAutoConfirmEnabled())
                    composeTestRule.onNodeWithContentDescription("Next").assertIsNotEnabled()
                else
                    composeTestRule.onNodeWithContentDescription("Next").performClick()
                score += if (answerCorrect) 1 else 0
            }
            idx++
        }
        // Check that the game is over
        composeTestRule.waitUntilAtLeastOneExists(
            hasText(composeTestRule.activity.getString(R.string.game_over))
                .or(hasText(composeTestRule.activity.getString(R.string.round_complete))),
            (viewModel.timeLeft.value.toLong() + 10) * 1000
        )
        if (idx == answers.size)
            composeTestRule.onNodeWithText(composeTestRule.activity.getString(R.string.round_complete)).assertIsDisplayed()
        else
            composeTestRule.onNodeWithText(composeTestRule.activity.getString(R.string.game_over)).assertIsDisplayed()
        // Check round info into viewmodel
        assert(RoundGeneratorImpl.serialize(quests) == viewModel.finishedRound?.roundId)
        assert(score == viewModel.finishedRound?.score)
        // Check all requested answers have been answered
        assert(score == targetScore)
    }
}