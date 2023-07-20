package indigo

class Game {

    val table: MutableList<String> = mutableListOf()
    val userCards: MutableList<String> = mutableListOf()
    val computerCards: MutableList<String> = mutableListOf()
    var activePlayer: String = firstTurn()
    val cardsDeck: MutableList<String> = getCardStack()
    private var lastWin: String = ""
    private val userWinedCards: MutableList<String> = mutableListOf()
    private val computerWinedCards: MutableList<String> = mutableListOf()
    private val score: MutableMap<String, Int> = mutableMapOf("player" to 0, "computer" to 0)

    fun takeCardsFromDeck(target: String, count: Int) {
        for (i in cardsDeck.lastIndex downTo cardsDeck.lastIndex - count + 1) {
            if (i < 0){
                return
            }
            when(target) {
                "table" -> table.add(cardsDeck[i])
                "player" -> userCards.add(cardsDeck[i])
                "computer" -> computerCards.add(cardsDeck[i])
                else -> return
            }
            cardsDeck.removeAt(i)
        }
    }

    fun turn(cardId: Int) {
        if (table.size == 0) {
            return moveCardById(activePlayer, cardId)
        }

        val card = getCardById(activePlayer, cardId)
        val cardRank = card.slice(0 until card.lastIndex)
        val tableTopCard = table.last()
        val tableTopCardRank = tableTopCard.slice(0 until tableTopCard.lastIndex)

        moveCardById(activePlayer, cardId)

        if (cardRank == tableTopCardRank || card.last() == tableTopCard.last()) {
            when(activePlayer) {
                "player" -> userWinedCards += table
                "computer" -> computerWinedCards += table
            }
            table.clear()
            recountScore()
            lastWin = activePlayer
            println("${activePlayer.replaceFirstChar {it.titlecase()}} wins cards")
            printScore()
        }
    }

    fun userCard(): Int {
        println("Cards in hand: " + printCard(userCards))
        while (true) {
            println("Choose a card to play (1-${userCards.size}):")
            val input = readln().trim()
            if (input == "exit") {
                return -1
            }
            if (!Regex("[1-9]").matches(input)) {
                continue
            }
            val cardId = input.toInt() - 1
            if (cardId !in 0..userCards.lastIndex) {
                continue
            }
            return cardId
        }
    }

    fun computerCard(): Int {
        println(computerCards.joinToString(" "))
        val suits = mutableListOf<Char>()
        val ranks = mutableListOf<String>()

        for (i in computerCards) {
            suits.add(i.last())
            ranks.add(i.slice(0 until i.lastIndex))
        }

        if (table.isNotEmpty()) {
            val tableTopCard = table.last()
            val winSuit: Char = tableTopCard.last()
            val winRank: String = tableTopCard.slice(0 until tableTopCard.lastIndex)

            val winSuitCount = suits.count { it == winSuit }
            val winRankCount = ranks.count { it == winRank }

            if (winSuitCount != 0 || winRankCount != 0) {
                val cardId = if (winRankCount > winSuitCount) ranks.indexOf(winRank) else suits.indexOf(winSuit)
                println("Computer plays ${computerCards[cardId]}")
                return cardId
            }
        }

        val suitsGrouped: List<Pair<Char, Int>> = suits.groupingBy { it }.eachCount().toList().sortedBy { (_, v) -> v }
        val ranksGrouped: List<Pair<String, Int>> = ranks.groupingBy { it }.eachCount().toList().sortedBy { (_, v) -> v }
        val suitMax: Char = suitsGrouped.last().first
        val rankMax: String = ranksGrouped.last().first
        val suitMaxCount: Int = suitsGrouped.last().second
        val rankMaxCount: Int = ranksGrouped.last().second
        val cardId = if (suitMaxCount >= rankMaxCount) suits.indexOf(suitMax) else ranks.indexOf(rankMax)
        println("Computer plays ${computerCards[cardId]}")
        return cardId
    }

    fun togglePlayer() {
        when(activePlayer) {
            "player" -> activePlayer = "computer"
            "computer" -> activePlayer = "player"
        }
    }

    fun end() {
        when(lastWin) {
            "computer" -> computerWinedCards += table
            "player" -> userWinedCards += table
        }
        recountScore(true)
        printScore()
    }

    private fun moveCardById(source: String, id: Int) {
        when(source) {
            "player" -> table.add(userCards.removeAt(id))
            "computer" -> table.add(computerCards.removeAt(id))
        }
    }

    private fun printScore() {
        println("Score: Player ${score["player"]} - Computer ${score["computer"]}")
        println("Cards: Player ${userWinedCards.size} - Computer ${computerWinedCards.size}")

    }

    private fun recountScore(gameEnd: Boolean = false) {
        var userScore = 0
        var computerScore = 0
        val ranks = listOf("A", "10", "J", "Q", "K")
        for (i in userWinedCards) {
            val rank = if (i.length == 2) i.substring(0, 1) else i.substring(0, 2)
            if (rank in ranks) userScore++
        }
        for (i in computerWinedCards) {
            val rank = if (i.length == 2) i.substring(0, 1) else i.substring(0, 2)
            if (rank in ranks) computerScore++
        }
        if (gameEnd) {
            if (userWinedCards.size >= computerWinedCards.size) {
                userScore += 3
            } else {
                computerScore += 3
            }
        }
        score["player"] = userScore
        score["computer"] = computerScore
    }

    private fun getCardStack(): MutableList<String> {
        val ranks = listOf("A", "2", "3", "4", "5", "6", "7", "8", "9", "10", "J", "Q", "K")
        val suits = listOf("♠", "♥", "♦", "♣")
        val cards = mutableListOf<String>()
        for (i in ranks) {
            for (k in suits) {
                cards.add(i + k)
            }
        }
        return cards.shuffled().toMutableList()
    }

    private fun firstTurn(): String {
        while (true) {
            println("Play first?")
            when(readln()) {
                "yes" -> return "player"
                "no" -> return "computer"
            }
        }
    }

    private fun getCardById(source: String, id: Int): String {
        when(source) {
            "table" -> return table[id]
            "player" -> return userCards[id]
            "computer" -> return computerCards[id]
        }
        return ""
    }

    private fun printCard(list: List<String>): String {
        var res = ""
        for (i in 0 .. list.lastIndex) {
            res += "${i + 1})${list[i]} "
        }
        return res.trim()
    }
}