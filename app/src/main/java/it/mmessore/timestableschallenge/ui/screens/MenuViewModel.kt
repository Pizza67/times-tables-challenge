package it.mmessore.timestableschallenge.ui.screens

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import it.mmessore.timestableschallenge.data.AppRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MenuViewModel @Inject constructor(
    private val repository: AppRepository,
): ViewModel() {
    private val _hasPlayedRounds: MutableStateFlow<Boolean> = MutableStateFlow(false)
    val hasPlayedRounds = _hasPlayedRounds.asStateFlow()

    fun getRoundNum() {
        viewModelScope.launch {
            _hasPlayedRounds.emit(repository.getRoundNum() > 0)
        }
    }
}