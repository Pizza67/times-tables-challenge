package it.mmessore.timestableschallenge.data.persistency

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class Round(
    @PrimaryKey
    val roundId: String,
    val timestamp: Long,
    val score: Int,
    val timeLeft: Int
) {
    fun hasBetterOrEqualsScore(other: Round): Boolean {
        return (score >= other.score) && (timeLeft >= other.timeLeft)
    }
}