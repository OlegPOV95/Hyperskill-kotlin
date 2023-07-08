package cinema

class Cinema {
    private val rows: Int
    private val columns: Int
    private val cinema: MutableList<MutableList<String>>
    private val totalSeats: Int
    private var totalIncome: Int
    private var purchasedTickets: Int = 0
    private var currentIncome: Int = 0

    init {
        println("Enter the number of rows:")
        rows = readln().toInt()
        println("Enter the number of seats in each row:")
        columns = readln().toInt()
        cinema = MutableList(rows) {
            MutableList(columns) {"S"}
        }
        totalSeats = rows * columns
        totalIncome = if (totalSeats > 60) {
            (rows / 2 * columns * 10) + ((rows - rows / 2) * columns * 8)
        } else {
            totalSeats * 10
        }
    }

    fun start() {
        while (true) {
            printMenu()
            when (readln().toInt()) {
                1 -> printSeatsTable()
                2 -> buyTicket()
                3 -> printStatistics()
                0 -> break
            }
        }
    }

    private fun printMenu() {
        println("""
        1. Show the seats
        2. Buy a ticket
        3. Statistics
        0. Exit
    """.trimIndent())
    }

    private fun printSeatsTable() {
        var result: String = "\nCinema:\n "
        repeat(cinema[0].size) {result += " ${it + 1}"}
        var count: Int = 1
        for (i in cinema) {
            result += "\n$count " + i.joinToString(" ")
            count++
        }
        result += "\n"
        println(result)
    }

    private fun buyTicket() {
        println("Enter a row number:")
        val rowSelected: Int = readln().toInt()
        println("Enter a seat number in that row:")
        val columnSelected: Int = readln().toInt()

        if (rowSelected !in 1..rows || columnSelected !in 1..columns) {
            println("Wrong input!\n")
            return buyTicket()
        }

        if ("B" in cinema[rowSelected - 1][columnSelected - 1]) {
            println("That ticket has already been purchased!\n")
            return buyTicket()
        }

        cinema[rowSelected - 1][columnSelected - 1] = "B"

        val totalCost: Int = if (rows * columns > 60 && rowSelected > rows / 2) 8 else 10

        currentIncome += totalCost
        purchasedTickets += 1

        println("Ticket price: $${totalCost}\n")
    }

    private fun printStatistics() {
        val percentage: Double = purchasedTickets.toDouble() / totalSeats * 100
        println("""
            Number of purchased tickets: $purchasedTickets
            Percentage: ${"%.2f".format(percentage)}%
            Current income: $${currentIncome}
            Total income: $${totalIncome}
            
    """.trimIndent())
    }
}

fun main() {
    val cinema = Cinema()
    cinema.start()
}