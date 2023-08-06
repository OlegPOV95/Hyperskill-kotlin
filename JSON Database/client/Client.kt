package jsondatabase.client

import com.google.gson.Gson
import java.io.DataInputStream
import java.io.DataOutputStream
import java.io.File
import java.net.InetAddress
import java.net.Socket

fun main(args: Array<String>) {
    val request = buildRequest(args)
    val serverAddress = "127.0.0.1"
    val serverPort = 23456

    Socket(InetAddress.getByName(serverAddress), serverPort).use { socket ->
        println("Client started!")
        DataOutputStream(socket.getOutputStream()).use { output ->
            DataInputStream(socket.getInputStream()).use { input ->
                output.writeUTF(request)
                println("""Sent: $request""")
                println("Received: ${input.readUTF()}")
            }
        }
    }
}

fun buildRequest(args: Array<String>): String {
    if (args[0] == "-in") {
        val file = File("src/jsondatabase/client/data/${args[1]}")
        return file.readText()
    }
    val request = Request(args[1])
    if (args.size >= 4) request.key = args[3]
    if (args.size >= 6) request.value = args[5]
    return Gson().toJson(request)
}

data class Request(val type: String, var key: String = "", var value: String = "")