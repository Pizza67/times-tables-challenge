package it.mmessore.timestableschallenge.ui.screens

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import it.mmessore.timestableschallenge.data.Levels
import it.mmessore.timestableschallenge.data.RoundInfo
import it.mmessore.timestableschallenge.data.RoundRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SummaryViewModel @Inject constructor(
    private val repository: RoundRepository,
): ViewModel() {
    private val _roundInfo: MutableStateFlow<RoundInfo> = MutableStateFlow(RoundInfo(0, Levels.list.first()))
    val roundInfo: StateFlow<RoundInfo> = _roundInfo

    fun fetchRoundInfo(roundId: String) {
        viewModelScope.launch {
            repository.getRound(roundId).collect { round ->
                _roundInfo.emit(RoundInfo(round.score, Levels.getLevelByScore(round.score)))
            }
        }
    }
}