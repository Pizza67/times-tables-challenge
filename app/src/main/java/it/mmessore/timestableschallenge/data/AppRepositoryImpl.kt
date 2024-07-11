package it.mmessore.timestableschallenge.data

import android.content.Context
import it.mmessore.timestableschallenge.data.persistency.Achievement
import it.mmessore.timestableschallenge.data.persistency.AchievementDao
import it.mmessore.timestableschallenge.data.persistency.AppPreferences
import it.mmessore.timestableschallenge.data.persistency.Round
import it.mmessore.timestableschallenge.data.persistency.RoundDao
import javax.inject.Inject

class AppRepositoryImpl @Inject constructor(
    private val context: Context,
    private val appPreferences: AppPreferences,
    private val roundDao: RoundDao,
    private val achievementDao: AchievementDao
): AppRepository {

    override suspend fun insertRound(round: Round) {
        if (appPreferences.overwriteBestScores)
            roundDao.insertRound(round)
        else
            roundDao.insertRoundIfBetterOrEquals(round, appPreferences.useTimeLeft)
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

    override suspend fun getBestRound(useTimeLeft: Boolean): Round? {
        return roundDao.getBestRound(useTimeLeft)
    }

    override suspend fun getWorstRound(): Round? {
        return roundDao.getWorstRound()
    }

    override suspend fun isNewBestRound(round: Round): Boolean {
        val bestRound = getBestRound(appPreferences.useTimeLeft)
        return if (bestRound != null && getRoundNum() > 1)
            round.hasBetterOrEqualsScore(bestRound, appPreferences.useTimeLeft)
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