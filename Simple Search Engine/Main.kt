package search

fun main(args: Array<String>) {
    println(args[1])
    val data = Data(args[1])

    while (true) {
        printMenu()
        when (readln()) {
            "1" -> data.find()
            "2" -> data.printAll()
            "0" -> break
            else -> println("Incorrect option! Try again.")
        }
    }
    println("Bye!")
}

fun printMenu() {
    println("""
            
            === Menu ===
            1. Find a person
            2. Print all people
            0. Exit
        """.trimIndent())
}

