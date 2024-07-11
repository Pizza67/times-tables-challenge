package it.mmessore.timestableschallenge.data.persistency

// Definisci il DAO (Data Access Object)

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction

@Dao
interface RoundDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertRound(round: Round)

    @Transaction
    suspend fun insertRoundIfBetterOrEquals(newRound: Round, useTimeLeft: Boolean) {
        val existingRound = getRound(newRound.roundId)
        if (existingRound == null || newRound.hasBetterOrEqualsScore(existingRound, useTimeLeft)) {
            insertRound(newRound)
        }
    }

    @Query("SELECT * FROM Round WHERE roundId = :id")
    suspend fun getRound(id: String): Round?

    @Query("""
        SELECT * FROM Round 
        ORDER BY 
            CASE WHEN :useTimeleft THEN timeLeft ELSE 0 END DESC, 
            score DESC, 
            timestamp ASC 
        LIMIT 1
    """)
    suspend fun getBestRound(useTimeleft: Boolean): Round?

    @Query("SELECT * FROM Round ORDER BY score ASC LIMIT 1")
    suspend fun getWorstRound(): Round?

    @Query("SELECT * FROM Round ORDER BY timestamp DESC LIMIT 1")
    suspend fun getLastRound(): Round?

    @Query("SELECT COUNT(*) FROM Round")
    suspend fun getRoundNum(): Int

    @Query("SELECT AVG(score) FROM Round")
    suspend fun getAvgScore(): Double

    @Query("SELECT SUM(score) FROM Round")
    suspend fun getTotalScore(): Int

    @Query("DELETE FROM Round")
    suspend fun deleteAllRounds()
}