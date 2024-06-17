package it.mmessore.timestableschallenge.ui.screens

import android.media.MediaPlayer
import android.os.CountDownTimer
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import it.mmessore.timestableschallenge.R
import it.mmessore.timestableschallenge.data.Quest
import it.mmessore.timestableschallenge.data.RoundGenerator
import it.mmessore.timestableschallenge.data.RoundRepository
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
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
        private const val DEFAULT_TIME_LEFT = 30
        private const val DEFAULT_ROUND_QUESTS = 20

        private const val SCORE_LOW_LVL = 6
        private const val SCORE_MEDIUM_LVL = 12
        private const val SCORE_HIGH_LVL = 17
    }

    private var quests: List<Quest> = RoundGenerator().generate(DEFAULT_ROUND_QUESTS)
    private var currentQuestIdx = 0

    private val _answer = MutableStateFlow(NO_ANSWER)
    val answer: StateFlow<String> = _answer
    private val _score = MutableStateFlow(DEFAULT_SCORE)
    val score: StateFlow<Int> = _score
    private val _timeLeft = MutableStateFlow(DEFAULT_TIME_LEFT)
    val timeLeft: StateFlow<Int> = _timeLeft
    private val _roundState = MutableStateFlow(RoundState.STARTING)
    val roundState: StateFlow<RoundState> = _roundState
    private val _currentQuest = MutableStateFlow(quests[currentQuestIdx])
    val currentQuest: StateFlow<Quest> = _currentQuest


    private val timer = object : CountDownTimer((DEFAULT_TIME_LEFT * 1000).toLong(), 1000) { // 30 secondi (30000 millisecondi), aggiornamento ogni secondo (1000 millisecondi)
        override fun onTick(millisUntilFinished: Long) {
            _timeLeft.value = (millisUntilFinished / 1000).toInt()
        }

        override fun onFinish() {
            _roundState.value = RoundState.FINISHED
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
            delay(1000)
            _score.value = DEFAULT_SCORE
        }
        _answer.value = NO_ANSWER
        timer.cancel()
        _timeLeft.value = DEFAULT_TIME_LEFT
        _roundState.value = RoundState.STARTING
        currentQuestIdx = 0
        if (newRound)
            quests = RoundGenerator().generate(DEFAULT_ROUND_QUESTS)
        _currentQuest.value = quests[currentQuestIdx]
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
            _roundState.value = RoundState.FINISHED
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

    fun getScoreImageId(score: Int): Int = when (score) {
        in 0..SCORE_LOW_LVL -> R.drawable.img_score_low
        in SCORE_LOW_LVL+1..SCORE_MEDIUM_LVL -> R.drawable.img_score_medium
        in SCORE_MEDIUM_LVL+1..SCORE_HIGH_LVL -> R.drawable.img_score_high
        in SCORE_HIGH_LVL+1..< DEFAULT_ROUND_QUESTS -> R.drawable.img_score_top
        else -> R.drawable.img_score_max
    }

    fun getScoreDescriptionId(score: Int): Int = when (score) {
        in 0..SCORE_LOW_LVL -> R.string.desc_score_low
        in SCORE_LOW_LVL+1..SCORE_MEDIUM_LVL -> R.string.desc_score_medium
        in SCORE_MEDIUM_LVL+1..SCORE_HIGH_LVL -> R.string.desc_score_high
        in SCORE_HIGH_LVL+1..< DEFAULT_ROUND_QUESTS -> R.string.desc_score_top
        else -> R.string.desc_score_max
    }

    private fun playSound(player: MediaPlayer) {
        if (player.isPlaying)
            player.seekTo(0)
        else
            player.start()
    }
}