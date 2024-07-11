package it.mmessore.timestableschallenge.ui.screens

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import it.mmessore.timestableschallenge.data.persistency.AppPreferences
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject

@HiltViewModel
class SettingsViewModel @Inject constructor(private val appPreferences: AppPreferences) : ViewModel() {

    private val _swExtendedMode = MutableStateFlow(appPreferences.extendedMode)
    val swExtendedMode = _swExtendedMode.asStateFlow()
    private val _swOverwriteBestScores = MutableStateFlow(appPreferences.overwriteBestScores)
    val swOverwriteBestScores = _swOverwriteBestScores.asStateFlow()
    private val _swPlaySounds = MutableStateFlow(appPreferences.playSounds)
    val swPlaySounds = _swPlaySounds.asStateFlow()
    private val _swUseTimeLeft = MutableStateFlow(appPreferences.useTimeLeft)
    val swUseTimeLeft = _swUseTimeLeft.asStateFlow()

    fun toggleExtendedMode() {
        appPreferences.extendedMode = !appPreferences.extendedMode
        _swExtendedMode.value = appPreferences.extendedMode
    }

    fun toggleOverwriteBestScores() {
        appPreferences.overwriteBestScores = !appPreferences.overwriteBestScores
        _swOverwriteBestScores.value = appPreferences.overwriteBestScores
    }

    fun togglePlaySounds() {
        appPreferences.playSounds = !appPreferences.playSounds
        _swPlaySounds.value = appPreferences.playSounds
    }

    fun toggleUseTimeLeft() {
        appPreferences.useTimeLeft = !appPreferences.useTimeLeft
        _swUseTimeLeft.value = appPreferences.useTimeLeft
    }
}
