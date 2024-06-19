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


    private val timer = object : CountDownTimer((ROUND_TIME_SECONDS * 1000).toLong(), 1000) { // 30 secondi (30000 millisecondi), aggiornamento ogni secondo (1000 millisecondi)
        override fun onTick(millisUntilFinished: Long) {
            _timeLeft.value = (millisUntilFinished / 1000).toInt()
        }

        override fun onFinish() {
            finishRound()
        }
    }

    fun startRound() {
        viewModelScope.launch {
            _roundState.value = RoundState.IN_PROGRESS
            delay(1500)
            timer.start()
        }
    }

    fun resetRound(newRound: Boolean = true) {
        viewModelScope.launch {
            _score.value = DEFAULT_SCORE
            _answer.value = NO_ANSWER
            timer.cancel()
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
            _currentQuest.value = quests[++currentQuestIdx]
        } else {
            playSound(playerError)
        }

        if (currentQuestIdx < quests.size - 1) {
            _answer.value = NO_ANSWER
        } else {
            finishRound()
        }
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

    private fun finishRound() {
        viewModelScope.launch {
            val finishedRound = Round(
                timestamp = System.currentTimeMillis(),
                roundId = RoundGenerator.serialize(quests),
                score = _score.value
            )
            repository.insertRound(finishedRound)
            _roundState.emit(RoundState.FINISHED)
        }
    }

    fun getRoundId(): String = RoundGenerator.serialize(quests)
}