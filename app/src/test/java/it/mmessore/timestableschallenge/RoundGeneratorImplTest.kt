package it.mmessore.timestableschallenge

import it.mmessore.timestableschallenge.data.Quest
import it.mmessore.timestableschallenge.data.RoundGeneratorImpl
import it.mmessore.timestableschallenge.data.persistency.FakeAppPreferences
import junit.framework.TestCase.assertEquals
import junit.framework.TestCase.assertFalse
import org.junit.Assert.assertNotEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.jupiter.api.assertThrows

class RoundGeneratorImplTest {
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
        assertEquals(RoundGeneratorImpl(FakeAppPreferences()).generate(30).size, 30)
    }

    @Test
    fun generate_round_with_no_duplicates() {
        val questsNum = 30
        val round = RoundGeneratorImpl(FakeAppPreferences()).generate(questsNum)
        assertEquals(round.size, questsNum)
        val roundDistinct = round.distinct()
        assertEquals(round.size, roundDistinct.size)
    }
    @Test
    fun generate_round_with_duplicates_no_consecutive() {
        val questsNum = 30
        val round = RoundGeneratorImpl(minTable = 4, maxTable = 4, appPreferences = FakeAppPreferences()).generate(questsNum)
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
        val round = RoundGeneratorImpl(easyOps = easyList, maxNumEasyQuests = maxNumEasyQuests, appPreferences = FakeAppPreferences()).generate(questsNum)
        assertEquals(round.size, questsNum)
        assert(round.filter { it.isEasy(easyList) }.size <= maxNumEasyQuests)
    }

    @Test
    fun generate_round_with_empty_easy_ops() {
        val round = RoundGeneratorImpl(
            easyOps = emptyList(),
            appPreferences = FakeAppPreferences()
        ).generate(50)
        assertEquals(50, round.size)
        round.forEach { quest ->
            assertFalse(quest.isEasy(emptyList()))
        }
    }
    @Test
    fun generate_round_with_max_easy_quests() {
        val easyOps = listOf(1, 2, 5)
        val maxNumEasyQuests = 5
        val round = RoundGeneratorImpl(
            easyOps = easyOps,
            maxNumEasyQuests = maxNumEasyQuests,
            appPreferences = FakeAppPreferences()
        ).generate()
        val numEasyQuests = round.count { it.isEasy(easyOps) }
        assertTrue(numEasyQuests <= maxNumEasyQuests)
    }

    @Test
    fun generate_invalid_round() {
        assertThrows<IllegalArgumentException> {
            RoundGeneratorImpl(minTable = 8, maxTable = 3, appPreferences = FakeAppPreferences()).generate()
        }
    }

    @Test
    fun serialize_deserialize_quests() {
        val questsNum = 200
        val quests = RoundGeneratorImpl(FakeAppPreferences(extendedMode = true, numQuestions = questsNum)).generate(questsNum)
        val encodedQuests = RoundGeneratorImpl.serialize(quests)
        val decodedQuests = RoundGeneratorImpl.deserialize(encodedQuests)
        assertEquals(quests, decodedQuests)
    }

    @Test
    fun serialize_and_deserialize_empty_round() {
        val emptyRound = emptyList<Quest>()
        val encoded = RoundGeneratorImpl.serialize(emptyRound)
        val decoded = RoundGeneratorImpl.deserialize(encoded)
        assertEquals(emptyRound, decoded)
    }

    @Test
    fun isValid_valid_base64_string() {
        val quests = RoundGeneratorImpl(FakeAppPreferences()).generate()
        val encoded = RoundGeneratorImpl.serialize(quests)
        assertTrue(RoundGeneratorImpl.isValid(encoded, quests.size))
    }

    @Test
    fun isValid_invalid_base64_string() {
        val invalidString = "abcde"
        assertFalse(RoundGeneratorImpl.isValid(invalidString, 10))
    }
}