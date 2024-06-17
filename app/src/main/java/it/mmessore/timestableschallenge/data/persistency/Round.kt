package it.mmessore.timestableschallenge.data.persistency

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class Round(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val timestamp: Long,
    val roundData: String,
    val score: Int
)