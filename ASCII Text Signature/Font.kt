package signature

import java.io.File

class Font(filePath: String, private val whiteSpaceSize: Int = 4) {

    val letterHeight: Int
    private val fileLines: List<String>
    private val lettersWidth: Map<Char, Int>
    private val letters: MutableMap<Char, Array<String>>

    init {
        fileLines = File(filePath).readLines()
        letterHeight = fileLines[0].split(" ")[0].toInt()

        lettersWidth = getLettersWidth()
        letters = getLetters()
    }

    fun getStringWidth(str: String): Int {
        var stringWidth = 0
        for (i in str) {
            if (lettersWidth.containsKey(i)) {
                stringWidth += lettersWidth[i]!!
            }
            if (i == ' ') {
                stringWidth += whiteSpaceSize
            }
        }
        return stringWidth
    }

    fun getLetterString(letter: Char, strNum: Int): String {
        if (letters.containsKey(letter) && strNum < letterHeight) {
            return letters[letter]!![strNum]
        }
        if (letter == ' ') {
            return " ".repeat(whiteSpaceSize)
        }
        return ""
    }

    private fun getLettersWidth(): Map<Char, Int> {

        val result: MutableMap<Char, Int> = mutableMapOf()

        for (i in 1 until fileLines.size step letterHeight + 1) {
            result[fileLines[i][0]] = fileLines[i].slice(2..fileLines[i].lastIndex).toInt()
        }

        return result
    }

    private fun getLetters(): MutableMap<Char, Array<String>> {
        val result: MutableMap<Char, Array<String>> = mutableMapOf()

        for (i in 1 until fileLines.size step letterHeight + 1) {

            result[fileLines[i][0]] = Array(letterHeight) {
                fileLines[i + 1 + it]
            }

        }
        return result
    }
}
