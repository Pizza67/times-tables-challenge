package it.mmessore.timestableschallenge.data

import it.mmessore.timestableschallenge.data.persistency.Round
import it.mmessore.timestableschallenge.data.persistency.RoundDao
import kotlinx.coroutines.flow.Flow

class RoundRepository(private val roundDao: RoundDao) {
    val allRounds: Flow<List<Round>> = roundDao.getAllRounds()

    suspend fun insertRound(round: Round) {
        roundDao.insertRound(round)
    }

    suspend fun deleteAllRounds() {
        roundDao.deleteAllRounds()
    }
}