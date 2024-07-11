package it.mmessore.timestableschallenge.ui.screens

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import it.mmessore.timestableschallenge.R
import it.mmessore.timestableschallenge.data.AppRepository
import it.mmessore.timestableschallenge.data.Badges
import it.mmessore.timestableschallenge.data.Levels
import it.mmessore.timestableschallenge.data.RoundInfo
import it.mmessore.timestableschallenge.data.SummaryDialogInfo
import it.mmessore.timestableschallenge.data.persistency.AppPreferences
import it.mmessore.timestableschallenge.data.persistency.Round
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SummaryViewModel @Inject constructor(
    private val repository: AppRepository,
    private val appPreferences: AppPreferences,
    private val coroutineScope: CoroutineScope
): ViewModel() {
    private val _roundInfo: MutableStateFlow<RoundInfo> = MutableStateFlow(RoundInfo())
    val roundInfo: StateFlow<RoundInfo> = _roundInfo
    private val _rewardDialogInfo: MutableStateFlow<SummaryDialogInfo?> = MutableStateFlow(null)
    val rewardDialogInfo: StateFlow<SummaryDialogInfo?> = _rewardDialogInfo
    private val _bestScoreDialogInfo: MutableStateFlow<SummaryDialogInfo?> = MutableStateFlow(null)
    val bestScoreDialogInfo: StateFlow<SummaryDialogInfo?> = _bestScoreDialogInfo

    fun fetchRoundInfo(round: Round?) {
        round ?: return
        viewModelScope.launch(context = coroutineScope.coroutineContext) {
            _roundInfo.emit(RoundInfo(round.score, round.timeLeft, Levels.getLevelByScore(round.score)))
            if (repository.isNewBestRound(round)) {
                _bestScoreDialogInfo.emit(
                    SummaryDialogInfo(
                        title = R.string.new_best_round,
                        message = R.string.new_best_round_message,
                        image = R.drawable.img_best_round,
                        contentDescription = R.string.new_best_round
                    )
                )
            }
        }
    }

    fun useTimeleft() = appPreferences.useTimeLeft

    fun checkRewards() {
        viewModelScope.launch(context = coroutineScope.coroutineContext) {
            repository.getCurrentAchievement()?.let { achievement ->
                if (!repository.isAchievementUnlocked(achievement.id)) {
                    repository.unlockNewAchievement(achievement)
                    _rewardDialogInfo.emit(
                        SummaryDialogInfo(
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