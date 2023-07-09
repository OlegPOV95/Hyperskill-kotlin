package converter

enum class Units (val type: String, val rate: Double, val dictionary: List<String>){
    METERS("length",1.0, listOf("m", "meter", "meters")),
    KILOMETERS("length",1000.0, listOf("km", "kilometer", "kilometers")),
    CENTIMETERS("length",0.01, listOf("cm", "centimeter", "centimeters")),
    MILLIMETERS("length",0.001, listOf("mm", "millimeter", "millimeters")),
    MILES("length",1609.35, listOf("mi", "mile", "miles")),
    YARDS("length",0.9144, listOf("yd", "yard", "yards")),
    FEET("length",0.3048, listOf("ft", "foot", "feet")),
    INCHES("length",0.0254, listOf("in", "inch", "inches")),
    GRAMS("weight",1.0, listOf("g", "gram", "grams")),
    KILOGRAMS("weight",1000.0, listOf("kg", "kilogram", "kilograms")),
    MILLIGRAMS("weight",0.001, listOf("mg", "milligram", "milligrams")),
    POUNDS("weight",453.592, listOf("lb", "pound", "pounds")),
    OUNCES("weight",28.3495, listOf("oz", "ounce", "ounces")),
    CELSIUS("temperature",1.0, listOf("dc", "degree Celsius", "degrees Celsius", "celsius", "c")),
    FAHRENHEIT("temperature",1.0, listOf("df", "degree Fahrenheit", "degrees Fahrenheit", "fahrenheit", "f")),
    KELVINS("temperature",1.0, listOf("k", "kelvin", "kelvins"));
}

fun main() {

    while (true) {
        println("Enter what you want to convert (or exit): ")
        val input: String = readln().trim()

        if (input == "exit")
            break

        val count: Double = try {
            input.substringBefore(" ").toDouble()
        } catch (e: Exception) {
            println("Parse error")
            continue
        }

        val unitIn: String = findUnit(input,"from")
        val unitTo: String = findUnit(input,"to")


        if ((unitIn == "???" || unitTo == "???") || (Units.valueOf(unitIn).type != Units.valueOf(unitTo).type)) {
            println("Conversion from ${getNameByCount(unitIn, 2.0)} to ${getNameByCount(unitTo, 2.0)} is impossible")
            continue
        }

        val result: Double =
            if (Units.valueOf(unitIn).type == "temperature")
                calculateTemperature(unitIn, unitTo, count)
            else if (count <= 0.0) {
                println("${Units.valueOf(unitIn).type.replaceFirstChar { c -> c.uppercase() }} shouldn't be negative.")
                continue
            } else
                count * Units.valueOf(unitIn).rate / Units.valueOf(unitTo).rate

        println("$count ${getNameByCount(unitIn, count)} is $result ${getNameByCount(unitTo, result)}")
    }
}

fun calculateTemperature(from: String, to: String, value: Double): Double {
    if (from == to)
        return value
    when (from) {
        "CELSIUS" -> when (to) {
            "FAHRENHEIT" -> return value * 1.8 + 32.00
            "KELVINS" -> return value + 273.15
        }
        "FAHRENHEIT" -> when (to) {
            "CELSIUS" -> return (value - 32.00) / 1.8
            "KELVINS" -> return (value + 459.67) / 1.8
        }
        "KELVINS" -> when (to) {
            "CELSIUS" -> return value - 273.15
            "FAHRENHEIT" -> return value * 1.8 - 459.67
        }
    }
    return 0.0
}

fun findUnit(str: String, type: String): String {
    val regex = Regex("[^A-Za-z ]")

    val substr: List<String> = if (str.contains(" in ")) {
        str.lowercase().split(" in ")
    } else if (str.contains(" to ")){
        str.lowercase().split(" to ")
    } else return "???"

    for (i in Units.values()) {
        for (k in i.dictionary) {
            when (type) {
                "from" -> if (substr[0].replace(regex, "").trim().contentEquals(k.lowercase())) return i.name
                "to" -> if (substr[1].contentEquals(k.lowercase())) return i.name
            }
        }
    }
    return "???"
}

fun getNameByCount(unit: String, count: Double): String {
    if (unit == "???")
        return "???"
    if (count != 1.0)
        return Units.valueOf(unit).dictionary[2]
    return Units.valueOf(unit).dictionary[1]
}

