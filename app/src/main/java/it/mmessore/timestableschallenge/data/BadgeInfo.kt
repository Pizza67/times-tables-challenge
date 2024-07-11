package it.mmessore.timestableschallenge.data

data class BadgeInfo(
    val id: Int,
    val timestamp: Long = 0,
    val avgScore: Double = 0.0,
    val numRounds: Int = 0
) {
    fun isAchieved(): Boolean {
        return timestamp != 0L
    }
}
