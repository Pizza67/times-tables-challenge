package it.mmessore.timestableschallenge.data

import it.mmessore.timestableschallenge.data.persistency.Constants
import java.util.Base64

class RoundGenerator (
    val minTable: Int = 1,
    val maxTable: Int = 9,
    val easyOps: List<Int> = listOf(1, 10),
    val maxNumEasyQuests: Int = 3
) {

    init {
        require (minTable <= maxTable)
    }

    fun generate(n: Int = Constants.ROUND_QUESTS): List<Quest> {
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

    companion object {
        fun serialize(quests: List<Quest>): String {
            val sb = StringBuilder()
            quests.forEach {
                sb.append(it.toHex())
            }
            return hexToBase64(sb.toString())
        }

        fun deserialize(base64String: String): List<Quest> {
            val quests = mutableListOf<Quest>()
            val hexString = base64ToHex(base64String)
            for (i in hexString.indices step 2) {
                quests.add(Quest.fromHex(hexString.substring(i, i + 2)))
            }
            return quests
        }

        fun isValid(base64String: String): Boolean {
            return try {
                val quests = deserialize(base64String)
                quests.isNotEmpty() && quests.size == Constants.ROUND_QUESTS
            } catch (e: Exception) {
                false
            }
        }

        private fun hexToBase64(hexString: String): String {
            val byteArray = hexStringToByteArray(hexString)
            return Base64.getEncoder().encodeToString(byteArray)
        }

        private fun hexStringToByteArray(hexString: String): ByteArray {
            val len = hexString.length
            val data = ByteArray(len / 2)
            var i = 0
            while (i < len) {
                data[i / 2] = ((Character.digit(hexString[i], 16) shl 4)
                        + Character.digit(hexString[i + 1], 16)).toByte()
                i += 2
            }
            return data
        }

        private fun base64ToHex(base64String: String): String {
            val byteArray = Base64.getDecoder().decode(base64String)
            return byteArrayToHexString(byteArray)
        }

        private fun byteArrayToHexString(byteArray: ByteArray): String {
            val hexChars = CharArray(byteArray.size * 2)
            for (i in byteArray.indices) {
                val v = byteArray[i].toInt() and 0xFF
                hexChars[i * 2] = "0123456789abcdef"[v ushr 4]
                hexChars[i * 2 + 1] = "0123456789abcdef"[v and 0x0F]
            }
            return String(hexChars)
        }
    }
}