package chucknorris
import java.lang.Integer.toBinaryString

fun main() {
    while (true) {
        println("Please input operation (encode/decode/exit):")
        when(val input = readln()) {
            "encode" -> encode()
            "decode" -> decode()
            "exit" -> break
            else -> println("There is no '$input' operation")
        }
    }
    println("Bye!")
}

fun encode() {
    println("Input string:")
    val input = readln()
    var binaryStr = ""

    for (i in input) {
        binaryStr += String.format("%7s", toBinaryString(i.code)).replace(" ", "0")
    }

    var result = if (binaryStr[0] == '0') "00 0" else "0 0"
    for (i in 1 until binaryStr.length) {
        result += if (binaryStr[i] == binaryStr[i-1]) {
            "0"
        } else {
            " " + if (binaryStr[i] == '0') "00 0" else "0 0"
        }
    }

    println("Encoded string:")
    println(result)
}

fun decode() {
    println("Input encoded string:")

    val inputStr = readln()
    val input = inputStr.split(" ")
    if (input.size % 2 != 0) {
        return println("Encoded string is not valid.")
    }

    val regex = """^[0 0]+${'$'}""".toRegex()
    if (!regex.containsMatchIn(inputStr)) {
        return println("Encoded string is not valid.")
    }

    var count = 0
    for (i in 0 .. input.lastIndex step 2) {
        if (input[i].length > 2) {
            return println("Encoded string is not valid.")
        }
        count += input[i+1].length
    }
    if (count % 7 != 0) {
        return println("Encoded string is not valid.")
    }

    var binaryStr = ""
    for (i in 0 .. input.lastIndex step 2) {
        repeat(input[i + 1].length) {
            binaryStr += if (input[i] == "0") "1" else "0"
        }
    }

    var result = ""
    var symbol = ""
    for (i in 0 .. binaryStr.lastIndex) {
        symbol += binaryStr[i]
        if ((i + 1) % 7 == 0) {
            result += Integer.parseInt(symbol, 2).toChar()
            symbol = ""
        }
    }
    println("Decoded string:")
    println(result)
}

