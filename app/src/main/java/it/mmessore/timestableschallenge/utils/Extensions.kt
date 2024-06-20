package it.mmessore.timestableschallenge.utils

import android.text.format.DateUtils.DAY_IN_MILLIS
import android.text.format.DateUtils.FORMAT_ABBREV_RELATIVE
import android.text.format.DateUtils.getRelativeTimeSpanString
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

fun formatTimestamp(timestamp: Long): String {
    val now = System.currentTimeMillis()
    val yesterday = now - DAY_IN_MILLIS

    return if (timestamp >= yesterday) {
        getRelativeTimeSpanString(
            timestamp,
            now,
            DAY_IN_MILLIS,
            FORMAT_ABBREV_RELATIVE
        ).toString()
    } else {
        val date = Date(timestamp)
        val dateFormat = SimpleDateFormat("d MMMM yyyy", Locale.getDefault())
        dateFormat.format(date)
    }
}