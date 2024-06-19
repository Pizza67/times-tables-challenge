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

    @Query("SELECT * FROM Round ORDER BY timestamp DESC")
    fun getAllRounds(): Flow<List<Round>>

    @Query("SELECT * FROM Round WHERE roundId = :id")
    fun getRound(id: String): Flow<Round>

    @Query("SELECT * FROM Round ORDER BY score ASC LIMIT 1")
    fun getWorstRound(): Flow<Round>

    @Query("SELECT * FROM Round ORDER BY timestamp DESC LIMIT 1")
    suspend fun getLastRound(): Round?

    @Query("SELECT COUNT(*) FROM Round")
    fun getRoundNum(): Flow<Int>

    @Query("SELECT AVG(score) FROM Round")
    fun getAvgScore(): Flow<Double>

    @Query("DELETE FROM Round")
    suspend fun deleteAllRounds()
}