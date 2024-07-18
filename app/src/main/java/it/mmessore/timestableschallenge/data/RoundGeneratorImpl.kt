package it.mmessore.timestableschallenge.data

import it.mmessore.timestableschallenge.data.persistency.AppPreferences
import it.mmessore.timestableschallenge.data.persistency.ConstantsImpl
import java.util.Base64
import javax.inject.Inject

class RoundGeneratorImpl @Inject constructor (
    private val appPreferences: AppPreferences,
    private val minTable: Int = 1,
    private val maxTable: Int = if (!appPreferences.extendedMode) 9 else 12,
    private val easyOps: List<Int> = listOf(1, 10, 11),
    private val maxNumEasyQuests: Int = 3
): RoundGenerator {

    init {
        require (minTable <= maxTable)
    }

    override fun generate(n: Int): List<Quest> {
        val quests = mutableListOf<Quest>()
        var savedEasyOps = 0
        val allowSameQuests = n > ((maxTable - minTable + 1) * 8)
        while (quests.size < n) {
            val op1 = (minTable..maxTable).random()
            val maxOp2 = if (!appPreferences.extendedMode) 10 else 12
            val op2 = (1..maxOp2).random()
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

        fun isValid(base64String: String, size: Int): Boolean {
            return try {
                val quests = deserialize(base64String)
                quests.isNotEmpty() && quests.size == size
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