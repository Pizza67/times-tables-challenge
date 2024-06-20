package it.mmessore.timestableschallenge.ui.screens

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import it.mmessore.timestableschallenge.data.RoundRepository
import javax.inject.Inject

@HiltViewModel
class StatsViewModel @Inject constructor(
    private val repository: RoundRepository,
): ViewModel() {

}