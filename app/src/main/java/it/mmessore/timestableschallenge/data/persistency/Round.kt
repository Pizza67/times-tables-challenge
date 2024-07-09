package it.mmessore.timestableschallenge.data.persistency

import android.os.Parcel
import android.os.Parcelable
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class Round(
    @PrimaryKey
    val roundId: String,
    val timestamp: Long,
    val score: Int,
    val timeLeft: Int
) : Parcelable {

    fun hasBetterOrEqualsScore(other: Round, useTimeleft: Boolean): Boolean {
        var ret = (score >= other.score)
        if (useTimeleft)
            ret = ret && (timeLeft >= other.timeLeft)
        return ret
    }

    constructor(parcel: Parcel) : this(
        parcel.readString() ?: "",
        parcel.readLong(),
        parcel.readInt(),
        parcel.readInt()
    )

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(roundId)
        parcel.writeLong(timestamp)
        parcel.writeInt(score)
        parcel.writeInt(timeLeft)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<Round>{
        override fun createFromParcel(parcel: Parcel): Round {
            return Round(parcel)
        }

        override fun newArray(size: Int): Array<Round?> {
            return arrayOfNulls(size)
        }
    }
}