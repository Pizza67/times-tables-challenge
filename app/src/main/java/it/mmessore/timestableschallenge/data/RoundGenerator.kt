package it.mmessore.timestableschallenge.data

class RoundGenerator (
    val minTable: Int = 1,
    val maxTable: Int = 9,
    val easyOps: List<Int> = listOf(1, 10),
    val maxNumEasyQuests: Int = 3
) {

    init {
        require (minTable <= maxTable)
    }
    fun generate(n: Int): List<Quest> {
        val quests = mutableListOf<Quest>()
        var savedEasyOps = 0
        val allowSameQuests = n > ((maxTable - minTable + 1) * 8)
        while (quests.size < n) {
            val op1 = (minTable..maxTable).random()
            val op2 = (1..10).random()
            val quest = Quest(op1, op2)
            if (quests.isEmpty() ||
                (allowSameQuests && quests.last() != quest) || // if allow same quests check they're not consecutive
                !quests.contains(quest)) { // if not allow same quests check they're not already in the list
                // check if we've reach the max num of easy quests
                if (quest.isEasy(easyOps) && savedEasyOps < maxNumEasyQuests) {
                    savedEasyOps++
                    quests.add(Quest(op1, op2))
                } else if (!quest.isEasy(easyOps)) {
                    quests.add(Quest(op1, op2))
                }
            }
        }
        return quests
    }

}