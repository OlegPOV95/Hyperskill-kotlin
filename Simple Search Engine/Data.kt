package search

import java.io.File

class Data(path: String) {
    private val dataList: List<String>
    private val indexMap: Map<String, MutableList<Int>>

    init {
        dataList = File(path).readLines()
        indexMap = getIndexMap(dataList)
    }

    fun find() {
        println("Select a matching strategy: ALL, ANY, NONE")
        val strategy = readln()
        println("Enter a name or email to search all matching people.")
        val query = readln()
        when (strategy) {
            "ALL" -> findAll(query)
            "ANY" -> findAny(query)
            "NONE" -> findNone(query)
        }
    }

    fun printAll() {
        println("\n=== List of people ===")
        dataList.forEach(::println)
    }

    private fun findAll(query: String) {
        val result = mutableListOf<Int>()
        val queryWords = query.split(" ")
        val wordsIndices = findWordsIndices(query)

        if (wordsIndices.size < queryWords.size) {
            return printSearchResult(result)
        }
        if (wordsIndices.size == 1) {
            return printSearchResult(wordsIndices[0])
        }
        wordsIndices[0].forEach {
            var contain = true
            for (i in 1..wordsIndices.size) {
                if (!wordsIndices[i].contains(it)) {
                    contain = false
                    break
                }
            }
            if (contain) {
                result.add(it)
            }
        }
        printSearchResult(result)
    }

    private fun findAny(query: String) {
        val result = mutableSetOf<Int>()
        val wordsIndices = findWordsIndices(query)
        if (wordsIndices.isEmpty()) {
            return printSearchResult(result)
        }
        if (wordsIndices.size == 1) {
            return printSearchResult(wordsIndices[0])
        }
        wordsIndices.forEach { list ->
            list.forEach {
                result.add(it)
            }
        }
        printSearchResult(result)

    }

    private fun findNone(query: String) {
        val result = mutableSetOf<Int>()
        val wordsIndices = findWordsIndices(query)
        val ignoreIndices = mutableSetOf<Int>()
        wordsIndices.forEach { ignoreIndices += it }
        println(wordsIndices)
        for (i in 0..dataList.lastIndex) {
            if (ignoreIndices.contains(i)) {
                continue
            }
            result.add(i)
        }
        printSearchResult(result)
    }

    private fun findWordsIndices(query: String): List<MutableList<Int>> {
        if (query.isEmpty()) {
            return emptyList()
        }
        val words = query.split(" ").map { it.lowercase() }
        return indexMap.filterKeys { it in words }.values.toList()
    }

    private fun printSearchResult(result: Collection<Int>) {
        if (result.isEmpty()) {
            return println("No matching people found.")
        }
        println("\n${result.size} persons found:")
        result.forEach {
            println(dataList[it])
        }
    }

    private fun getIndexMap(list: List<String>): Map<String, MutableList<Int>> {
        val map = mutableMapOf<String, MutableList<Int>>()
        for (i in 0..list.lastIndex) {
            list[i]
                .split(" ")
                .forEach {
                    val key = it.lowercase()
                    if (map.containsKey(key)) map[key]?.add(i)
                    else map[key] = mutableListOf(i)
                }
        }
        return map.toMap()
    }
}
