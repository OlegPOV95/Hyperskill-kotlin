package connectfour

fun main() {
    println("Connect Four")
    val game = Game()
    game.printStartInfo()
    repeat(game.gamesCount) {
        if (game.gamesCount > 1) {
            if (it >= 1) {
                game.printGameScore()
            }
            println("Game #${it + 1}")
        }
        val board = game.createBoard()
        printBoard(board)
        var turnCount = 0
        while (true) {
            println("${game.getActivePlayerName()}'s turn:")
            val input = readln().trim()
            if (input == "end") {
                return println("Game over!")
            }
            if (!Regex("\\d+").matches(input)) {
                println("Incorrect column number")
                continue
            }
            val col = input.toInt() - 1
            if (col !in 0 .. board[0].lastIndex) {
                println("The column number is out of range (1 - ${board[0].size})")
                continue
            }
            var turnComplete = false
            var row = 0
            for (i in board.lastIndex downTo 0) {
                if (board[i][col] == " ") {
                    board[i][col] = game.getActivePlayerSymbol()
                    turnComplete = true
                    row = i
                    turnCount++
                    break
                }
            }
            if (!turnComplete) {
                println(println("Column $input is full"))
                continue
            }
            printBoard(board)
            if (isWin(board, row, col, game.getActivePlayerSymbol())) {
                println("Player ${game.getActivePlayerName()} won")
                game.addGameScore("win")
                break
            }
            if (turnCount == board.size * board[0].size) {
                println("It is a draw")
                game.addGameScore("draw")
                break
            }
            game.changeActivePlayer()
        }
        game.changeActivePlayer()
    }
    if (game.gamesCount > 1) {
        game.printGameScore()
    }
    println("Game over!")
}

fun isWin (board: Array<Array<String>>, row: Int, col: Int, dotType: String): Boolean {
    val winningSequence = dotType.repeat(4)
    // Horizontal
    if (board[row].joinToString("").contains(winningSequence)) {
        return true
    }
    // Vertical
    var vertical = ""
    for (i in board) {
        vertical += i[col]
    }
    if (vertical.contains(winningSequence)) {
        return true
    }
    //Diagonal
    var rightDown = ""
    var leftUp = ""
    var rightUp = ""
    var leftDown = ""
    for (i in 1..3) {
        if (board.lastIndex >= row + i && board[0].lastIndex >= col + i) {
            rightDown += board[row + i][col + i]
        }
        if (row - i >= 0 && col - i >= 0) {
            leftUp += board[row - i][col - i]
        }
        if (row - i >= 0 && board[0].lastIndex >= col + i) {
            rightUp += board[row - i][col + i]
        }
        if (board.lastIndex >= row + i && col - i >= 0) {
            leftDown += board[row + i][col - i]
        }
    }
    if ((rightDown + dotType + leftUp).contains(winningSequence) || (rightUp + dotType + leftDown).contains(winningSequence)) {
        return true
    }
    return false
}

fun printBoard(board: Array<Array<String>>) {
    var result = ""
    val width = board[0].size
    repeat(width) {
        result += " ${it + 1}"
    }
    for (i in board) {
        result += "\n║"
        for (k in i) {
            result += "$k║"
        }
    }
    result += "\n╚" + "═╩".repeat(width - 1) + "═╝"
    println(result)
}
