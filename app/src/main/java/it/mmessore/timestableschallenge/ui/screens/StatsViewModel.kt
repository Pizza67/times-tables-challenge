package it.mmessore.timestableschallenge.ui.screens

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import it.mmessore.timestableschallenge.data.Badges
import it.mmessore.timestableschallenge.data.RoundRepository
import it.mmessore.timestableschallenge.data.persistency.Round
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class StatsViewModel @Inject constructor(
    private val repository: RoundRepository,
): ViewModel() {

    private val _currentRank = MutableStateFlow(Badges.list[0].nameStrId)
    val currentRank: StateFlow<Int> = _currentRank
    private val _currentRankImg = MutableStateFlow(Badges.list[0].image)
    val currentRankImg: StateFlow<Int> = _currentRankImg
    private val _numRounds = MutableStateFlow(0)
    val numRounds: StateFlow<Int> = _numRounds
    private val _totScore = MutableStateFlow(0)
    val totScore: StateFlow<Int> = _totScore
    private val _avgRounds = MutableStateFlow(0.0)
    val avgRounds: StateFlow<Double> = _avgRounds
    private val _bestRound: MutableStateFlow<Round?> = MutableStateFlow(null)
    val bestRound: StateFlow<Round?> = _bestRound
    private val _worstRound: MutableStateFlow<Round?> = MutableStateFlow(null)
    val worstRound: StateFlow<Round?> = _worstRound

    init {
        viewModelScope.launch {
            _avgRounds.value = repository.getAvgScore()
            _numRounds.value = repository.getRoundNum()
            _totScore.value = repository.getTotalScore()
            val currentRank = Badges.getBadgebyStats(_avgRounds.value, _numRounds.value)
            _currentRank.value = currentRank.nameStrId
            _currentRankImg.value = currentRank.image
            _bestRound.value = repository.getBestRound()
            _worstRound.value = repository.getWorstRound()
        }
    }
}