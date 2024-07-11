package it.mmessore.timestableschallenge.data

data class RoundInfo(
    val score: Int = 0,
    val timeLeft: Int = 0,
    val level: Level = Levels.list.first()
)
