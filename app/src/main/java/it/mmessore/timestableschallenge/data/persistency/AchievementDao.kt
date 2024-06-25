package it.mmessore.timestableschallenge.data.persistency

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface AchievementDao {
    @Insert(onConflict = OnConflictStrategy.ABORT)
    suspend fun insertAchievement(achievement: Achievement)

    @Query("SELECT * FROM Achievement")
    suspend fun getAllAchievements(): List<Achievement>

    @Query("SELECT * FROM Achievement WHERE id = :id")
    suspend fun getAchievement(id: Int): Achievement?

    @Query("DELETE FROM Achievement")
    suspend fun deleteAllAchievements()

}