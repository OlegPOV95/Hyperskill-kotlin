package phonebook

import java.io.File
import kotlin.math.sqrt

fun main() {
    val path = "C:/Users/user/Documents"
    val contacts = File("$path/directory.txt").readLines()
    val keysForFind = File("$path/find.txt").readLines()

    // Linear search block
    println("Start searching (linear search)...")
    System.currentTimeMillis().run {
        val searchResult = linearSearch(keysForFind, contacts)
        val time = getTimeString(System.currentTimeMillis() - this)
        println("Found $searchResult / ${keysForFind.size} entries. Time taken: $time\n")
    }

    // Bubble sort + jump search block
    println("Start searching (bubble sort + jump search)...")
    System.currentTimeMillis().run {
        // Bubble sort
        var bubbleSortWasStopped = false
        val data = bubbleSortWithTimeLimit(contacts.toMutableList(), 10000L)
            .run {
                if (this == null) {
                    bubbleSortWasStopped = true
                    contacts
                } else {
                    this
                }
            }
        val sortDuration = System.currentTimeMillis() - this
        // Search
        val searchStartTime = System.currentTimeMillis()
        val searchResult = if (bubbleSortWasStopped) {
            linearSearch(keysForFind, data)
        } else {
            jumpSearch(keysForFind, data)
        }
        val searchDuration = System.currentTimeMillis() - searchStartTime

        println("Found $searchResult / ${keysForFind.size} entries. Time taken: ${getTimeString(System.currentTimeMillis() - this)}\n" +
                "Sorting time: ${getTimeString(sortDuration)}" +
                if (bubbleSortWasStopped) {" - STOPPED, moved to linear search\n"} else {"\n"} +
                "Searching time: ${getTimeString(searchDuration)}\n")
    }

    // Quick sort + binary search block
    println("Start searching (quick sort + binary search)...")
    System.currentTimeMillis().run {
        val data = qSort(contacts)
        val sortDuration = System.currentTimeMillis() - this
        // Binary Search
        val searchStartTime = System.currentTimeMillis()
        val searchResult = bSearch(keysForFind, data)
        val searchDuration = System.currentTimeMillis() - searchStartTime

        println("Found $searchResult / ${keysForFind.size} entries. Time taken: ${getTimeString(System.currentTimeMillis() - this)}\n" +
                "Sorting time: ${getTimeString(sortDuration)}\n" +
                "Searching time: ${getTimeString(searchDuration)}\n")
    }

    // Hash table
    println("Start searching (hash table)...")
    System.currentTimeMillis().run {
        val table = contacts.associate { it.substringAfter(" ") to it.substringBefore(" ") }
        val tableCreateTime = System.currentTimeMillis() - this
        val searchStartTime = System.currentTimeMillis()
        var searchResult = 0
        keysForFind.forEach { if (table.containsKey(it)) searchResult++}
        val searchDuration = System.currentTimeMillis() - searchStartTime
        println("Found $searchResult / ${keysForFind.size} entries. Time taken: ${getTimeString(System.currentTimeMillis() - this)}\n" +
                "Creating time: ${getTimeString(tableCreateTime)}\n" +
                "Searching time: ${getTimeString(searchDuration)}")
    }
}

fun linearSearch(keys: List<String>, list: List<String>): Int {
    var count = 0
    keys.forEach { key ->
        for (record in list) {
            if (record.substringAfter(" ") == key) {
                count++
                break
            }
        }
    }
    return count
}

fun jumpSearch(keys: List<String>, list: List<String>): Int {
    val stepSize = sqrt(list.size.toDouble()).toInt()
    var count = 0
    keys.forEach { key ->
        var blockStart = 0
        jumpLoop@ for (i in list.indices step stepSize) {
            if (list[i].substringAfter(" ") > key) {
                for (k in blockStart..i) {
                    if (list[k].substringAfter(" ") == key) {
                        count++
                        break@jumpLoop
                    }
                }
            }
            blockStart = i
        }
    }
    return count
}

fun getTimeString(time: Long): String {
    return String.format("%1\$tM min. %1\$tS sec. %1\$tL ms.", time)
}

fun bubbleSortWithTimeLimit(list: MutableList<String>, limitTimeMillis: Long = 50000L): MutableList<String>?{
    var swapped: Boolean
    var next: String
    var current: String
    var temp: String
    val startTime = System.currentTimeMillis()
    for (i in list.indices) {
        swapped = false
        for (k in 0 until list.lastIndex - i) {
            current = list[k].substringAfter(" ")
            next = list[k+1].substringAfter(" ")
            if (current > next) {
                temp = list[k]
                list[k] = list[k+1]
                list[k+1] = temp
                swapped = true
            }
        }
        if (!swapped) {
            break
        }
        if (System.currentTimeMillis()  - startTime > limitTimeMillis) {
            return null
        }
    }
    return list
}

fun qSort(data: List<String>): List<String> {
    if (data.isEmpty()) {
        return data
    }
    val pivot = data[data.size / 2]
    val low = qSort(data.filter { it.substringAfter(" ") < pivot.substringAfter(" ") })
    val high = qSort(data.filter { it.substringAfter(" ") > pivot.substringAfter(" ") })
    return low + pivot + high
}

fun bSearch(keys: List<String>, list: List<String>): Int {
    var count = 0
    keys.forEach { key ->
        var low = 0
        var high = list.lastIndex
        while (low <= high) {
            val midIndex = low + (high - low) / 2
            val name = list[midIndex].substringAfter(" ")
            if (key > name) {
                low = midIndex + 1
            } else if (key < name) {
                high = midIndex - 1
            } else {
                count++
                break
            }
        }
    }
    return count
}