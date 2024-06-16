package it.mmessore.timestableschallenge.data

class Quest(
    val op1: Int,
    val op2: Int
) {
    fun answer(): Int {
        return op1 * op2
    }

    fun isEasy(easyOps: List<Int>): Boolean {
        return (op1 in easyOps || op2 in easyOps)
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
