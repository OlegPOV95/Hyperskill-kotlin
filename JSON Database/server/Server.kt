package jsondatabase.server

import java.io.DataInputStream
import java.io.DataOutputStream
import java.io.File
import java.net.InetAddress
import java.net.ServerSocket
import java.net.Socket
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors
import java.util.concurrent.locks.ReadWriteLock
import java.util.concurrent.locks.ReentrantReadWriteLock
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.google.gson.JsonElement
import com.google.gson.JsonObject

fun main() {
    val database = Database()
    val executor: ExecutorService = Executors.newCachedThreadPool()
    val serverAddress = "127.0.0.1"
    val serverPort = 23456
    try {
        ServerSocket(serverPort, 50, InetAddress.getByName(serverAddress)).use { server ->
            println("Server started!")
            while (!server.isClosed) {
                executor.submit(Session(server.accept(), server, executor, database))
            }
        }
    } catch (e: Exception) {
        e.printStackTrace()
    }
}

class Session(
    private val socket: Socket,
    private val server: ServerSocket,
    private val executor: ExecutorService,
    private val database: Database): Runnable {

    override fun run() {
        DataInputStream(socket.getInputStream()).use { input ->
            DataOutputStream(socket.getOutputStream()).use { output ->
                try {
                    val request = Gson().fromJson(input.readUTF(), JsonObject::class.java)
                    if (request.get("type").asString == "exit") {
                        output.writeUTF(Response("OK").toJson())
                        executor.shutdown()
                        server.close()
                    } else {
                        output.writeUTF(database.query(request))
                    }
                } catch (e: Exception) {
                    output.writeUTF(Response("ERROR").toJson())
                }
            }
        }
    }
}

data class Response(val response: String, val value: JsonElement? = null, val reason: String? = null) {
    fun toJson(): String = Gson().toJson(this)
}

class Database {
    private val databaseFile = File("src/jsondatabase/server/data/db.json")
    private val errorMsg = "ERROR"
    private val successMsg = "OK"
    private val lock: ReadWriteLock = ReentrantReadWriteLock()
    private val gson = GsonBuilder().setPrettyPrinting().create()

    fun query(request: JsonObject): String {
        val keys = getKeyList(request["key"])
        return when (request["type"].asString) {
            "get" -> get(keys)
            "set" -> set(keys, request["value"])
            "delete" -> delete(keys)
            else -> Response(errorMsg, reason = "Invalid command").toJson()
        }
    }

    fun get(keys: List<String>): String {
        var dataObject = readDatabaseFile()
        keys.forEach { key ->
            val innerObject = dataObject.get(key) ?: return Response(errorMsg, reason = "No such key").toJson()
            if (key != keys[keys.lastIndex]) dataObject = innerObject.asJsonObject
        }
        return Response(successMsg, dataObject[keys[keys.lastIndex]]).toJson()
    }

    private fun set(keys: List<String>, value: JsonElement): String {
        lock.writeLock().lock()
        val db = readDatabaseFile()
        var dataObject = db
        for (i in 0 until keys.lastIndex) {
            if (keys[i] !in dataObject.keySet()) {
                dataObject.add(keys[i], JsonObject())
            }
            dataObject = dataObject.get(keys[i]).asJsonObject
        }
        dataObject.add(keys[keys.lastIndex], value)
        databaseFile.writeText(gson.toJson(db))
        lock.writeLock().unlock()

        return Response(successMsg).toJson()
    }

    private fun delete(keys: List<String>): String {
        lock.writeLock().lock()
        val db = readDatabaseFile()
        var dataObject = db
        keys.forEach { key ->
            val innerObject = dataObject.get(key) ?: return Response(errorMsg, reason = "No such key").toJson()
            if (key != keys[keys.lastIndex]) {
                dataObject = innerObject.asJsonObject
            }
        }
        dataObject.remove(keys[keys.lastIndex])
        databaseFile.writeText(gson.toJson(db))
        lock.writeLock().unlock()
        return Response(successMsg).toJson()
    }

    private fun readDatabaseFile(): JsonObject {
        return try {
            lock.readLock().lock()
            val json = databaseFile.readText()
            lock.readLock().unlock()
            gson.fromJson(json, JsonObject::class.java)
        } catch (e:Exception) {
            JsonObject()
        }
    }

    private fun getKeyList(key: JsonElement): List<String> {
        return if (key.isJsonArray) {
            key.asJsonArray.map { it.asString }.toList()
        } else {
            listOf(key.asString)
        }
    }
}

