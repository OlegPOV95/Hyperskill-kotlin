package battleship

import kotlin.math.abs

enum class Unit(val type: String, val size: Int) {
    AIRCRAFT_CARRIER("Aircraft Carrier",5),
    BATTLESHIP("Battleship",4),
    SUBMARINE("Submarine",3),
    CRUISER("Cruiser",3),
    DESTROYER("Destroyer",2);
}

class Ship(val size: Int) {
    var hits = 0
        set(value) {
            field = value
            if (field == size) isSank = true
        }
    var isSank = false
}

class Field {
    private val fogCell = '~'
    private val shipCell = 'O'
    private val hitCell = 'x'
    private val missCell = 'M'
    private val field = Array(10) { Array(10) {fogCell} }
    private val shipsPositions = mutableMapOf<String, Ship>()
    var allShipsSunk = false

    fun setUnit(startX: Int, startY: Int, endX: Int, endY: Int, size: Int): Boolean {
        val xRange = if (startX > endX) endX..startX else startX..endX
        val yRange = if (startY > endY) endY..startY else startY..endY

        for (x in xRange.first - 1 .. xRange.last + 1) {
            for (y in yRange.first - 1 .. yRange.last + 1) {
                if (!isFogCell(x, y)) return false
            }
        }

        val ship = Ship(size)
        for (x in xRange) {
            for (y in yRange) {
                field[x][y] = shipCell
                shipsPositions["$x$y"] = ship
            }
        }
        return true
    }

    fun shot(x: Int, y: Int) {
        var msg = ""
        when (field[x][y]) {
            shipCell -> {
                field[x][y] = hitCell
                shipsPositions.remove("$x$y")?.let { ship ->
                    ship.hits++
                    msg = if (ship.isSank) {
                        if (shipsPositions.isEmpty()) {
                            allShipsSunk = true
                            "You sank the last ship. You won. Congratulations!"
                        } else {
                            "You sank a ship!"
                        }
                    } else {
                        "You hit a ship!"
                    }
                }
            }
            hitCell -> msg = "You hit a ship!"
            fogCell -> {
                field[x][y] = missCell
                msg = "You missed!"
            }
            missCell -> msg = "You missed!"
        }
        printField()
        println(msg)
    }

    fun printField(hideShips: Boolean = true) {
        print("  1 2 3 4 5 6 7 8 9 10")
        val startChar = 'A'
        for (x in 0..9) {
            print("\n${startChar + x}")
            for (y in 0..9) {
                var cell = field[x][y]
                if (hideShips && cell == shipCell) cell = fogCell
                print(" $cell")
            }
        }
        print("\n")
    }

    private fun isFogCell(x: Int, y: Int): Boolean {
        return try {
            field[x][y] == fogCell
        } catch (_: ArrayIndexOutOfBoundsException) {
            true
        }
    }
}


fun main() {
    println("Player 1, place your ships on the game field")
    val playerOneField = takeNewField()
    endMove()

    println("Player 2, place your ships to the game field")
    val playerTwoField = takeNewField()
    endMove()

    var isFirstPlayer = true
    while (true) {
        val field = if(isFirstPlayer) {
            playerTwoField.printField()
            println("---------------------")
            playerOneField.printField(false)
            println("Player 1, it's your turn:")
            playerTwoField
        } else {
            playerOneField.printField()
            println("---------------------")
            playerTwoField.printField(false)
            println("Player 2, it's your turn:")
            playerOneField
        }

        val input = getInput()
        try {
            if (!isValidPosition(input)) {
                throw Exception()
            }

            field.shot(
                input.first().code - 65,
                input.substring(1).toInt() - 1)

            if (field.allShipsSunk) break
        } catch (e: Exception) {
            println("Error! You entered the wrong coordinates! Try again:")
        }
        isFirstPlayer = !isFirstPlayer
        endMove()
    }
}

fun isValidPosition(input: String): Boolean {
    return input matches Regex("""^[A-J]([1-9]|10)$""")
}

fun getInput(): String {
    return readln().trim().uppercase()
}

fun takeNewField(): Field {
    val field = Field()
    field.printField()
    Unit.values().forEach { unit ->
        while (true) {
            println("Enter the coordinates of the ${unit.type} (${unit.size} cells):")
            try {
                val coordinates = getInput().split(" ")

                if (!coordinates.all { isValidPosition(it) }) {
                    throw Exception("Error! Incorrect input! Try again:")
                }

                val startX: Int = coordinates[0].first().code - 65
                val endX: Int = coordinates[1].first().code - 65
                val startY: Int = coordinates[0].substring(1).toInt() - 1
                val endY: Int = coordinates[1].substring(1).toInt() - 1

                if (startX != endX && startY != endY) {
                    throw Exception("Error! Wrong ship location! Try again:")
                }

                if (abs(endX - startX + endY - startY) != unit.size - 1) {
                    throw Exception("Error! Wrong length of the ${unit.type}! Try again:")
                }

                if (field.setUnit(startX, startY, endX, endY, unit.size)) {
                    break
                } else {
                    throw Exception("Error! You placed it too close to another one. Try again:")
                }

            } catch (e: Exception) {
                println(e.message)
            }
        }
        field.printField(false)
    }
    return field
}

fun endMove() {
    println("Press Enter and pass the move to another player")
    readln()
}

