package it.mmessore.timestableschallenge.data

import it.mmessore.timestableschallenge.data.persistency.Achievement
import it.mmessore.timestableschallenge.data.persistency.Round

interface AppRepository {

    suspend fun insertRound(round: Round)

    suspend fun deleteAppData()

    suspend fun lastRound(): Round?suspend fun getRound(roundId: String): Round?

    suspend fun getAvgScore(): Double

    suspend fun getTotalScore(): Int

    suspend fun getRoundNum(): Int

    suspend fun getBestRound(useTimeleft: Boolean): Round?

    suspend fun getWorstRound(): Round?

    suspend fun isNewBestRound(round: Round): Boolean

    suspend fun unlockNewAchievement(achievement: Achievement)

    suspend fun getAchievements(): List<Achievement>

    suspend fun getCurrentAchievement(): Achievement?

    suspend fun isAchievementUnlocked(id: Int): Boolean
}