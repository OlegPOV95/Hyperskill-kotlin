package blockchain

import java.lang.Thread.sleep
import java.security.MessageDigest
import java.util.concurrent.TimeUnit
import java.util.concurrent.locks.ReentrantReadWriteLock

val blockChain = BlockChain()

fun main() {
    val names = listOf("Jack","James","Daniel","Harry","Charlie","Ethan","Matthew","Ryen","Riley","Noah","Sophie",
        "Emily","Grace","Amelia","Jessica","Lucy","Sophia","Katie","Eva","Aoife")


    val miners = List(15) { MinerThread(it.toString()) }
    miners.forEach { it.start() }

    repeat(100) {
        val msg = names.random() + " sent " + (10..1000).random() + " VC to " + names.random()
        blockChain.send(msg)
        sleep(50)
    }


    miners.forEach { it.join() }
    blockChain.printAll()
}

class MinerThread(val num: String): Thread() {
    override fun run() {
        val startTime = System.currentTimeMillis()
        while (true) {
            val id = blockChain.tail?.id?.plus(1) ?: 1
            val prev = blockChain.tail
            val previousHash = prev?.hash ?: "0"
            val range = 0..2147483647
            var magicNumber: Int
            var hash: String

            while (true) {
                magicNumber = range.random()
                hash = hash256("$id$previousHash$magicNumber")
                if (hash.startsWith("0".repeat(blockChain.zerosCount))) {
                    val timestamp = System.currentTimeMillis()
                    val generatedTime = TimeUnit.MILLISECONDS.toSeconds(timestamp - startTime).toInt()
                    val block = Block(id, prev, hash, timestamp, magicNumber, generatedTime, num)
                    if (blockChain.add(block)) {
                        return
                    }
                    break
                }
            }
        }
    }
}

class Block(
    val id: Int,
    val prev: Block?,
    val hash: String,
    val timestamp: Long,
    val magicNumber: Int,
    val generatedTime: Int,
    val minerNum: String,
    var message: String = "",
    var nStatus: String = ""
)

class BlockChain {
    var head: Block? = null
    var tail: Block? = null
    var zerosCount = 0

    private val lock = ReentrantReadWriteLock()
    private val messages: MutableList<String> = mutableListOf()

    @Synchronized
    fun add(block: Block): Boolean {
        if (tail == null) {
            if (block.prev != null) {
                return false
            }
            head = block
            tail = head
        } else {
            if (block.prev == null || block.prev.hash != tail!!.hash || !block.hash.startsWith("0".repeat(zerosCount))) {
                return false
            }
            tail = block

            sleep(100)

            if (messages.isNotEmpty()) {
                tail!!.message = messages.joinToString("\n")
                lock.writeLock().lock()
                messages.clear()
                lock.writeLock().unlock()
            }
        }
        updateComplexity()
        return true
    }

    fun send(msg: String) {
        lock.writeLock().lock()
        messages.add(msg)
        lock.writeLock().unlock()
    }

    private fun updateComplexity() {
        val generatedTime = tail?.generatedTime ?: 0
        if (zerosCount == 2) {
            tail?.nStatus = "N stays the same"
            return
        }
        when {
            generatedTime > 3 -> {
                zerosCount--
                tail?.nStatus = "N was decreased by 1"
            }
            generatedTime > 1 -> tail?.nStatus = "N stays the same"
            else -> {
                zerosCount++
                tail?.nStatus = "N was increased to $zerosCount"
            }
        }
    }

    fun printAll() {
        val list: MutableList<Block> = mutableListOf()
        var current = tail
        while (true) {
            if (current == null) {
                break
            }
            list.add(current)
            current = current.prev
        }
        for (i in list.lastIndex downTo 0) {
            val message = if (list[i].message.isEmpty()) {
                "Block data: no messages"
            } else {
                "Block data:\n" + list[i].message
            }
            val str = "Block:\n" +
                    "Created by miner # ${list[i].minerNum}\n" +
                    "miner${list[i].minerNum} gets 100 VC\n" +
                    "Id: ${list[i].id}\n" +
                    "Timestamp: ${list[i].timestamp}\n" +
                    "Magic number: ${list[i].magicNumber}\n" +
                    "Hash of the previous block:\n" +
                    "${list[i].prev?.hash ?: "0"}\n" +
                    "Hash of the block:\n" +
                    "${list[i].hash}\n" +
                    "$message\n" +
                    "Block was generating for ${list[i].generatedTime} seconds\n" +
                    "${list[i].nStatus}\n"

            println(str)
        }
    }
}

fun hash256(input: String): String {
    val digest = MessageDigest.getInstance("SHA-256")
    val hash = digest.digest(input.toByteArray(charset("UTF-8")))
    val hexString = StringBuilder()
    for (elem in hash) {
        val hex = Integer.toHexString(0xff and elem.toInt())
        if (hex.length == 1) hexString.append('0')
        hexString.append(hex)
    }
    return hexString.toString()
}