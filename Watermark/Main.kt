package watermark

import java.awt.Color
import java.awt.image.BufferedImage
import java.io.File
import javax.imageio.ImageIO

fun main() {
    val image = getImage() ?: return
    val watermark = getImage("watermark") ?: return

    if (image.height < watermark.height || image.width < watermark.width) {
        return println("The watermark's dimensions are larger.")
    }

    var useAlpha = false
    var transparencyColor: Color? = null
    if (watermark.transparency == 3) {
        println("Do you want to use the watermark's Alpha channel?")
        useAlpha = readln().lowercase() == "yes"
    } else {
        println("Do you want to set a transparency color?")
        if (readln().lowercase() == "yes") {
            transparencyColor = getTransparencyColor() ?: return
        }
    }

    println("Input the watermark transparency percentage (Integer 0-100):")
    val transparency = readln().toIntOrNull() ?: return println("The transparency percentage isn't an integer number.")
    if (transparency !in 1..100) {
        return println("The transparency percentage is out of range.")
    }

    println("Choose the position method (single, grid):")
    val method = readln().lowercase()
    if (method !in listOf("single", "grid")) {
        println("The position method input is invalid.")
        return
    }
    val position: MutableMap<String, Int> = mutableMapOf(
        "startX" to 0,
        "startY" to 0,
        "endX" to image.width,
        "endY" to image.height)

    if (method == "single") {
        val maxX = image.width - watermark.width
        val maxY = image.height - watermark.height
        println("Input the watermark position ([x 0-$maxX] [y 0-$maxY]):")
        val input = readln()
        if (!Regex("""^-?\d+ -?\d+$""").matches(input)) {
            return println("The position input is invalid.")
        }
        val (x, y) = input.split(" ").map { it.toInt() }
        if (x !in 0..maxX || y !in 0..maxY) {
            return println("The position input is out of range.")
        }
        position["startX"] = x
        position["startY"] = y
        position["endX"] = x + watermark.width
        position["endY"] = y + watermark.height
    }

    println("Input the output image filename (jpg or png extension):")
    val outputImageName = readln()
    if (!Regex("""^[\w/\\-]+.(jpg|png)$""").matches(outputImageName)) {
        return println("The output file extension isn't \"jpg\" or \"png\".")
    }


    val output = createOutputImage(image, watermark, transparency, useAlpha, transparencyColor, position)

    val outputFile = File(outputImageName)

    ImageIO.write(output, outputFile.extension, outputFile)
    println("The watermarked image $outputImageName has been created.")
}

fun getImage(messageName: String = ""): BufferedImage? {
    if (messageName.isNotEmpty()) {
        println("Input the $messageName image filename:")
    } else {
        println("Input the image filename:")
    }
    val file = File(readln())
    if (!file.exists()) {
        println("The file $file doesn't exist.")
        return null
    }
    val image = ImageIO.read(file)
    if  (image.colorModel.numColorComponents != 3) {
        println("The number of ${messageName.ifEmpty { "image" }} color components isn't 3.")
        return null
    }
    if (image.colorModel.pixelSize !in listOf(24, 32)) {
        println("The ${messageName.ifEmpty { "image" }} isn't 24 or 32-bit.")
        return null
    }
    return image
}

fun getTransparencyColor(): Color? {
    println("Input a transparency color ([Red] [Green] [Blue]):")
    val input = readln()
    if (!Regex("""^[0-2]?[0-5]?[0-5] [0-2]?[0-5]?[0-5] [0-2]?[0-5]?[0-5]${'$'}""").matches(input)) {
        println("The transparency color input is invalid.")
        return null
    }
    val rgb = input.split(" ").map { it.toInt() }
    return Color(rgb[0], rgb[1], rgb[2])
}

fun createOutputImage(image: BufferedImage, watermark: BufferedImage, watermarkTransparency: Int, useAlpha: Boolean,
                      transparencyColor: Color?, position: MutableMap<String, Int>): BufferedImage {
    var watermarkX = 0
    var watermarkY = 0
    for (i in position["startX"]!! until position["endX"]!!) {
        if (watermarkX % watermark.width == 0) watermarkX = 0
        for (k in position["startY"]!! until position["endY"]!!) {
            if (watermarkY % watermark.height == 0) watermarkY = 0
            val imageColor = Color(image.getRGB(i, k))
            val watermarkColor = Color(watermark.getRGB(watermarkX, watermarkY), useAlpha)
            val transparency = if (useAlpha) (watermarkColor.alpha / 2.55).toInt() * watermarkTransparency / 100 else watermarkTransparency
            val outputColor =
                if (watermarkColor == transparencyColor) {
                    imageColor
                } else {
                    Color(
                        (transparency * watermarkColor.red + (100 - transparency) * imageColor.red) / 100,
                        (transparency * watermarkColor.green + (100 - transparency) * imageColor.green) / 100,
                        (transparency * watermarkColor.blue + (100 - transparency) * imageColor.blue) / 100
                    )
                }
            image.setRGB(i, k, outputColor.rgb)
            watermarkY++
        }
        watermarkY = 0
        watermarkX++
    }
    return image
}