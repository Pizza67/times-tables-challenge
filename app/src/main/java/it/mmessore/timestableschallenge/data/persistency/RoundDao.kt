package it.mmessore.timestableschallenge.data.persistency

// Definisci il DAO (Data Access Object)

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface RoundDao {
    @Insert
    suspend fun insertRound(round: Round)

    @Query("SELECT * FROM Round ORDER BY timestamp DESC")
    fun getAllRounds(): Flow<List<Round>>

    @Query("DELETE FROM Round")
    suspend fun deleteAllRounds()
}