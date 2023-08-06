package contacts

import java.io.File

fun main(args: Array<String>) {
    val database = getDatabase(args)
    val book = Book(database)
    while (true) {
        println("[menu] Enter action (add, list, search, count, exit):")
        when (readln()) {
            "add" -> addContactToBook(book)
            "list" -> listAction(book)
            "count" -> book.count()
            "search" -> search(book)
            "exit" -> break
        }
    }
}

fun addContactToBook(book: Book) {
    println("[add] Enter the type (person, organization):")
    val contact = when (readln().trim()) {
        "person" -> Person()
        "organization" -> Organization()
        else -> return
    }
    contact.build()
    book.add(contact)
}

fun listAction(book: Book) {
     while (true) {
        book.printContactList()
        println("[list] Enter action ([number], back):")
        val input = readln().trim()
        if (input == "back") {
            break
        }
        val contactId = input.toIntOrNull()?.minus(1) ?: continue
        return recordAction(contactId, book)
    }
}

fun recordAction(contactId: Int, book: Book) {
    val contact = book.getContact(contactId) ?: return
    while (true) {
        println(contact.getInfo())
        println("[record] Enter action (edit, delete, menu):")
        when (readln().trim()) {
            "edit" -> {
                println("Select a field (${contact.changeableFields.joinToString(", ")}):")
                val field = readln().trim()
                if (contact.update(field)) {
                    println("Saved")
                    book.saveContactsToFile()
                    println(contact.getInfo())
                    return
                }
            }

            "delete" -> return book.deleteContact(contactId)
            "menu" -> return
        }
    }
}

fun search(book: Book) {
    while (true){
        println("Enter search query:")
        val result = book.search(readln().trim())
        println("Found ${result.size} result:")
        var count = 1
        for (i in result.values) {
            println("$count. $i")
            count++
        }
        println("\nEnter action ([number], back, again):")
        val action = readln().trim()
        when (action) {
            "back" -> break
            "again" -> continue
        }
        if (result.isEmpty()) {
            continue
        }
        val contactNum = action.toIntOrNull()?.minus(1) ?: continue
        val contactId = result.keys.toList().getOrNull(contactNum) ?: continue
        return recordAction(contactId, book)
    }
}

fun getDatabase(args: Array<String>): File {
    if (args.size > 1 && Regex(""".db$""").matches(args[1])) {
        val file = File(args[1])
        if (!file.exists()) {
            file.writeText("[]")
        }
        return file
    }
    val file = File("base.db")
    file.writeText("[]")
    return file
}

