package minesweeper
import kotlin.random.Random

class Minesweeper {
    private val fieldSize: Int = 9
    private val cellCount: Int = fieldSize * fieldSize
    private val mineField: MutableList<String> = MutableList(cellCount) {"."}
    private val minePosition: MutableList<Int> = mutableListOf()
    private val mineMarkList: MutableList<Int> = mutableListOf()
    private var gameIsOver: Boolean = false

    fun game() {
        println("How many mines do you want on the field?")
        val mineCount: Int = readln().toInt()
        while (minePosition.size < mineCount) {
            val num = Random.nextInt(0, cellCount)
            if (num !in minePosition)
                minePosition.add(num)
        }
        while (!gameIsOver) {
            printMinefield()
            println("Set/unset mines marks or claim a cell as free: ")
            val command: List<String> = parseCommand(readln())
            if (command.contains("error")) {
                println("Invalid command")
                continue
            }
            val point = command[1].toInt()
            if (mineField[point] !in listOf(".", "*")) {
                println("Invalid command")
                continue
            }
            when (command[0]) {
                "mine" -> markMine(point)
                "free" -> {
                    if (point in minePosition) {
                        for (i in minePosition)
                            mineField[i] = "X"
                        printMinefield()
                        println("You stepped on a mine and failed!")
                        gameIsOver = true
                        break
                    }
                    openCell(point)
                }
            }
            if (mineField.count{ e -> e == "."} == mineCount) {
                gameIsOver = true
                printMinefield()
                println("Congratulations! You found all the mines!")
            }
        }
    }


    private fun parseCommand(input: String): List<String> {
        val commandMatch: MatchResult = "(^\\d \\d )(\\bmine|free\\b)".toRegex().find(input) ?: return listOf("error")
        val command: String = commandMatch.groupValues[2]
        var point: String = commandMatch.groupValues[1].replace(" ", "")
        point = ((point[1].digitToInt() * fieldSize - fieldSize) + point[0].digitToInt() - 1).toString()
        return listOf(command, point)
    }

    private fun markMine(point: Int) {

        if (point !in mineMarkList) {
            mineMarkList.add(point)
            mineField[point] = "*"
        } else {
            mineMarkList.remove(point)
            mineField[point] = "."
        }

        if (mineMarkList.size == minePosition.size) {
            if (mineMarkList.count {e -> minePosition.contains(e)} == minePosition.size) {
                gameIsOver = true
                printMinefield()
                println("Congratulations! You found all the mines!")
            }
        }
    }

    private fun openCell(point: Int, deep: Int = 1) {
        val neighboringCells: MutableList<Int> = addNeighboringCellsToList(point)

        if (point - fieldSize >= 0)
            neighboringCells += addNeighboringCellsToList(point - fieldSize)
        if (point + fieldSize < cellCount)
            neighboringCells += addNeighboringCellsToList(point + fieldSize)

        val mineCount = neighboringCells.count { e -> minePosition.contains(e)}
        if (point !in minePosition)
            mineField[point] = if (mineCount > 0) mineCount.toString() else "/"
        if (mineCount == 0 || deep > 0)
            for (i in neighboringCells)
                if (mineField[i] != "/")
                    openCell(i, deep - 1)
    }

    private fun addNeighboringCellsToList(point: Int): MutableList<Int> {
        val list: MutableList<Int> = mutableListOf(point)
        if (point - 1 >= point / fieldSize * fieldSize)
            list.add(point - 1)
        if (point + 1 < point / fieldSize * fieldSize + fieldSize)
            list.add(point + 1)
        return list

    }

    private fun printMinefield() {
        var field = " │123456789│\n—│—————————│\n1│"
        repeat(cellCount) {
            if (it > 0 && it % fieldSize == 0) {
                field += "│\n${(it+9)/9}│"
            }
            field += mineField[it]
        }
        field += "|\n—│—————————│"
        println(field)
    }
}

fun main() {
    val minesweeper = Minesweeper()
    minesweeper.game()
}
