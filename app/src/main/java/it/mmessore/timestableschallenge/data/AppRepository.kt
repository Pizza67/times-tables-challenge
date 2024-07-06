package it.mmessore.timestableschallenge.data

import android.content.Context
import it.mmessore.timestableschallenge.data.persistency.Achievement
import it.mmessore.timestableschallenge.data.persistency.AchievementDao
import it.mmessore.timestableschallenge.data.persistency.Round
import it.mmessore.timestableschallenge.data.persistency.RoundDao

class AppRepository(
    private val context: Context,
    private val roundDao: RoundDao,
    private val achievementDao: AchievementDao
) {

    suspend fun insertRound(round: Round) {
        roundDao.insertRoundIfBetterOrEquals(round)
    }

    suspend fun deleteAppData() {
        roundDao.deleteAllRounds()
        achievementDao.deleteAllAchievements()
    }

    suspend fun lastRound(): Round? {
        return roundDao.getLastRound()
    }

    suspend fun getRound(roundId: String): Round? {
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

    suspend fun isNewBestRound(round: Round): Boolean {
        val bestRound = getBestRound()
        return if (bestRound != null && getRoundNum() > 1)
            round.hasBetterOrEqualsScore(bestRound)
        else
            false
    }

    suspend fun unlockNewAchievement(achievement: Achievement) {
        achievementDao.insertAchievement(achievement)
    }

    suspend fun getAchievements(): List<Achievement> {
        return achievementDao.getAllAchievements()
    }

    suspend fun getCurrentAchievement(): Achievement? {
        val badge = Badges.getBadgebyStats(getAvgScore(), getRoundNum())
        return Achievement.createFromBadge(context, badge, getAvgScore(), getRoundNum())
    }

    suspend fun isAchievementUnlocked(id: Int): Boolean {
        return achievementDao.getAchievement(id) != null
    }
}