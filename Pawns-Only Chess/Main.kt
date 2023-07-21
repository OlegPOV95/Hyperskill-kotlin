package chess

import kotlin.math.abs

fun main() {
    println("Pawns-Only Chess")

    println("First Player's name:")
    val firstPlayer = readln()
    println("Second Player's name:")
    val secondPlayer = readln()

    var currentPlayer = firstPlayer
    var currentColor = "W"
    var opponentColor = "B"
    var whiteCount = 8
    var blackCount = 8
    var whiteEnPassant = ""
    var blackEnPassant = ""

    val chessboard: Array<Array<String>> = Array(8) { Array(8) { " " } }
    repeat(8) {
        chessboard[1][it] = "W"
        chessboard[6][it] = "B"
    }
    printChessboard(chessboard)


    while (true) {
        println("$currentPlayer's turn:")
        val input = readln()

        if (input == "exit") {
            break
        }

        if (!Regex("([a-h][1-8]){2}").matches(input)) {
            println("Invalid Input")
            continue
        }

        val colFrom: Int = input[0].code - 97
        val rowFrom: Int = input[1].digitToInt() - 1
        val colTo: Int = input[2].code - 97
        val rowTo: Int = input[3].digitToInt() - 1

        if (chessboard[rowFrom][colFrom] != currentColor) {
            println("No ${if (currentColor == "W") "white" else "black"} pawn at ${input.substring(0,2)}")
            continue
        }

        val diagonal: Int = abs(colTo - colFrom)
        if (diagonal > 1 || (diagonal == 0 && chessboard[rowTo][colTo] != " ")) {
            println("Invalid Input")
            continue
        }

        if (currentColor == "W") {
            val maxRank = if (rowFrom == 1) 2 else 1

            if (rowFrom >= rowTo || rowTo - rowFrom > maxRank) {
                println("Invalid Input")
                continue
            }
            if (diagonal == 1 && "$rowTo$colTo" != blackEnPassant && chessboard[rowTo][colTo] != opponentColor) {
                println("Invalid Input")
                continue
            }
            if ("$rowTo$colTo" == blackEnPassant) {
                chessboard[rowTo - 1][colTo] = " "
                blackCount--
            }
            if (rowTo - rowFrom == 2) {
                whiteEnPassant = "${rowTo - 1}$colTo"
            }
            if (chessboard[rowTo][colTo] == opponentColor) {
                blackCount--
            }

            blackEnPassant = ""
        }

        if (currentColor == "B") {
            val maxRank = if (rowFrom == 6) 2 else 1

            if (rowFrom <= rowTo || rowFrom - rowTo > maxRank) {
                println("Invalid Input")
                continue
            }
            if (diagonal == 1 && "$rowTo$colTo" != whiteEnPassant && chessboard[rowTo][colTo] != opponentColor) {
                println("Invalid Input")
                continue
            }
            if ("$rowTo$colTo" == whiteEnPassant) {
                chessboard[rowTo + 1][colTo] = " "
                whiteCount--
            }
            if (rowFrom - rowTo == 2) {
                blackEnPassant = "${rowTo + 1}$colTo"
            }
            if (chessboard[rowTo][colTo] == opponentColor) {
                whiteCount--
            }
            whiteEnPassant = ""
        }

        chessboard[rowFrom][colFrom] = " "
        chessboard[rowTo][colTo] = currentColor
        printChessboard(chessboard)

        if (blackCount == 0 || (currentPlayer == firstPlayer && rowTo == 7)) {
            println("White Wins!")
            break
        }

        if (whiteCount == 0 || (currentPlayer == secondPlayer && rowTo == 0)) {
            println("Black Wins!")
            break
        }

        currentColor = if (currentPlayer == firstPlayer) "B" else "W"
        opponentColor = if (currentColor == "B") "W" else "B"
        currentPlayer = if (currentPlayer == firstPlayer) secondPlayer else firstPlayer

        if (isStalemate(chessboard, currentColor, opponentColor)) {
            println("Stalemate!")
            break
        }

    }

    println("Bye!")
}

fun printChessboard(chessboard: Array<Array<String>>) {
    var str = ""
    for (i in chessboard.lastIndex downTo 0) {
        str += "  +---+---+---+---+---+---+---+---+\n"
        str += "${i + 1} | ${chessboard[i].joinToString(" | ")} |\n"
    }
    str += "  +---+---+---+---+---+---+---+---+\n"
    str += "    a   b   c   d   e   f   g   h"
    println(str)
}

fun isStalemate(chessboard: Array<Array<String>>, color: String, opponent: String): Boolean {
    val direction = if (color == "W") 1 else -1

    for (i in 1 until chessboard.lastIndex) {
        for (k in 0 .. chessboard[i].lastIndex) {
            if (chessboard[i][k] == color) {
                when {
                    chessboard[i + direction][k] == " " -> return false
                    k != 0 && chessboard[i + direction][k - 1] == opponent -> return false
                    k < 7 && chessboard[i + direction][k + 1] == opponent -> return false
                }
            }
        }
    }
    return true
}