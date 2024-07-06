package it.mmessore.timestableschallenge.data

import android.content.Context
import it.mmessore.timestableschallenge.data.persistency.Achievement
import it.mmessore.timestableschallenge.data.persistency.AchievementDao
import it.mmessore.timestableschallenge.data.persistency.Round
import it.mmessore.timestableschallenge.data.persistency.RoundDao

class AppRepositoryImpl(
    private val context: Context,
    private val roundDao: RoundDao,
    private val achievementDao: AchievementDao
): AppRepository {

    override suspend fun insertRound(round: Round) {
        roundDao.insertRoundIfBetterOrEquals(round)
    }

    override suspend fun deleteAppData() {
        roundDao.deleteAllRounds()
        achievementDao.deleteAllAchievements()
    }

    override suspend fun lastRound(): Round? {
        return roundDao.getLastRound()
    }

    override suspend fun getRound(roundId: String): Round? {
        return roundDao.getRound(roundId)
    }

    override suspend fun getAvgScore(): Double {
        return roundDao.getAvgScore()
    }

    override suspend fun getTotalScore(): Int {
        return roundDao.getTotalScore()
    }

    override suspend fun getRoundNum(): Int {
        return roundDao.getRoundNum()
    }

    override suspend fun getBestRound(): Round? {
        return roundDao.getBestRound()
    }

    override suspend fun getWorstRound(): Round? {
        return roundDao.getWorstRound()
    }

    override suspend fun isNewBestRound(round: Round): Boolean {
        val bestRound = getBestRound()
        return if (bestRound != null && getRoundNum() > 1)
            round.hasBetterOrEqualsScore(bestRound)
        else
            false
    }

    override suspend fun unlockNewAchievement(achievement: Achievement) {
        achievementDao.insertAchievement(achievement)
    }

    override suspend fun getAchievements(): List<Achievement> {
        return achievementDao.getAllAchievements()
    }

    override suspend fun getCurrentAchievement(): Achievement? {
        val badge = Badges.getBadgebyStats(getAvgScore(), getRoundNum())
        return Achievement.createFromBadge(context, badge, getAvgScore(), getRoundNum())
    }

    override suspend fun isAchievementUnlocked(id: Int): Boolean {
        return achievementDao.getAchievement(id) != null
    }
}