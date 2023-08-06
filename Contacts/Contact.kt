package contacts

import kotlinx.datetime.*

abstract class Contact {
    abstract val type: String
    abstract var name: String
    open var number: String = "[no number]"
    open val changeableFields: List<String> = listOf("number")
    private val createdTime: String = currentTime()
    private var updatedTime: String = createdTime


    open fun build() {
       setNumber()
    }

    fun update(field: String): Boolean {
        if (field in changeableFields) {
            edit(field)
            updatedTime = currentTime()
            return true
        }
        return false
    }

    open fun getInfo(): String {
        return "Number: $number\n" +
            "Time created: $createdTime\n" +
            "Time last edit: $updatedTime\n"
    }

    open fun getContactName(): String {
        return name
    }

    protected open fun edit(field: String) {
        when (field) {
            "number" -> setNumber()
        }
    }

    open fun getSearchString(): String {
        return if (number != "[no number]") number else ""
    }

    private fun setNumber() {
        println("Enter the number:")
        val input = readln().trim()
        number = if (checkNumber(input)) input else "[no number]"
    }

    private fun currentTime(): String {
        val timeNow = Clock.System.now()
        val timeZone = TimeZone.currentSystemDefault()
        return timeNow.toLocalDateTime(timeZone).toString()
    }

    private fun checkNumber(number: String): Boolean {
        if (number.count { it == '(' } > 1 || number.count { it == ')' } > 1) return false
        val regex = Regex("""^\+?((\([\da-zA-Z]+\))|([\da-zA-Z]+))([\s-]\(?[\da-zA-Z]{2,}\)?)?([\s-][\da-zA-Z]{2,})*$""")
        if (!regex.matches(number)) {
            println("Wrong number format!")
            return false
        }
        return  true
    }
}