package sorting

import java.io.File
import java.lang.Exception
import java.util.Scanner

fun main(args: Array<String>) {
    val command: Map<String, String>
    try {
        command = getCommand(args)
    } catch (e: Exception) {
        return println(e.message)
    }
    val input: Array<String> = getInput(command["dataType"]!!)
    val result = sort(input, command["dataType"]!!, command["sortingType"]!!)

    if (command["outputFile"]!!.isEmpty()) {
        println(result)
    } else {
        try {
            val outputFile = File(command["outputFile"]!!)
            outputFile.writeText(result)
        } catch (e: Exception) {
            println(e.message)
        }
    }
}

fun sort(data: Array<String>, dataType: String, sortingType: String): String {
    var sortedArray: Array<String> = arrayOf()
    if (dataType == "long") {
        var longArray: Array<Long> = arrayOf()
        for (i in data) {
            if (i.toLongOrNull() != null) {
                longArray += i.toLong()
            } else {
                println("\"$i\" is not a long. It will be skipped.")
            }
        }
        longArray.sort()
        longArray.forEach {
            sortedArray += it.toString()
        }
    } else {
        sortedArray = data
        sortedArray.sort()
    }
    var result = ""
    if (sortingType == "natural") {
        println("Total ${dataType}s: ${sortedArray.size}.")
        if (dataType == "line") {
            result = "Sorted data:"
            for (i in sortedArray) {
                result += "\n $i"
            }
        } else {
            result = "Sorted data: ${sortedArray.joinToString(" ")}"
        }
        return result
    }
    val strMap: MutableMap<String, Int> = mutableMapOf()
    for (i in sortedArray) {
        if (strMap.containsKey(i)) {
            strMap[i] = strMap[i]!! + 1
        } else {
            strMap[i] = 1
        }
    }
    val sortedMap = strMap.toList().sortedBy { (_, value) -> value }.toMap()
    result = "Total ${dataType}s: ${sortedArray.size}."
    for (i in sortedMap) {
        result += "\n${i.key}: ${i.value} time(s), ${(i.value.toFloat() * 100 / sortedArray.size).toInt()}%"
    }
    return result
}

fun getCommand(args: Array<String>): Map<String, String> {
    val cmd = mutableMapOf(
        "dataType" to "word",
        "sortingType" to "natural",
        "inputFile" to "",
        "outputFile" to ""
    )
    for (i in 0 .. args.lastIndex) {
        if (args[i][0] == '-') {
            val param = if (args.lastIndex == i) "" else args[i + 1]
            when(args[i]) {
                "-dataType" -> {
                    if (param in listOf("word", "line", "long")) {
                        cmd["dataType"] = param
                    } else {
                        throw Exception("No data type defined!")
                    }
                }
                "-sortingType" -> {
                    if (param in listOf("natural", "byCount")) {
                        cmd["sortingType"] = param
                    } else {
                        throw Exception("No data type defined!")
                    }
                }
                "-inputFile" -> {
                    if (param.isEmpty()) {
                        throw Exception("No input file defined!")
                    } else {
                        cmd["inputFile"] = param
                    }
                }
                "-outputFile" -> {
                    if (param.isEmpty()) {
                        throw Exception("No output file defined!")
                    } else {
                        cmd["outputFile"] = param
                    }
                }
                else -> println("\"${args[i]}\" is not a valid parameter. It will be skipped.")
            }
        }
    }
    return cmd.toMap()
}

fun getInput(dataType: String, inputFile: String = ""): Array<String> {
    var input: Array<String> = arrayOf()
    if (inputFile.isEmpty()) {
        val scanner = Scanner(System.`in`)
        if (dataType == "line") {
            while (scanner.hasNextLine()) {
                input += scanner.nextLine()
            }
        } else {
            while (scanner.hasNext()) {
                input += scanner.next()
            }
        }
    } else {
        val fileLines = File(inputFile).readLines()
        for (i in fileLines) {
            if (dataType == "line") {
                input += i
            } else {
                input += i.split(" ")
            }
        }
    }
    return input
}