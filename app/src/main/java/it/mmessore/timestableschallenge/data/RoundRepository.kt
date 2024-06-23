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

    suspend fun lastRound(): Round? {
        return roundDao.getLastRound()
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

    suspend fun getAvgScore(): Double {
        return roundDao.getAvgScore()
    }

    suspend fun getTotalScore(): Int {
        return roundDao.getTotalScore()
    }

    suspend fun getRoundNum(): Int {
        return roundDao.getRoundNum()
    }

    suspend fun getBestRound(): Round? {
        return roundDao.getBestRound()
    }

    suspend fun getWorstRound(): Round? {
        return roundDao.getWorstRound()
    }

}