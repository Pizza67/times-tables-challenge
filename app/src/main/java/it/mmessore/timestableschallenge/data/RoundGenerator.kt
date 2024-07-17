package it.mmessore.timestableschallenge.data

import it.mmessore.timestableschallenge.data.persistency.ConstantsImpl

interface RoundGenerator {
    fun generate(n: Int = ConstantsImpl.ROUND_QUESTS): List<Quest>
}