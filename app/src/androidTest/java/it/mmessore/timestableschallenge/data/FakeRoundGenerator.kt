package it.mmessore.timestableschallenge.data

class FakeRoundGenerator(val quests: List<Quest>): RoundGenerator {

    override fun generate(n: Int): List<Quest> = quests
}