package svcs
import java.io.File
import java.security.MessageDigest


fun main(args: Array<String>) {

    val mainDir = File("vcs/")
    if (!mainDir.exists())
        createSystemCatalog()

    if (args.isEmpty())
        return printHelpPage()

    when(args[0]) {
        "--help" -> printHelpPage()
        "config" -> config(args)
        "add" -> add(args)
        "log" -> log()
        "commit" -> commit(args)
        "checkout" -> checkout(args)
        else -> println("'${args[0]}' is not a SVCS command.")
    }
}

fun createSystemCatalog() {
    val parentDir = File("vcs/").mkdir()
    val commits = File("vcs/commits/").mkdir()
    val config = File("vcs/config.txt").createNewFile()
    val index = File("vcs/index.txt").createNewFile()
    val log = File("vcs/log.txt").createNewFile()
}

fun printHelpPage() {
    println("""
        These are SVCS commands:
        config     Get and set a username.
        add        Add a file to the index.
        log        Show commit logs.
        commit     Save changes.
        checkout   Restore a file.
    """.trimIndent())
}

fun config(args: Array<String>) {
    val file = File("vcs/config.txt")
    if (args.size == 1) {
        if (file.readText().isEmpty()) {
            return println("Please, tell me who you are.")
        }
        return println("The username is ${file.readText()}.")
    }

    file.writeText(args[1])
    println("The username is ${file.readText()}.")
}

fun add(args: Array<String>) {
    val index = File("vcs/index.txt")
    if (args.size == 1 && index.readText().isEmpty()) {
        return println("Add a file to the index.")
    } else if (args.size == 1) {
        println("Tracked files:")
        index.readLines().forEach {
            println(it)
        }
        return
    }

    if (!File(args[1]).exists()) {
        return println("Can't find '${args[1]}'.")
    }
    if (args[1] !in index.readLines()) {
        index.appendText(args[1] + "\n")
        return println("The file '${args[1]}' is tracked.")
    }
}

fun log() {
    val log = File("vcs/log.txt").readText()
    if (log.isEmpty()) {
        return println("No commits yet.")
    }
    print(log)
}

fun commit(args: Array<String>) {
    if (args.size == 1) {
        return println("Message was not passed.")
    }

    val indexFile: List<String> = File("vcs/index.txt").readLines()
    val files: List<File> = File("./").listFiles()?.filter {it.name in indexFile } ?: emptyList()
    if (files.isEmpty()) {
        return
    }
    var hash = ""
    files.forEach {
        hash += it.readText().sha1
    }
    val commitsDir = File("vcs/commits/")
    if (hash in (commitsDir.listFiles()?.map {it.name }?.toList() ?: emptyList())) {
        println("Nothing to commit.")
    } else {
        File("vcs/commits/$hash").mkdir()
        files.forEach {
            it.copyTo(File("vcs/commits/$hash/${it.name}"))
        }
        println("Changes are committed.")
        val config = File("vcs/config.txt")
        val log = File("vcs/log.txt")

        log.writeText("" +
                "commit $hash\n" +
                "Author: ${config.readText()}\n" +
                args[1] +
                "\n" +
                log.readText()
        )
    }

}

fun checkout(args: Array<String>) {
    if (args.size == 1) {
        return println("Commit id was not passed.")
    }
    val commitDir = File("vcs/commits/${args[1]}")
    if (!commitDir.exists()) {
        return println("Commit does not exist.")
    }
    commitDir.listFiles()?.forEach {
        it.copyTo(File("./${it.name}"), true)
    }
    println("Switched to commit ${args[1]}.")
}

val String.sha1: String
    get() {
        val bytes = MessageDigest.getInstance("SHA-1").digest(this.toByteArray())
        return bytes.joinToString("") {
            "%02x".format(it)
        }
    }