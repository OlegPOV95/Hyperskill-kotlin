package encryptdecrypt

import java.io.File
import java.lang.Exception


fun unicode(input: String, operation: String, key: Int): String {
    var output = ""
    when (operation) {
        "enc" -> input.forEach {output += it + key }
        "dec" -> input.forEach {output += it - key }
    }
    return output
}

fun shift(input: String, operation: String, key: Int): String {
    val letters = if (operation == "dec") {
        ('z' downTo 'a').toList()
    } else {
        ('a'..'z').toList()
    }
    var output = ""

    for (i in input) {
        if (!i.isLetter()) {
            output += i
            continue
        }
        val temp = letters[(letters.indexOf(i.lowercaseChar()) + key) % letters.size]
        if (i.isUpperCase()) {
            output += temp.uppercaseChar()
        } else {
            output += temp
        }
    }

    return output
}


fun main(args: Array<String>) {
    var operation = "enc"
    var key = 0
    var data = ""
    var inputFile = ""
    var outputFile = ""
    var algorithm = "shift"

    for (i in args.indices) {
        if (i + 1 < args.size) {
            if (args[i] == "-mode") {
                operation = args[i + 1]
            }
            if (args[i] == "-key") {
                key = args[i + 1].toInt()
            }
            if (args[i] == "-data") {
                data = args[i + 1]
            }
            if (args[i] == "-in") {
                inputFile = args[i + 1]
            }
            if (args[i] == "-out") {
                outputFile = args[i + 1]
            }
            if (args[i] == "-alg") {
                algorithm = args[i + 1]
            }
        }
    }


    if (inputFile.isNotEmpty()) {
        try {
            data = File(inputFile).readText()
        } catch(e: Exception) {
            println("Error")
            return
        }
    }

    val result = if (algorithm == "unicode") unicode(data, operation, key) else shift(data, operation, key)

    if (outputFile.isNotEmpty()) {
        try {
            File(outputFile).writeText(result)
        } catch (e: Exception) {
            println("Error")
            return
        }
    } else {
        println(result)
    }
}



