package connectfour

class Game {
    val players: Array<String>
    val symbols = arrayOf("o", "*")
    val score: Array<Int>  = arrayOf(0, 0)
    val gamesCount: Int
    var activePlayer = 0
    private val boardSize: Array<Int>

    init {
        players = addPlayers()
        boardSize = selectBoardSize()
        gamesCount = selectGamesCount()
    }

    fun printStartInfo() {
        println("${players[0]} VS ${players[1]}")
        println("${boardSize[0]} X ${boardSize[1]} board")
        println(if (gamesCount == 1) {"Single game"} else {"Total $gamesCount games"})
    }

    fun printGameScore() {
        println("Score")
        println("${players[0]}: ${score[0]} ${players[1]}: ${score[1]}")
    }

    fun createBoard(): Array<Array<String>> = Array(boardSize[0]) { Array(boardSize[1]) {" "} }

    fun changeActivePlayer() {
        activePlayer = activePlayer.xor(1)
    }

    fun getActivePlayerName(): String = players[activePlayer]

    fun getActivePlayerSymbol(): String = symbols[activePlayer]

    fun addGameScore(value: String) {
        when (value) {
            "win" -> score[activePlayer] += 2
            "draw" -> {
                score[0]++
                score[1]++
            }
        }
    }

    private fun addPlayers(): Array<String> {
        println("First player's name:")
        val a = readln()
        println("Second player's name:")
        val b = readln()
        return arrayOf(a, b)
    }

    private fun selectBoardSize(): Array<Int> {
        var rows = 6
        var columns = 7
        val boardRegex = Regex("\\d+\\s*[x|X]\\s*\\d+")
        while (true) {
            println("Set the board dimensions (Rows x Columns)")
            println("Press Enter for default (6 x 7)")
            val input = readln().trim()
            if (input.isEmpty()) {
                break
            }
            if (!input.matches(boardRegex)) {
                println("Invalid input")
                continue
            }
            val fieldParam = Regex("\\d+").findAll(input)
                .map { it.value.toInt() }
                .toList()
            if (fieldParam[0] !in 5..9) {
                println("Board rows should be from 5 to 9")
                continue
            }
            if (fieldParam[1] !in 5..9) {
                println("Board columns should be from 5 to 9")
                continue
            }
            rows = fieldParam[0]
            columns = fieldParam[1]
            break
        }
        return arrayOf(rows, columns)
    }

    private fun selectGamesCount(): Int {
        while (true) {
            println(
                """
                Do you want to play single or multiple games?
                For a single game, input 1 or press Enter
                Input a number of games:
            """.trimIndent()
            )
            val input = readln()

            if (input.isEmpty()) {
                return 1
            }
            if (Regex("[1-9]\\d*").matches(input)) {
                return input.toInt()
            }
            println("Invalid input")
        }
    }
}