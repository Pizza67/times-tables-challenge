package it.mmessore.timestableschallenge.data

class Quest(
    val op1: Int,
    val op2: Int
) {
    companion object {
        fun fromHex(hex: String): Quest {
            return Quest(hex.substring(0, 1).toInt(16), hex.substring(1, 2).toInt(16))
        }
    }
    fun answer(): Int {
        return op1 * op2
    }

    fun isEasy(easyOps: List<Int>): Boolean {
        return (op1 in easyOps || op2 in easyOps)
    }

    fun answerLength() = answer().toString().length

    fun toHex(): String {
        return op1.toString(16) + op2.toString(16)
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is Quest) return false
        if (op1 == other.op1 && op2 == other.op2) return true
        if (op1 == other.op2 && op2 == other.op1) return true

        return false
    }

    override fun hashCode(): Int {
        return (op1 * op2) + (op1 + op2)
    }
}
