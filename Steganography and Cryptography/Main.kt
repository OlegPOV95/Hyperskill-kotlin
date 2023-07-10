package cryptography
import java.awt.Color
import java.io.File
import javax.imageio.ImageIO
import java.awt.image.BufferedImage
import kotlin.experimental.xor

val END_OF_MESSAGE = byteArrayOf(0, 0, 3)

fun main() {
    while (true) {
        println("Task (hide, show, exit):")
        when(val input = readln()) {
            "hide" -> hide()
            "show" -> show()
            "exit" -> break
            else -> println("Wrong task: $input")
        }
    }
    println("Bye!")
}

fun hide() {
    println("Input image file:")
    val inputImagePath = readln()

    println("Output image file:")
    val outputImagePath = readln()

    println("Message to hide:")
    val messageText = readln().toByteArray()

    println("Password:")
    val password = readln().toByteArray()

    val message = crypto(messageText, password) + END_OF_MESSAGE

    val binaryMessage = ArrayList<Int>()
    for (i in 0 .. message.lastIndex) {
        String.format("%8s", message[i].toString(2)).replace(" ", "0").forEach {
            binaryMessage.add(it.toString().toInt())
        }
    }

    try {
        val image: BufferedImage = ImageIO.read(File(inputImagePath))

        if (image.width * image.height < binaryMessage.size) {
            return println("The input image is not large enough to hold this message.")
        }

        var counter = 0
        loop@ for (row in 0 until image.height) {
            for (col in 0 until image.width) {
                val rgb = Color(image.getRGB(col, row))
                val newRgb = Color(
                    rgb.red,
                    rgb.green,
                    rgb.blue.and(254).or(binaryMessage[counter])
                )
                image.setRGB(col, row, newRgb.rgb)

                counter++
                if (counter >= binaryMessage.size) {
                    break@loop
                }
            }
        }

        ImageIO.write(image, "png", File(outputImagePath))
        println("Message saved in $outputImagePath image.")

    } catch (e: Exception) {
        return println(e.message)
    }
}

fun show() {
    println("Input image file:")
    val inputImagePath = readln()

    println("Password:")
    val password = readln().toByteArray()

    try {
        val image = ImageIO.read(File(inputImagePath))
        var message = byteArrayOf()
        var symbol = ""
        loop@ for (row in 0 until image.height) {
            for (col in 0 until image.width) {

                val rgb = Color(image.getRGB(col, row))
                symbol += rgb.blue.and(1)

                if (symbol.length == 8) {
                    message += Integer.parseInt(symbol, 2).toByte()
                    symbol = ""
                }

                if (message.size >= 3 && message.last().toInt() == 3)
                {
                    var flag = true
                    val messageTriplet = message.takeLast(3)
                    for (i in 0..2) {
                        if (messageTriplet[i] != END_OF_MESSAGE[i]) {
                            flag = false
                        }
                    }
                    if (flag) {
                        break@loop
                    }
                }
            }
        }

        val decryptMessageText = crypto(message, password).toString(Charsets.UTF_8)
        println("Message:")
        println(decryptMessageText.slice(0..decryptMessageText.lastIndex - 3))

    } catch (e: Exception) {
        println(e.message)
    }

}


fun crypto(text: ByteArray, key: ByteArray): ByteArray {
    var result = byteArrayOf()

    var counter = 0
    for (i in text.indices) {

        result += text[i].xor(key[counter])

        if ((i + 1) % key.size == 0) {
            counter = 0
        } else {
            counter++
        }
    }
    return result
}
