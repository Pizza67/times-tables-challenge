package it.mmessore.timestableschallenge.data.persistency

import android.content.Context
import androidx.room.Entity
import androidx.room.PrimaryKey
import it.mmessore.timestableschallenge.data.Badge
import it.mmessore.timestableschallenge.data.Badges

@Entity
data class Achievement(
    @PrimaryKey
    val id: Int,
    val name: String,
    val avgScore: Double,
    val numRounds: Int,
    val timestamp: Long
) {
    companion object {
        fun createFromBadge(context: Context, badge: Badge, avgScore: Double, numRounds: Int, timestamp: Long = System.currentTimeMillis()): Achievement? {
            val idx = Badges.list.indexOf(badge)
            return if (idx >= 0) {
                Achievement(
                    id = idx,
                    name = context.getString(badge.nameStrId),
                    avgScore = avgScore,
                    numRounds = numRounds,
                    timestamp = timestamp
                )
            } else null
        }
    }
}
