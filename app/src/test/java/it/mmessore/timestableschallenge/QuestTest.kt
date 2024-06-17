package it.mmessore.timestableschallenge

import it.mmessore.timestableschallenge.data.Quest
import org.junit.Test

class QuestTest {
    @Test
    fun quests_are_equals(){
        val q1 = Quest(4, 5)
        val q2 = Quest(5, 4)
        assert(q1 == q2)
    }

    @Test
    fun quests_are_not_equals(){
        val q1 = Quest(6, 5)
        val q2 = Quest(5, 4)
        assert(q1 != q2)
    }

    @Test
    fun serialize_deserialize_hex() {
        val q1 = Quest(4, 5)
        val q1Hex = q1.toHex()
        assert(q1Hex == "45")
        val q2 = Quest(4, 10)
        val q2Hex = q2.toHex()
        assert(q2Hex == "4a")
        assert(Quest.fromHex(q1Hex) == q1)
        assert(Quest.fromHex(q2Hex) == q2)
    }
}