package it.mmessore.timestableschallenge

import it.mmessore.timestableschallenge.data.Quest
import it.mmessore.timestableschallenge.data.RoundGenerator
import junit.framework.TestCase.assertEquals
import junit.framework.TestCase.assertFalse
import org.junit.Assert.assertNotEquals
import org.junit.Test
import org.junit.jupiter.api.assertThrows

class RoundGeneratorTest {
    private fun hasConsecutiveElements(list: List<Quest>): Boolean {
        for (i in 0 until list.size - 1) {
            if (list[i] == list[i + 1]) {
                return true
            }
        }
        return false
    }
    @Test
    fun generate_round_with_proper_size() {
        assertEquals(RoundGenerator().generate(30).size, 30)
    }
    @Test
    fun generate_round_with_no_duplicates() {
        val questsNum = 30
        val round = RoundGenerator(minTable = 1, maxTable = 9).generate(questsNum)
        assertEquals(round.size, questsNum)
        val roundDistinct = round.distinct()
        assertEquals(round.size, roundDistinct.size)
    }
    @Test
    fun generate_round_with_duplicates_no_consecutive() {
        val questsNum = 30
        val round = RoundGenerator(minTable = 4, maxTable = 4).generate(questsNum)
        assertEquals(round.size, questsNum)
        val roundDistinct = round.distinct()
        assertNotEquals(round.size, roundDistinct.size)
        assertFalse(hasConsecutiveElements(round))
    }
    @Test
    fun test_round_do_not_exceeds_easy_quests() {
        val questsNum = 100
        val easyList = listOf(1, 10)
        val maxNumEasyQuests = 4
        val round = RoundGenerator(easyOps = easyList, maxNumEasyQuests = maxNumEasyQuests).generate(questsNum)
        assertEquals(round.size, questsNum)
        assert(round.filter { it.isEasy(easyList) }.size <= maxNumEasyQuests)
    }
    @Test
    fun generate_invalid_round() {
        assertThrows<IllegalArgumentException> {
            RoundGenerator(minTable = 8, maxTable = 3)
        }
    }
    @Test
    fun encode_decode_quests() {
        val questsNum = 20
        val quests = RoundGenerator(minTable = 1, maxTable = 9).generate(questsNum)
        val encodedQuests = RoundGenerator.serialize(quests)
        val decodedQuests = RoundGenerator.deserialize(encodedQuests)
        assertEquals(quests, decodedQuests)
    }
}