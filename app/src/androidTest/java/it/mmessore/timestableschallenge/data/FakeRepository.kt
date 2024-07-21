package it.mmessore.timestableschallenge.data

import android.content.Context
import it.mmessore.timestableschallenge.data.persistency.Achievement
import it.mmessore.timestableschallenge.data.persistency.Round
import javax.inject.Inject

class FakeRepository @Inject constructor(private val context: Context): AppRepository {
    private var round: Round? = Round(
        roundId = "testRoundId",
        timestamp = System.currentTimeMillis(),
        score = 10,
        timeLeft = 0
    )
    private var isNewBestRound: Boolean = false
    private var currentAchievement: Achievement? = null
    private var isAchievementAlreadyUnlocked: Boolean = true
    private var roundNum: Int = 1

    fun setRound(round: Round) {
        this.round = round
    }

    fun setIsNewBestRound(isNewBestRound: Boolean) {
        this.isNewBestRound = isNewBestRound
    }

    fun setCurrentAchievement(achievementId: Int = 1, avgScore: Double = 0.0, numRounds: Int = 0) {
        this.currentAchievement = Achievement.createFromBadge(
            context = context,
            badge = Badges.list[achievementId],
            avgScore = avgScore,
            numRounds = numRounds
        )
    }

    fun setNewAchievementUnlocked(isUnlocked: Boolean) {
        this.isAchievementAlreadyUnlocked = !isUnlocked
    }

    fun setRoundNum(numRounds: Int) {
        roundNum = numRounds
    }

    override suspend fun insertRound(round: Round) {

    }

    override suspend fun deleteAppData() {
        TODO("Not yet implemented")
    }

    override suspend fun lastRound(): Round? {
        return round
    }

    override suspend fun getRound(roundId: String): Round? {
        return round
    }

    override suspend fun getAvgScore(): Double {
        return 10.0
    }

    override suspend fun getTotalScore(): Int {
        return 100
    }

    override suspend fun getRoundNum() = roundNum

    override suspend fun getBestRound(useTimeLeft: Boolean): Round? {
        return round
    }

    override suspend fun getWorstRound(): Round? {
        return round
    }

    override suspend fun isNewBestRound(round: Round): Boolean {
        return isNewBestRound
    }

    override suspend fun unlockNewAchievement(achievement: Achievement) {
        return
    }

    override suspend fun getAchievements(): List<Achievement> {
        return listOf(
            Achievement.createFromBadge(
                context = context,
                badge = Badges.list[0],
                avgScore = 10.0,
                numRounds = 10
        )!!)
    }

    override suspend fun getCurrentAchievement(): Achievement? {
        return this.currentAchievement
    }

    override suspend fun isAchievementUnlocked(id: Int): Boolean {
        return this.isAchievementAlreadyUnlocked
    }
}