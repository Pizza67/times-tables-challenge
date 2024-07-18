package it.mmessore.timestableschallenge.data.persistency

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.gson.Gson
import com.google.gson.JsonSyntaxException

@Entity
data class Round(
    @PrimaryKey
    val roundId: String,
    val timestamp: Long = System.currentTimeMillis(),
    val score: Int = 0,
    val timeLeft: Int = 0
) {

    fun hasBetterOrEqualsScore(other: Round, useTimeLeft: Boolean): Boolean {
        var ret = (score >= other.score)
        if (useTimeLeft)
            ret = ret && (timeLeft >= other.timeLeft)
        return ret
    }

    fun serialize(): String {
        val gson = Gson()
        return gson.toJson(this)
    }

    companion object {
        fun deserialize(jsonString: String): Round? {
            val gson = Gson()
            return try {
                gson.fromJson(jsonString, Round::class.java)
            } catch (e: JsonSyntaxException) { null }
        }
    }
}