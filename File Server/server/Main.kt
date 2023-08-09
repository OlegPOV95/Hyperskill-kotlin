package server

import kotlinx.serialization.*
import kotlinx.serialization.json.Json
import java.io.DataInputStream
import java.io.DataOutputStream
import java.io.File
import java.net.InetAddress
import java.net.ServerSocket
import java.net.Socket
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors
import java.util.concurrent.locks.ReentrantReadWriteLock
import kotlin.system.exitProcess

fun main(args: Array<String>) {
    val address = "127.0.0.1"
    val port = 23456
    val manager = FileManager()
    val executor: ExecutorService = Executors.newCachedThreadPool()
    ServerSocket(port, 50 , InetAddress.getByName(address)).use { server ->
        while (!server.isClosed) {
            executor.submit(Session(server.accept(), executor, server, manager))
        }
    }
}

class Session(
    private val socket: Socket,
    private val executor: ExecutorService,
    private val server: ServerSocket,
    private val manager: FileManager): Runnable {

    override fun run() {
        socket.use { socket ->
            DataInputStream(socket.getInputStream()).use{ input ->
                DataOutputStream(socket.getOutputStream()).use { output ->
                    val request = Json.decodeFromString<Request>(input.readUTF())
                    val response: Response
                    when(request.method) {
                        "PUT" -> {
                            val fileByteArray = ByteArray(input.readInt())
                            input.readFully(fileByteArray, 0, fileByteArray.size)
                            val fileId = manager.put(request.fileName, request.fileExtension, fileByteArray)
                            response = if (fileId != null) {
                                Response(200, request.method, fileId = fileId)
                            } else {
                                Response(403)
                            }
                            output.writeUTF(response.toJson())
                        }
                        "GET" -> {
                            val fileData = manager.getFileData(request.fileId, request.fileName)
                            if (fileData == null) {
                                output.writeUTF(Response(404).toJson())
                                return
                            }
                            val fileByteArray = manager.getByteArray(fileData.name)
                            if (fileByteArray == null) {
                                output.writeUTF(Response(404).toJson())
                                return
                            }
                            output.writeUTF(Response(200, request.method, fileExtension = fileData.extension).toJson())
                            output.writeInt(fileByteArray.size)
                            output.write(fileByteArray)
                        }
                        "DELETE" -> {
                            response = if (manager.delete(request.fileId, request.fileName)) {
                                Response(200)
                            } else {
                                Response(404)
                            }
                            output.writeUTF(response.toJson())
                        }
                        "exit" -> {
                            executor.shutdown()
                            server.close()
                        }
                        else -> output.writeUTF(Response(0).toJson())
                    }
                }
            }
        }
    }
}

class FileManager() {
    private val dirPath = "src/server/data/"
    private val look = ReentrantReadWriteLock()
    private val filesInfo = File("src/server/data/files_name.json")

    fun getByteArray(fileName: String): ByteArray? {
        val file = File(dirPath + fileName)
        return if (file.canRead()) {
            file.readBytes()
        } else {
            null
        }
    }

    fun getFileData(id: Int?, name: String): FileData? {
        val fileDataStorage = getFileDataStorage()
        return if (id == null) {
            fileDataStorage.getByName(name)
        } else {
            fileDataStorage.getById(id)
        }
    }

    fun put(fileName: String, extension: String, fileByteArray: ByteArray): Int? {
        val name = fileName.ifEmpty { getRandomString() + "." + extension }
        val file = File(dirPath + name)
        return if (file.createNewFile()) {
            file.writeBytes(fileByteArray)
            saveFileData(name, extension)
        } else {
            null
        }
    }

    fun delete(id: Int?, name: String): Boolean {
        val fileDataStorage = getFileDataStorage()
        look.writeLock().lock()
        val fileData = if (id == null) {
            fileDataStorage.removeByName(name)
        } else {
            fileDataStorage.removeById(id)
        } ?: return false
        look.writeLock().lock()
        return File(dirPath + fileData.name).delete()
    }

    private fun getRandomString(): String {
        val charPool = ('A'..'Z') + ('a'..'z')
        return List(12) { charPool.random() }.joinToString("")
    }

    private fun getFileDataStorage(): FileDataStorage {
        look.readLock().lock()
        val dataStorage = try {
            Json.decodeFromString<FileDataStorage>(filesInfo.readText())
        } catch (e: Exception) {
            FileDataStorage()
        }
        look.readLock().unlock()
        return dataStorage
    }

    private fun saveFileData(fileName: String, extension: String): Int {
        val id: Int
        look.writeLock().lock()
        try {
            val dataStorage = getFileDataStorage()
            id = dataStorage.add(fileName, extension)
            filesInfo.writeText(dataStorage.toJson())
        } finally {
            look.writeLock().unlock()
        }
        return id
    }
}


@Serializable
data class FileData(
    val name: String,
    val extension: String,
    val id: Int) {
}

@Serializable
class FileDataStorage {
    var index: Int = 0
    val files: MutableMap<Int, FileData> = mutableMapOf()
    val filesIdByName: MutableMap<String, Int> = mutableMapOf()

    fun toJson() = Json.encodeToString(this)

    fun add(name: String, extension: String): Int {
        files[index] =  FileData(name, extension, index)
        filesIdByName[name] = index
        return index++
    }

    fun getById(id: Int): FileData? {
        return files[id]
    }

    fun getByName(name: String): FileData? {
        val id = filesIdByName[name] ?: return null
        return getById(id)
    }

    fun removeById(id: Int): FileData? {
        return files.remove(id)
    }

    fun removeByName(name: String): FileData? {
        val id = filesIdByName[name] ?: return null
        return removeById(id)
    }
}

@Serializable
data class Request(var method: String,
                   var fileName: String = "",
                   var fileId: Int? = null,
                   var fileExtension: String = "") {

    fun toJson() = Json.encodeToString(this)
}

@Serializable
data class Response(val code: Int,
                    var method: String = "",
                    var fileName: String = "",
                    var fileExtension: String = "",
                    var fileId: Int? = null) {

    fun toJson() = Json.encodeToString(this)
}