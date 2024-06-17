package it.mmessore.timestableschallenge.data.persistency

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(entities = [Round::class], version = 1)
abstract class RoundDatabase : RoomDatabase() {
    abstract fun roundDao(): RoundDao
}