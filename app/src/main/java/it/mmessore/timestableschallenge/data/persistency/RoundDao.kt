package it.mmessore.timestableschallenge.data.persistency

// Definisci il DAO (Data Access Object)

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface RoundDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertRound(round: Round)

    @Query("SELECT * FROM Round WHERE roundId = :id")
    fun getRound(id: String): Flow<Round>

    @Query("SELECT * FROM Round ORDER BY timeLeft DESC, score DESC, timestamp ASC LIMIT 1")
    suspend fun getBestRound(): Round?

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