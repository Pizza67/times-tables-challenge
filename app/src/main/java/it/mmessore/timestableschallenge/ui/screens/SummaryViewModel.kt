package it.mmessore.timestableschallenge.ui.screens

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import it.mmessore.timestableschallenge.R
import it.mmessore.timestableschallenge.data.Levels
import it.mmessore.timestableschallenge.data.RoundInfo
import it.mmessore.timestableschallenge.data.AppRepository
import it.mmessore.timestableschallenge.data.Badges
import it.mmessore.timestableschallenge.data.RewardDialogInfo
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SummaryViewModel @Inject constructor(
    private val repository: AppRepository,
    private val coroutineScope: CoroutineScope
): ViewModel() {
    private val _roundInfo: MutableStateFlow<RoundInfo> = MutableStateFlow(RoundInfo(0, Levels.list.first()))
    val roundInfo: StateFlow<RoundInfo> = _roundInfo
    private val _rewardDialogInfo: MutableStateFlow<RewardDialogInfo?> = MutableStateFlow(null)
    val rewardDialogInfo: StateFlow<RewardDialogInfo?> = _rewardDialogInfo

    fun fetchRoundInfo(roundId: String) {
        viewModelScope.launch(context = coroutineScope.coroutineContext) {
            repository.getRound(roundId).collect { round ->
                _roundInfo.emit(RoundInfo(round.score, Levels.getLevelByScore(round.score)))
            }
        }
    }

    fun checkRewards() {
        viewModelScope.launch(context = coroutineScope.coroutineContext) {
            repository.getCurrentAchievement()?.let { achievement ->
                if (!repository.isAchievementUnlocked(achievement.id)) {
                    repository.insertAchievement(achievement)
                    _rewardDialogInfo.emit(
                        RewardDialogInfo(
                            title = R.string.new_achievement,
                            message = Badges.list[achievement.id].description,
                            image = Badges.list[achievement.id].image,
                            contentDescription = Badges.list[achievement.id].nameStrId
                        )
                    )
                }
            }

        }
    }
}