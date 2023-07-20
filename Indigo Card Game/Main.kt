package indigo

fun main() {
    println("Indigo Card Game")

    val game = Game()
    game.takeCardsFromDeck("table", 4)
    game.takeCardsFromDeck("computer", 6)
    game.takeCardsFromDeck("player", 6)

    println("Initial cards on the table: ${game.table.joinToString(" ")}")
    while (true) {
        when (game.table.size) {
            0 -> println("\nNo cards on the table")
            52 -> break
            else -> println("\n${game.table.size} cards on the table, and the top card is ${game.table.last()}")
        }

        if (game.userCards.isEmpty() && game.computerCards.isEmpty() && game.cardsDeck.isEmpty()) {
            game.end()
            break
        }

        when (0) {
            game.userCards.size -> game.takeCardsFromDeck("player", 6)
            game.computerCards.size -> game.takeCardsFromDeck("computer", 6)
        }

        val cardId: Int = if (game.activePlayer == "player") game.userCard() else game.computerCard()
        if (cardId < 0) {
            break
        }

        game.turn(cardId)
        game.togglePlayer()
    }
    println("Game Over")
}
