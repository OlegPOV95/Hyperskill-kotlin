package parking

fun main() {

    var parking: Parking? = null
    while(true) {
        val input = readln().split(" ")
        if (input[0] == "exit") {
            break
        }
        if (parking == null && input[0] != "create") {
            println("Sorry, a parking lot has not been created.")
            continue
        }
        try {
            when (input[0]) {
                "create" -> parking = Parking(input[1].toInt())
                "park"   -> parking?.add(input[1], input[2])
                "leave"  -> parking?.remove(input[1].toInt() - 1)
                "status" -> parking?.status()
                "spot_by_color" -> parking?.spotByColor(input[1])
                "spot_by_reg" -> parking?.spotByReg(input[1])
                "reg_by_color" -> parking?.regByColor(input[1])
            }
        } catch (e: Exception) {
            println("Something went wrong, try again")
        }

    }
}
