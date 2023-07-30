package calculator

import java.math.BigInteger

class Calculator {
    private val operations: Map<String, Int> = mapOf(
        "(" to 0,
        ")" to 0,
        "+" to 1,
        "-" to 1,
        "*" to 2,
        "/" to 2,
        "^" to 3,
        "~" to 4,
    )
    private val variables = mutableMapOf<String, String>()

    fun calculate(input: String): String {
        if (input.isBlank()) {
            return ""
        }
        if (input.contains('=')) {
            addVariables(input)
            return ""
        }

        val expression = processInput(input)
        val symbolStack = mutableListOf<String>()
        val numberStack = mutableListOf<BigInteger>()
        var isNegative = false
        for (i in expression) {
            if (i !in operations) {
                if (isNegative) {
                    numberStack.add(i.toBigInteger().negate())
                    isNegative = false
                } else {
                    numberStack.add(i.toBigInteger())
                }
                continue
            }
            if (i == "~") {
                isNegative = true
                continue
            }
            if (i == "(" || symbolStack.isEmpty()) {
                symbolStack.add(i)
                continue
            }


            for (k in symbolStack.lastIndex downTo 0) {
                if (symbolStack[k] == "(" && i == ")") {
                    symbolStack.removeAt(k)
                    break
                }
                if (operations[symbolStack[k]]!! < operations[i]!!) {
                    break
                }
                if (numberStack.size > 1) {
                    val a = numberStack[numberStack.lastIndex - 1]
                    val b = numberStack[numberStack.lastIndex]
                    numberStack[numberStack.lastIndex - 1] = mathOperation(a, b, symbolStack.removeAt(k))
                    numberStack.removeAt(numberStack.lastIndex)
                }
            }
            if (i != ")") {
                symbolStack.add(i)
            }
        }
        if (numberStack.size > 1) {
            for (i in symbolStack.lastIndex downTo 0) {
                val a = numberStack[numberStack.lastIndex - 1]
                val b = numberStack[numberStack.lastIndex]
                numberStack[numberStack.lastIndex - 1] = mathOperation(a, b, symbolStack.removeAt(i))
                numberStack.removeAt(numberStack.lastIndex)
            }
        }

        return numberStack.joinToString()
    }

    private fun mathOperation(a: BigInteger, b: BigInteger, operation: String): BigInteger {
        return when (operation) {
            "+" -> a.plus(b)
            "-" -> a.subtract(b)
            "/" -> a.divide(b)
            "*" -> a.multiply(b)
            "^" -> a.pow(b.toInt())
            else -> throw Exception("Invalid operation")
        }
    }

    private fun processInput(input: String): List<String> {
        val lostOperand = Regex("""[-+*/^]$""").matches(input)
        val lostOperationSymbol = Regex("""[a-zA-Z\d]\s+[a-zA-Z\d]""").matches(input)
        val lostBracket = input.count { it == '(' } != input.count { it == ')' }
        val symbolDuplicate = input.contains(Regex("""\*{2,}|/{2,}"""))
        if (lostOperand ||lostOperationSymbol || lostBracket || symbolDuplicate) {
            throw Exception("Invalid expression")
        }

        return insertVariableValues(input)
            .replace(Regex("""\s+"""), "")
            .replace("--", "+")
            .replace(Regex("""\+{2,}"""), "+")
            .replace(Regex("""^-"""), "~")
            .replace("+-", "+~")
            .expressionToList()
    }

    private fun addVariables(input: String) {
        val expression = input.replace(Regex("\\s+"), "")
        if (!expression.contains(Regex("""^[a-zA-Z]+="""))) {
            throw Exception("Invalid identifier")
        }
        if (!expression.contains(Regex("""^([a-zA-Z])+=-?(\d+|[a-zA-Z]+)$"""))) {
            throw Exception("Invalid assignment")
        }
        val (key, value) = expression.split("=")
        if (Regex("""[a-zA-Z]+""").matches(value)) {
            variables[key] = variables[value] ?: throw Exception("Unknown variable")
        } else {
            variables[key] = value
        }
    }

    private fun insertVariableValues(input: String): String {
        var output = ""
        input.split(Regex("""\b""")).forEach {
            output += if (it.contains(Regex("""[a-zA-Z]"""))) {
                variables[it] ?: throw Exception("Unknown variable")
            } else {
                it
            }
        }
        return output
    }

    private fun String.expressionToList(): List<String> {
        var separatedSymbol = ""
        this.forEach {
            separatedSymbol += if (it.toString() in operations)  " $it " else it
        }
        return buildList {
            separatedSymbol.split(" ").forEach {
                if (it.isNotBlank()) add(it.trim())
            }
        }
    }
}