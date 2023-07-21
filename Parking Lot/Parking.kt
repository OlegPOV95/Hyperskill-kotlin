package parking

import java.util.*

data class Parking(private val size: Int=0) {
    private val spots: Array<String> = Array(size) {""}
    private var carCount = 0

    init {
        if (size > 0) {
            println("Created a parking lot with $size spots.")
        }
    }

    fun add(carNum: String, color: String) {
        if (carCount == size) {
            return println("Sorry, the parking lot is full.")
        }
        for (i in 0 .. spots.lastIndex) {
            if (spots[i].isEmpty()) {
                spots[i] = "$carNum $color"
                carCount++
                println("$color car parked in spot ${i + 1}.")
                return
            }
        }
    }

    fun remove(id: Int) {
        if (spots[id] == "") {
            return println("There is no car in spot ${id + 1}.")
        }
        spots[id] = ""
        carCount--
        println("Spot ${id + 1} is free.")
    }

    fun status() {
        if (carCount == 0) {
            return println("Parking lot is empty.")
        }
        for (i in 0..spots.lastIndex) {
            if (spots[i] != "") {
                println("${i + 1} ${spots[i]}")
            }
        }
    }

    fun spotByColor(color: String) {
        val key = color.uppercase()
        val res = mutableListOf<Int>()
        for (i in 0..spots.lastIndex) {
            val car = spots[i].split(" ")
            if (car.size != 2) continue
            if (car[1].uppercase() == key) res += i + 1
        }
        if (res.isEmpty()) {
            println("No cars with color $color were found.")
        } else {
            println(res.joinToString(", "))
        }
    }

    fun spotByReg(regNumber: String) {
        val res = mutableListOf<Int>()
        for (i in 0..spots.lastIndex) {
            val car = spots[i].split(" ")
            if (car.size != 2) continue
            if (car[0] == regNumber) res += i + 1
        }
        println(res.joinToString(", ").ifEmpty { "No cars with registration number $regNumber were found." })
    }

    fun regByColor(color: String) {
        val key = color.uppercase()
        val res = mutableListOf<String>()
        for (i in 0..spots.lastIndex) {
            val car = spots[i].split(" ")
            if (car.size != 2) continue
            if (car[1].uppercase() == key) {
                res += car[0]
            }
        }
        if (res.isEmpty()) {
            println("No cars with color $color were found.")
        } else {
            println(res.joinToString(", "))
        }
    }
}
