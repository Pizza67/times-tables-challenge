package it.mmessore.timestableschallenge.utils

import android.content.Context
import android.content.pm.PackageManager
import android.text.format.DateUtils.DAY_IN_MILLIS
import android.text.format.DateUtils.FORMAT_ABBREV_RELATIVE
import android.text.format.DateUtils.getRelativeTimeSpanString
import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

fun formatTimestamp(timestamp: Long, showTime: Boolean = false): String {
    val now = System.currentTimeMillis()
    val yesterday = now - DAY_IN_MILLIS

    val ret = if (timestamp >= yesterday) {
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

    return if (showTime) {
        "$ret, ${formatTimestampToTime(timestamp)}"
    } else {
        ret
    }
}

fun formatTimestampToTime(timestamp: Long): String {
    val timeFormat = SimpleDateFormat("HH:mm", Locale.getDefault())
    return timeFormat.format(Date(timestamp))
}

fun formatNumber(number: Double, maximumFractionDigits: Int = 1, locale: Locale = Locale.getDefault()): String {
    val formatter = NumberFormat.getNumberInstance(locale)
    formatter.maximumFractionDigits = maximumFractionDigits
    return formatter.format(number)
}

fun getAppVersion(context: Context): String {
    try {
        val packageInfo = context.packageManager.getPackageInfo(context.packageName, 0)
        return packageInfo.versionName
    } catch (e: PackageManager.NameNotFoundException) {
        return "Unknown"
    }
}

fun getAppVersionCode(context: Context): String {
    try {
        val packageInfo = context.packageManager.getPackageInfo(context.packageName, 0)
        return packageInfo.versionCode.toString()
    } catch (e: PackageManager.NameNotFoundException) {
        return ""
    }
}