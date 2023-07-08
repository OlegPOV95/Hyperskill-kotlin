package machine

class CoffeeMachine(water: Int = 400, milk: Int = 540, coffee: Int = 120, cups: Int = 9,money: Int = 550) {
    private var water = water
    private var milk  = milk
    private var coffee = coffee
    private var cups = cups
    private var money = money

    public fun start() {
        while (true) {
            println("Write action (buy, fill, take, remaining, exit):")
            when (readLine()!!.toString()) {
                "buy"       -> buy()
                "fill"      -> fill()
                "take"      -> take()
                "remaining" -> status()
                "exit"      -> break
            }
        }
    }
    private fun status() {
        println("""
                The coffee machine has:
                $water ml of water
                $milk ml of milk
                $coffee g of coffee beans
                $cups disposable cups
                $$money of money
            """.trimIndent())
        println("")
    }
    private fun reduce(water: Int ,milk: Int, coffee: Int, cups: Int, money: Int) {
        when {
            this.water  < water  -> println("Sorry, not enough water!")
            this.milk   < milk   -> println("Sorry, not enough milk!")
            this.coffee < coffee -> println("Sorry, not enough coffee!")
            this.cups   < cups   -> println("Sorry, not enough cups!")
            else -> {
                this.water  -= water
                this.milk   -= milk
                this.coffee -= coffee
                this.cups   -= cups
                this.money  += money
                println("I have enough resources, making you a coffee!")
            }
        }
    }
    private fun fill () {
        println("Write how many ml of water you want to add:")
        water += readLine()!!.toInt()
        println("Write how many ml of milk you want to add:")
        milk += readLine()!!.toInt()
        println("Write how many grams of coffee beans you want to add:")
        coffee += readLine()!!.toInt()
        println("Write how many disposable cups you want to add:")
        cups += readLine()!!.toInt()
    }
    private fun buy() {
        println("What do you want to buy? 1 - espresso, 2 - latte, 3 - cappuccino, back - to main menu:")
        val select: String = readLine()!!.toString()
        when(select) {
            "1" -> reduce(250, 0, 16, 1, 4)  // Espresso
            "2" -> reduce(350, 75, 20, 1, 7) // Latte
            "3" -> reduce(200, 100, 12, 1, 6) // Cappuccino
            "back" -> return
        }
    }
    private fun take() {
        println("I gave you $$money")
        money = 0
    }
}


fun main() {
    val machine = CoffeeMachine()
    machine.start()
}
