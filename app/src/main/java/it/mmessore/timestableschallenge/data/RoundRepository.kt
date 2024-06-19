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

    suspend fun lastRoundQuests(): List<Quest> {
        val lastRound = roundDao.getLastRound()
        return if (lastRound != null)
            RoundGenerator.deserialize(lastRound.roundId)
        else
            RoundGenerator().generate()    }

    fun getRound(roundId: String): Flow<Round> {
        return roundDao.getRound(roundId)
    }
}