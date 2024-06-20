package it.mmessore.timestableschallenge.ui.screens

import android.media.MediaPlayer
import android.os.CountDownTimer
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import it.mmessore.timestableschallenge.data.Levels
import it.mmessore.timestableschallenge.data.Quest
import it.mmessore.timestableschallenge.data.RoundGenerator
import it.mmessore.timestableschallenge.data.RoundRepository
import it.mmessore.timestableschallenge.data.persistency.Constants.Companion.ROUND_TIME_SECONDS
import it.mmessore.timestableschallenge.data.persistency.Round
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.transform
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class RoundViewModel @Inject constructor(private val repository: RoundRepository): ViewModel() {
    enum class RoundState {
        STARTING,
        IN_PROGRESS,
        FINISHED
    }

    companion object {
        const val NO_ANSWER = "_"
        private const val DEFAULT_SCORE = 0
    }

    private var quests: List<Quest> = RoundGenerator().generate()
    private var currentQuestIdx = 0
    private var timeLeftMillis: Long = 0
    private var lastTickTime: Long = 0

    private val _answer = MutableStateFlow(NO_ANSWER)
    val answer: StateFlow<String> = _answer
    private val _score = MutableStateFlow(DEFAULT_SCORE)
    val score: StateFlow<Int> = _score
    private val _timeLeft = MutableStateFlow(ROUND_TIME_SECONDS)
    val timeLeft: StateFlow<Int> = _timeLeft
    private val _roundState = MutableStateFlow(RoundState.STARTING)
    val roundState: StateFlow<RoundState> = _roundState
    private val _currentQuest = MutableStateFlow(quests[currentQuestIdx])
    val currentQuest: StateFlow<Quest> = _currentQuest
    val level = score.transform { score -> emit (Levels.getLevelByScore(score)) }


    private val timer = object : CountDownTimer((ROUND_TIME_SECONDS * 1000).toLong(), 1000) {
        override fun onTick(millisUntilFinished: Long) {
            _timeLeft.value = (millisUntilFinished / 1000).toInt()
            timeLeftMillis = millisUntilFinished
            lastTickTime = System.currentTimeMillis()
        }

        override fun onFinish() {
            finishRound(0)
        }
    }

    fun startRound() {
        viewModelScope.launch {
            _roundState.value = RoundState.IN_PROGRESS
            delay(1000)
            timer.start()
        }
    }

    fun resetRound(newRound: Boolean = true) {
        viewModelScope.launch {
            _score.value = DEFAULT_SCORE
            _answer.value = NO_ANSWER
            _timeLeft.value = ROUND_TIME_SECONDS
            _roundState.value = RoundState.STARTING
            currentQuestIdx = 0
            quests = if (newRound) {
                RoundGenerator().generate()
            } else {
                repository.lastRoundQuests()
            }
            _currentQuest.value = quests[currentQuestIdx]
        }
    }

    fun onAnswer(answer: String, playerSuccess: MediaPlayer, playerError: MediaPlayer) {
        if (_roundState.value != RoundState.IN_PROGRESS)
            return

        if (answer != NO_ANSWER && answer.toInt() == _currentQuest.value.answer()) {
            playSound(playerSuccess)
            _score.value++
            if (currentQuestIdx == quests.size - 1) {
                timer.cancel()
                val timeleft = (timeLeftMillis - (System.currentTimeMillis() - lastTickTime)).toInt()
                finishRound(timeleft)
            } else {
                currentQuestIdx++
                _currentQuest.value = quests[currentQuestIdx]
            }
        } else {
            playSound(playerError)
        }
        _answer.value = NO_ANSWER
    }

    fun onBackspace() {
        if (_answer.value.length == 1)
            _answer.value = NO_ANSWER
        else
            _answer.value = _answer.value.dropLast(1)
    }

    fun onNumberClick(number: Char) {
        if (_answer.value.length < 2 && _roundState.value == RoundState.IN_PROGRESS)
            _answer.value = _answer.value.plus(number.toString()).replace(NO_ANSWER, "")
    }

    private fun playSound(player: MediaPlayer) {
        if (player.isPlaying)
            player.seekTo(0)
        else
            player.start()
    }

    private fun finishRound(timeLeft: Int = 0) {
        viewModelScope.launch {
            if (roundState.value == RoundState.IN_PROGRESS) {
                _roundState.value = RoundState.FINISHED
                val finishedRound = Round(
                    timestamp = System.currentTimeMillis(),
                    roundId = RoundGenerator.serialize(quests),
                    score = _score.value,
                    timeLeft = timeLeft
                )
                repository.insertRound(finishedRound)
            }
        }
    }

    fun getRoundId(): String = RoundGenerator.serialize(quests)
}