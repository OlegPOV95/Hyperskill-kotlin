package client

import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.Json.Default.decodeFromString
import kotlinx.serialization.json.JsonObject
import server.Request
import server.Response
import java.io.DataInputStream
import java.io.DataOutputStream
import java.io.File
import java.net.InetAddress
import java.net.Socket

fun main(array: Array<String>) {
    val address = "127.0.0.1"
    val port = 23456
    val request: Request
    var fileByteArray: ByteArray? = null

    println("Enter action (1 - get a file, 2 - create a file, 3 - delete a file):")
    val method = when(readln()) {
        "1" -> "GET"
        "2" -> "PUT"
        "3" -> "DELETE"
        "exit" ->"exit"
        else -> throw Exception("Invalid action")
    }
    request = Request(method)
    if (request.method != "exit") {
        if (request.method == "PUT") {
            println("Enter name of the file:")
            val fileName = readln()
            val file = File("src/client/data/$fileName")
            if (file.canRead()) {
                request.fileExtension = file.extension
                fileByteArray = file.readBytes()
            } else {
                throw Exception("File not found or can't read")
            }

            println("Enter name of the file to be saved on server:")
            request.fileName = readln()
            if (request.fileName.contains(" ")) {
                throw Exception("Invalid file name")
            }
        } else {
            println("Do you want to ${method.lowercase()} the file by name or by id (1 - name, 2 - id):")
            when (readln()) {
                "1" -> {
                    println("Enter name of the file:")
                    request.fileName = readln()
                }
                "2" -> {
                    println("Enter id:")
                    request.fileId = readln().toInt()
                }
            }
        }
    }

    Socket(InetAddress.getByName(address), port).use { socket ->
        DataOutputStream(socket.getOutputStream()).use { output ->
            DataInputStream(socket.getInputStream()).use { input ->
                output.writeUTF(request.toJson())
                if (request.method == "PUT" && fileByteArray != null) {
                    output.writeInt(fileByteArray.size)
                    output.write(fileByteArray)
                }
                println("The request was sent.")
                if (request.method == "exit") {
                    return
                }
                val response = Json.decodeFromString<Response>(input.readUTF())
                if (response.code == 200 && response.method == "GET") {
                    val inputByteArray = ByteArray(input.readInt())
                    input.readFully(inputByteArray, 0, inputByteArray.size)
                    saveFile(inputByteArray)
                }
                printMessage(response)
            }
        }
    }
}

fun printMessage(response: Response) {
    when(response.code) {
        404 -> println("The response says that this file is not found!")
        403 -> println("The response says that creating the file was forbidden!")
        200 -> {
            when(response.method) {
                "GET" -> println("File saved on the hard drive!")
                "PUT" -> println("Response says that file is saved! ID = ${response.fileId}")
                "DELETE" -> println("The response says that this file was deleted successfully!")
            }
        }
        else -> println("Response code: ${response.code}")
    }
}

fun saveFile(fileByteArray: ByteArray) {
    println("The file was downloaded! Specify a name for it:")
    val fileName = readln()
    val file = File("src/client/data/$fileName")
    if (file.createNewFile()) {
        file.writeBytes(fileByteArray)
    } else {
        throw Exception("Cannot save file")
    }
}