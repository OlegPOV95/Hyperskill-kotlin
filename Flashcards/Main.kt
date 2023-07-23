package flashcards

import java.io.File
import java.io.FileWriter

val CARDS: MutableMap<String, String> = mutableMapOf()
val HARDEST_CARDS: MutableMap<String, Int> = mutableMapOf()
var LOG_TEXT = ""

fun main(args: Array<String>) {
    val param = parsArgs(args)
    param["inputFile"]?.let { import(it) }
    while (true) {
        output("Input the action (add, remove, import, export, ask, exit, log, hardest card, reset stats):")
        when (input()) {
            "add" -> add()
            "remove" -> remove()
            "import" -> import()
            "export" -> export()
            "ask" -> asc()
            "log" -> log()
            "hardest card" -> hardest()
            "reset stats" -> reset()
            "exit" -> break
        }
    }
    param["outputFile"]?.let { export(it) }
    output("Bye bye!")
}

fun add() {
    output("The card:")
    val term = input()
    if (CARDS.containsKey(term))
        return output("The card \"$term\" already exists.")

    output("The definition of the card:")
    val definition = input()
    if (definition in CARDS.values)
        return output("The definition \"$definition\" already exists. Try again:")

    CARDS[term] = definition
    output("The pair (\"$term\":\"$definition\") has been added.")
}

fun remove() {
    output("Which card?")
    val key = input()
    if (CARDS.containsKey(key)) {
        CARDS.remove(key)
        output("The card has been removed.")
    } else {
        output("Can't remove \"$key\": there is no such card.")
    }
}

fun import(filePath: String = "") {
    val path = filePath.ifEmpty {
        output("File name:")
        input()
    }
    val file = File(path)
    if (!file.exists()) return output("File not found.")
    val lines = file.readLines()
    for (i in 0 until lines.lastIndex step 3) {
        CARDS[lines[i]] = lines[i + 1]
        HARDEST_CARDS[lines[i]] = lines[i + 2].toIntOrNull() ?: 0
    }
    output("${lines.size / 3} cards have been loaded.")
}

fun export(filePath: String = "") {
    val path = filePath.ifEmpty {
        output("File name:")
        input()
    }
    val file = FileWriter(path)
    file.use {
        CARDS.forEach { (k ,v) ->
            file.appendLine(k)
            file.appendLine(v)
            if (HARDEST_CARDS.containsKey(k)) {
                file.appendLine(HARDEST_CARDS[k].toString())
            } else {
                file.appendLine("0")
            }
        }
        output("${CARDS.size} cards have been saved.")
    }
}

fun asc() {
    output("How many times to ask?")
    val count = input().toInt()
    val cards = CARDS.entries
    repeat(count) {
        val card = cards.random()
        output("Print the definition of \"${card.key}\":")
        val answer = input()
        if (answer == card.value) {
            output("Correct!")
        } else {
            val answerCard: String? = cards.find { it.value == answer }?.key
            val response = "Wrong. The right answer is \"${card.value}\"" +
                    if (answerCard != null) ", but your definition is correct for \"${answerCard}\"." else "."
            HARDEST_CARDS[card.key] = (HARDEST_CARDS[card.key] ?: 0) + 1
            output(response)
        }
    }
}

fun log() {
    output("File name:")
    val file = File(input())
    file.writeText(LOG_TEXT)
    output("The log has been saved.")
}

fun hardest() {
    if (HARDEST_CARDS.isEmpty()) return output("There are no cards with errors.")
    val maxValue = HARDEST_CARDS.values.maxOrNull()
    val hardest = HARDEST_CARDS.filterValues { it == maxValue }.keys
    output("The hardest card${if (hardest.size > 1) "s are " else " is "}" +
            "${hardest.joinToString("\", \"", "\"", "\"")}. You have $maxValue errors answering " +
            "${if (hardest.size > 1) "them" else "it"}.")
}

fun reset() {
    HARDEST_CARDS.clear()
    output("Card statistics have been reset.")
}

fun parsArgs(args: Array<String>): Map<String, String> {
    val map: MutableMap<String, String> = mutableMapOf()
    val pattern = """-\w+\b\s\w+.\w+\b"""
    val match = Regex(pattern).findAll(args.joinToString(" "))
    for (i in match) {
        when (i.value.substringBefore(" ")) {
            "-import" -> map["inputFile"] = i.value.substringAfter(" ")
            "-export" -> map["outputFile"] = i.value.substringAfter(" ")
        }
    }
    return map
}

fun input(): String {
    val s = readln()
    LOG_TEXT += "$s\n"
    return s
}

fun output(s: String) {
    LOG_TEXT += "$s\n"
    println(s)
}
