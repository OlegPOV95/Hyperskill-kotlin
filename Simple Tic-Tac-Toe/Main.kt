package tictactoe

class TikTacToe {
    private var positionsList: MutableList<MutableList<Char>> = MutableList(3) { MutableList(3) {' '} }
    private var gameIsOn: Boolean = true
    private var isXStep: Boolean = true
    private var moveCount: Int = 0

    fun start() {
        printBoard()
        while (gameIsOn)
            makeMove(readln())
    }

    private fun makeMove(userInput: String) {
        if (!userInput.first().isDigit())
            return println("You should enter numbers!")

        val coordinates: MutableList<Int> = userInput.split(" ").map { char -> char.toInt() - 1 }.toMutableList()

        if (coordinates[0] !in 0..2 || coordinates[1] !in 0..2)
            return println("Coordinates should be from 1 to 3!")

        if (positionsList[coordinates[0]][coordinates[1]] != ' ')
            return println("This cell is occupied! Choose another one!")

        positionsList[coordinates[0]][coordinates[1]] = if (isXStep) 'X' else 'O'
        isXStep = !isXStep
        moveCount ++
        printBoard()
        check()
    }

    private fun check() {
        var column: String = ""
        val diagonals: MutableList<String> = mutableListOf("", "")

        // Diagonal
        repeat(3) {
            diagonals[0] += positionsList[it][it].toString()
            diagonals[1] += positionsList[it][2 - it].toString()
        }
        if (diagonals.contains("XXX") || diagonals.contains("OOO"))
            return gameOver()

        // Rows  and columns
        for (i in 0..2) {
            // Rows
            val row: String = positionsList[i].joinToString("")
            if (row == "XXX" || row == "OOO")
                return gameOver()
            // Column
            repeat(3) {
                column += positionsList[it][i]
                if (column == "XXX" || column == "OOO")
                    return gameOver()
            }
            column = ""
        }

        // Out of moves
        if (moveCount == 9)
            return gameOver(true)
    }

    private fun gameOver(draw: Boolean = false) {
        gameIsOn = false
        when {
            draw -> println("Draw")
            isXStep -> println("O wins")
            else -> println("X wins")
        }
    }

    private fun printBoard() {
        var board: String = String()
        board += "---------\n"
        for (i in 0..2) {
            board += "| "
            repeat(3) {board += "${positionsList[i][it]} "}
            board += "|\n"
        }
        board += "---------"
        println(board)
    }
}

fun main() {
    val game = TikTacToe()
    game.start()
}