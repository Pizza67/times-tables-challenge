package it.mmessore.timestableschallenge.ui.screens

import android.media.MediaPlayer
import android.os.CountDownTimer
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import it.mmessore.timestableschallenge.data.AppRepository
import it.mmessore.timestableschallenge.data.Quest
import it.mmessore.timestableschallenge.data.RoundGenerator
import it.mmessore.timestableschallenge.data.RoundGeneratorImpl
import it.mmessore.timestableschallenge.data.persistency.AppPreferences
import it.mmessore.timestableschallenge.data.persistency.Constants
import it.mmessore.timestableschallenge.data.persistency.Round
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class RoundViewModel @Inject constructor(
    private val repository: AppRepository,
    roundGenerator: RoundGenerator,
    private val appPreferences: AppPreferences,
    private val constants: Constants,
    private val coroutineScope: CoroutineScope
): ViewModel() {
    enum class RoundState {
        STARTING,
        IN_PROGRESS,
        FINISHED
    }

    enum class FinishReason {
        TIME_UP,
        COMPLETED
    }

    companion object {
        const val NO_ANSWER = "_"
        private const val DEFAULT_SCORE = 0
    }

    private var quests: List<Quest> = roundGenerator.generate()
    private var currentQuestIdx = 0
    private var timeLeftMillis: Long = 0
    private var lastTickTime: Long = 0
    private var finishReason: FinishReason? = null
    var finishedRound: Round? = null
        private set

    private val _answer = MutableStateFlow(NO_ANSWER)
    val answer: StateFlow<String> = _answer
    private val _score = MutableStateFlow(DEFAULT_SCORE)
    val score: StateFlow<Int> = _score
    private val _timeLeft = MutableStateFlow(constants.ROUND_TIME_SECONDS)
    val timeLeft: StateFlow<Int> = _timeLeft
    private val _roundState = MutableStateFlow(RoundState.STARTING)
    val roundState: StateFlow<RoundState> = _roundState
    private val _currentQuest = MutableStateFlow(quests[currentQuestIdx])
    val currentQuest: StateFlow<Quest> = _currentQuest
    private val _submitAnswer = MutableStateFlow(NO_ANSWER)
    val submitAnswer: StateFlow<String> = _submitAnswer

    private lateinit var timer: CountDownTimer

    init {
        viewModelScope.launch {
            timer = object : CountDownTimer((constants.ROUND_TIME_SECONDS * 1000).toLong(), 1000) {
                override fun onTick(millisUntilFinished: Long) {
                    _timeLeft.value = (millisUntilFinished / 1000).toInt()
                    timeLeftMillis = millisUntilFinished
                    lastTickTime = System.currentTimeMillis()
                }

                override fun onFinish() {
                    _timeLeft.value = 0
                    finishRound(finalScore = _score.value, reason = FinishReason.TIME_UP)
                }
            }
        }
    }

    fun startRound() {
        viewModelScope.launch {
            _roundState.value = RoundState.IN_PROGRESS
            delay(1000)
            timer.start()
        }
    }

    fun setRound(roundId: String? = null) {
        _score.value = DEFAULT_SCORE
        _answer.value = NO_ANSWER
        _timeLeft.value = constants.ROUND_TIME_SECONDS
        _roundState.value = RoundState.STARTING
        currentQuestIdx = 0
        finishReason = null
        quests = if (roundId != null) {
            RoundGeneratorImpl.deserialize(roundId)
        } else  {
            RoundGeneratorImpl(appPreferences).generate()
        }
        _currentQuest.value = quests[currentQuestIdx]
    }

    fun setLastRound() {
        viewModelScope.launch (context = coroutineScope.coroutineContext) {
            setRound(repository.lastRound()?.roundId)
        }
    }

    fun onAnswer(answer: String, playerSuccess: MediaPlayer, playerError: MediaPlayer) {
        if (_roundState.value != RoundState.IN_PROGRESS)
            return
        if (answer != NO_ANSWER && answer.toInt() == _currentQuest.value.answer()) {
            playSound(playerSuccess)
            _score.value++
            askNextQuestion()
        } else {
            playSound(playerError)
            if (isAutoConfirmEnabled())
                askNextQuestion()
        }
        _answer.value = NO_ANSWER
    }

    private fun isLastQuestion() = currentQuestIdx == quests.size - 1

    fun getFinishReason() = finishReason

    private fun askNextQuestion() {
        if (isLastQuestion()) {
            // Round completed (all questions answered)
            timer.cancel()
            // In case auto confirmation is enabled, time left is computed only
            // if all questions are answered otherwise is 0
            val timeLeft = if (isAutoConfirmEnabled() && _score.value < quests.size) 0 else (timeLeftMillis - (System.currentTimeMillis() - lastTickTime)).toInt()
            val finalScore = if (isAutoConfirmEnabled()) _score.value else quests.size
            finishRound(finalScore, timeLeft, FinishReason.COMPLETED)
        } else {
            currentQuestIdx++
            _currentQuest.value = quests[currentQuestIdx]
        }
        _submitAnswer.value = NO_ANSWER
    }

    fun onBackspace() {
        if (_answer.value.length == 1)
            _answer.value = NO_ANSWER
        else
            _answer.value = _answer.value.dropLast(1)
    }

    fun onNumberClick(number: Char) {
        // Add the digit only if the answer size is not yet met and the round is in progress
        if (!isAnswerLengthReached() && _roundState.value == RoundState.IN_PROGRESS)
            _answer.value = _answer.value.plus(number.toString()).replace(NO_ANSWER, "")

        // If auto confirm mode is on and the answer is complete, submit the answer
        if (isAutoConfirmEnabled() && isAnswerLengthReached())
            _submitAnswer.value = _answer.value
    }

    // Check if the answer length is reached (except NO_ANSWER)
    fun isAnswerLengthReached() =
        _answer.value.replace(NO_ANSWER, "").length >= _currentQuest.value.answerLength()

    fun isAutoConfirmEnabled() = appPreferences.autoConfirm

    private fun playSound(player: MediaPlayer) {
        if (appPreferences.playSounds) {
            if (player.isPlaying)
                player.seekTo(0)
            else
                player.start()
        }
    }

    private fun finishRound(
        finalScore: Int,
        timeLeft: Int = 0,
        reason: FinishReason?
    ) {
        viewModelScope.launch (context = coroutineScope.coroutineContext) {
            if (roundState.value == RoundState.IN_PROGRESS) {
                finishReason = reason
                _roundState.value = RoundState.FINISHED
                finishedRound = Round(
                    timestamp = System.currentTimeMillis(),
                    roundId = RoundGeneratorImpl.serialize(quests),
                    score = finalScore,
                    timeLeft = timeLeft
                ).also { round ->
                    repository.insertRound(round)
                }
            }
        }
    }
}