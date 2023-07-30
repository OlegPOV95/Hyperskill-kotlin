package calculator

fun main() {
    val calculator = Calculator()
    while (true) {
        val input = readln()

        if (input.isEmpty()) {
            continue
        }
        if (input == "/exit") {
            break
        }
        if (input == "/help") {
            println("The program calculates the sum of numbers")
            continue
        }
        if (Regex("""^/\w*""").matches(input)) {
            println("Unknown command")
            continue
        }

        try {
            val result = calculator.calculate(input)
            if (result.isEmpty()) continue
            println(result)
        } catch (e: Exception) {
            println(e.message)
            continue
        }
    }

    println("Bye!")
}