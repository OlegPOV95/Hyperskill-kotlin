package signature

import java.io.File

fun main() {

    println("Enter name and surname:")
    val name = readln()

    println("Enter person's status:")
    val status = readln()


    val romanFont = Font(".roman.txt", 10)
    val mediumFont = Font("medium.txt", 5)
    val nameWidth = romanFont.getStringWidth(name)
    val statusWidth = mediumFont.getStringWidth(status)
    val width = 4 + if (nameWidth > statusWidth) nameWidth else statusWidth
    var result = ""

    result += "8".repeat(width + 4)
    result += getString(width, nameWidth, name, romanFont)
    result += getString(width, statusWidth, status, mediumFont)
    result += "\n" + "8".repeat(width + 4)

    println(result)
}

fun getString(windowWidth: Int, textWidth: Int, text: String, font: Font): String {
    var result = "\n"
    val leftPadding = (windowWidth - textWidth) / 2
    val rightPadding = windowWidth - textWidth - leftPadding

    for (i in 0 until font.letterHeight) {
        result += if (i != 0) "\n88" else "88"
        repeat(leftPadding) {
            result += " "
        }
        for (k in text) {
            result += font.getLetterString(k, i)
        }
        repeat(rightPadding) {
            result += " "
        }
        result += "88"
    }
    return result
}

