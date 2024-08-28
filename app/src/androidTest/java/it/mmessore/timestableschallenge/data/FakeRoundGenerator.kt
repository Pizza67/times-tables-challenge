package it.mmessore.timestableschallenge.data

import it.mmessore.timestableschallenge.data.persistency.AppPreferences
import javax.inject.Inject

class FakeRoundGenerator @Inject constructor(private val appPreferences: AppPreferences): RoundGenerator {

    val quests = RoundGeneratorImpl(appPreferences).generate(appPreferences.numQuestions)

    override fun generate(n: Int): List<Quest> = quests
    fun getRoundId() = RoundGeneratorImpl.serialize(quests)
    fun getNewRoundId() = RoundGeneratorImpl.serialize(RoundGeneratorImpl(appPreferences).generate())
}