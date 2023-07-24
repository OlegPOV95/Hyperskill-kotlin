package wordsvirtuoso

import java.io.File

fun main(args: Array<String>) {
    val (candidateWords, allWords) = getWords(args) ?: return

    val secretWord = candidateWords.random()
    val wrongCharacters = mutableSetOf<Char>()
    val previousTries = mutableSetOf<String>()
    val timeStart = System.currentTimeMillis()
    var turnCount = 1

    println("Words Virtuoso")
    while (true) {
        println("Input a 5-letter word:")
        val input = readln().lowercase()
        if (input == "exit") {
            println("The game is over.")
            break
        }
        if (isInputInvalid(input, allWords)) {
            continue
        }

        var triesResult = ""
        repeat(5) {
            triesResult += when {
                input[it] == secretWord[it] -> "\u001B[48:5:10m"
                secretWord.contains(input[it]) -> "\u001B[48:5:11m"
                else -> {
                    wrongCharacters.add(input[it].uppercaseChar())
                    "\u001B[48:5:7m"
                }
            }
            triesResult += input[it].uppercaseChar() + "\u001B[0m"
        }
        previousTries += triesResult
        previousTries.forEach { println(it) }

        if (input == secretWord) {
            println("Correct!")
            if (turnCount == 1) {
                println("Amazing luck! The solution was found at once.")

            } else {
                val gameDuration  = System.currentTimeMillis() - timeStart
                println("The solution was found after $turnCount tries in ${gameDuration / 100} seconds.")
            }
            break
        } else {
            println("\u001B[48:5:14m" +
                    wrongCharacters.sorted().joinToString("") { it.uppercase() } +
                    "\u001B[0m"
            )

        }

        turnCount++
    }
}

fun getWords(filesName: Array<String>): Array<Set<String>>? {
    if (filesName.size != 2) {
        println("Error: Wrong number of arguments.")
        return null
    }
    val allWordsFile = File(filesName[0])
    if (!allWordsFile.exists()) {
        println("Error: The words file ${filesName[0]} doesn't exist.")
        return null
    }
    val candidateWordsFile = File(filesName[1])
    if (!candidateWordsFile.exists()) {
        println("Error: The candidate words file ${filesName[1]} doesn't exist.")
        return null
    }
    val allWords = allWordsFile.readLines().map { it.lowercase() }.toSet()
    val candidateWords = candidateWordsFile.readLines().map { it.lowercase() }.toSet()
    val regex = """[a-zA-Z]{5}+""".toRegex()
    var invalidWordsCount = allWords.count { !regex.matches(it) || it.toSet().size != 5 }
    if (invalidWordsCount != 0) {
        println("Error: $invalidWordsCount invalid words were found in the ${allWordsFile.name} file.")
        return null
    }
    invalidWordsCount = candidateWords.count { !regex.matches(it) || it.toSet().size != 5 }
    if (invalidWordsCount != 0) {
        println("Error: $invalidWordsCount invalid words were found in the ${candidateWordsFile.name} file.")
        return null
    }
    val notIncCandidate = candidateWords - allWords
    if (notIncCandidate.isNotEmpty()) {
        println("Error: ${notIncCandidate.size} candidate words are not included in the ${allWordsFile.name} file.")
        return null
    }
    return arrayOf(candidateWords, allWords)
}

fun isInputInvalid(word: String, words: Set<String>): Boolean {
    when {
        word.length != 5 -> {
            println("The input isn't a 5-letter word.")
        }
        !Regex("""[a-zA-Z]+""").matches(word) -> {
            println("One or more letters of the input aren't valid.")
        }
        word.toSet().size < 5 -> {
            println("The input has duplicate letters.")
        }
        word !in words -> {
            println("The input word isn't included in my words list.")
        }
        else -> return false
    }
    return true
}

