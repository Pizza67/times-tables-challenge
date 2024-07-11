package it.mmessore.timestableschallenge.data.persistency

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(entities = [Round::class, Achievement::class], version = 2)
abstract class AppDatabase : RoomDatabase() {
    abstract fun roundDao(): RoundDao
    abstract fun achievementDao(): AchievementDao
}