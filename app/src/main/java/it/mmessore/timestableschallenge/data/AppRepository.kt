package it.mmessore.timestableschallenge.data

import android.content.Context
import it.mmessore.timestableschallenge.data.persistency.Achievement
import it.mmessore.timestableschallenge.data.persistency.AchievementDao
import it.mmessore.timestableschallenge.data.persistency.Round
import it.mmessore.timestableschallenge.data.persistency.RoundDao
import kotlinx.coroutines.flow.Flow

class AppRepository(
    private val context: Context,
    private val roundDao: RoundDao,
    private val achievementDao: AchievementDao
) {

    suspend fun insertRound(round: Round) {
        roundDao.insertRound(round)
    }

    suspend fun deleteAppData() {
        roundDao.deleteAllRounds()
        achievementDao.deleteAllAchievements()
    }

    suspend fun lastRound(): Round? {
        return roundDao.getLastRound()
    }

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

    suspend fun insertAchievement(achievement: Achievement) {
        achievementDao.insertAchievement(achievement)
    }

    suspend fun getAchievements(): List<Achievement> {
        return achievementDao.getAllAchievements()
    }

    suspend fun getCurrentAchievement(): Achievement? {
        val badge = Badges.getBadgebyStats(getAvgScore(), getRoundNum())
        return Achievement.createFromBadge(context, badge, getAvgScore(), getRoundNum())
    }

    suspend fun unlockNewAchievement(achievement: Achievement) {
        achievementDao.insertAchievement(achievement)
    }

    suspend fun isAchievementUnlocked(id: Int): Boolean {
        return achievementDao.getAchievement(id) != null
    }
}