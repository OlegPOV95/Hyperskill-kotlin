package bullscows
import kotlin.random.Random

fun main() {
    val secretCodeLength: Int
    val maxCharCount: Int
    try {
        println("Input the length of the secret code:")
        val length = readln()
        if (!Regex("^[1-9]\\d*$").matches(length)) {
            throw Exception("Error: \"$length\" isn't a valid number.")
        }
        secretCodeLength = length.toInt()

        println("Input the number of possible symbols in the code:")
        maxCharCount = readln().toInt()
        if (maxCharCount > 36) {
            throw Exception("Error: maximum number of possible symbols in the code is 36 (0-9, a-z).")
        }
        if (maxCharCount < secretCodeLength) {
            throw Exception("Error: it's not possible to generate a code with a length of $secretCodeLength with $maxCharCount unique symbols.")
        }
    } catch (e: Exception) {
        return println(e.message)
    }

    val secretCode = createSecretCode(secretCodeLength, maxCharCount)

    println("Okay, let's start a game!")
    var turnCounter = 1
    while (true) {
        println("Turn $turnCounter:")
        val isWin = check(readln(), secretCode)
        if (isWin) {
            break
        }
        turnCounter++
    }
}

fun check(input: String, secretCode: String): Boolean {
    var cow = 0
    var bull = 0
    for (i in 0 .. input.lastIndex) {
        if (input[i] == secretCode[i]) {
            bull++
            continue
        }
        if (secretCode.contains(input[i])) {
            cow++
        }
    }
    val bullEnding = if (bull != 1) "s" else ""
    val cowEnding = if (cow != 1) "s" else ""
    if (bull == secretCode.length) {
        println("Grade: $bull bull$bullEnding")
        println("Congratulations! You guessed the secret code.")
        return true
    }
    println("Grade: $bull bull$bullEnding and $cow cow$cowEnding")
    return false
}

fun createSecretCode(length: Int, maxSymbols: Int): String {
    var result = ""
    val symbols = "0123456789abcdefghijklmnopqrstuvwxyz"
    while (result.length < length) {
        val random = Random.nextInt(0, maxSymbols)
        if (!result.contains(symbols[random])) {
            result += symbols[random]
        }
    }
    var message = "The secret is prepared: "
    repeat(length){message += "*"}
    message += if (maxSymbols <= 10) {
        " (0-9)"
    } else {
        " (0-9, a-${symbols[maxSymbols - 1]})."
    }
    println(message)
    return result
}