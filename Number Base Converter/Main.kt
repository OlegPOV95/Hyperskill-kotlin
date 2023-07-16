package converter
import java.math.BigInteger
import java.math.BigDecimal
import java.math.RoundingMode
import kotlin.math.pow
// Do not delete this line

fun main() {
    while (true) {
        println("Enter two numbers in format: {source base} {target base} (To quit type /exit)")
        val input = readln()
        if (input == "/exit")
            break
        convertNumbers(input)
    }
}

fun convertNumbers(base: String) {
    val (source, target) = base.split(" ").map { it.toInt() }
    while (true) {
        println("Enter number in base $source to convert to base $target (To go back type /back)")
        val input = readln().lowercase()
        if (input == "/back")
            break
        var number = ""
        var fraction = ""
        if (input.contains(".")) {
            val splitNumber: List<String> = input.split(".")
            number = splitNumber[0]
            fraction = fractionConvert(splitNumber[1], source, target)
        } else {
            number = input
        }
        if (source != 10)
            number = toDecimal(number, source.toBigInteger())
        if (target != 10)
            number = fromDecimal(number, target.toBigInteger())
        println("Conversion result: $number$fraction")
    }
}

fun fractionConvert(numberString: String, source: Int, target: Int, precision: Int = 5): String {
    var decimal: BigDecimal = BigDecimal.ZERO
    if (source != 10) {
        val sourceBase = source.toDouble()
        var power: Double = 1.0
        for (i in numberString) {
            val digit = if (i.isLetter()) i.code - 87 else i.digitToInt()
            decimal += BigDecimal.ONE
                .divide(sourceBase.pow(power).toBigDecimal(), 100,  RoundingMode.DOWN)
                .multiply(digit.toBigDecimal())
            power ++
        }
        decimal = decimal.remainder(BigDecimal.ONE)
        println(decimal)
    } else {
        decimal ="0.$numberString".toBigDecimal()
    }

    var result = ""
    if (target != 10) {
        val targetBase = target.toBigDecimal()
        var iterator = 0
        while (iterator < precision) {
            val divideAndRemainder = decimal.multiply(targetBase)
                .divideAndRemainder(BigDecimal.ONE)
            if (divideAndRemainder[0] == BigDecimal.ZERO)
                break
            result += if (divideAndRemainder[0] >= BigDecimal.TEN)
                Char(divideAndRemainder[0].toInt() + 55) else divideAndRemainder[0].toInt().toString()
            decimal = divideAndRemainder[1]
            iterator++
        }
    }
    return ".$result"
}

fun toDecimal(numberString: String, base: BigInteger): String {
    val number = numberString.reversed().lowercase()
    var result = BigInteger.ZERO
    for (i in 0.. number.lastIndex) {
        result += if(number[i].isLetter()) {
            (number[i].code - 87).toBigInteger() * base.pow(i)
        } else {
            number[i].digitToInt().toBigInteger() * base.pow(i)
        }
    }
    return result.toString()
}

fun fromDecimal(numberString: String, base: BigInteger): String {
    var num = numberString.toBigInteger()
    var result = ""
    while (true) {
        val division = num.divide(base)
        val remainder = num.remainder(base)
        result += if (remainder >= BigInteger.TEN) Char(remainder.toInt() + 55) else remainder
        if (division == BigInteger.ZERO)
            break
        num = division
    }
    return result.reversed()
}