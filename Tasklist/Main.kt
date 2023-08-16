package tasklist

import com.squareup.moshi.JsonAdapter
import com.squareup.moshi.Moshi
import com.squareup.moshi.Types
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import kotlinx.datetime.*
import java.io.File

enum class Priority(val color: String) {
    C("\u001B[101m \u001B[0m"),
    H("\u001B[103m \u001B[0m"),
    N("\u001B[102m \u001B[0m"),
    L("\u001B[104m \u001B[0m")
}

enum class Due(val color: String) {
    I("\u001B[102m \u001B[0m"),
    T("\u001B[103m \u001B[0m"),
    O("\u001B[101m \u001B[0m")
}

class Task (var taskDate: String, var taskTime: String, var taskPriority: String, var text: MutableList<String>) {
    lateinit var due: String
    init {
        updateDueValue()
    }

    fun updateDueValue() {
        val currentDate = Clock.System.now().toLocalDateTime(TimeZone.of("UTC+2")).date
        val numbersOfDay = currentDate.daysUntil(LocalDate.parse(taskDate))
        due = when {
            numbersOfDay == 0 -> Due.T.color
            numbersOfDay > 0 -> Due.I.color
            else -> Due.O.color
        }
    }
}

fun main() {
    val moshi: Moshi = Moshi.Builder()
        .add(KotlinJsonAdapterFactory())
        .build()
    val type = Types.newParameterizedType(MutableList::class.java, Task::class.java)
    val jsonAdapter: JsonAdapter<MutableList<Task>> = moshi.adapter(type)

    val file = File("tasklist.json")
    if (!file.exists()) {
        file.createNewFile()
    }
    val tasks: MutableList<Task> =
        try {
            jsonAdapter.fromJson(file.readText()) ?: mutableListOf()
        } catch (e: Exception) {
            mutableListOf()
        }

    while (true) {
        println("Input an action (add, print, edit, delete, end):")
        when (readln().trim()) {
            "add" -> {
                val priority = getPriority()
                val date = getDate()
                val time = getTime()
                val text = getText()
                if (text.isEmpty()) {
                    println("The task is blank")
                    continue
                }
                val task = Task(date, time, priority, text)
                tasks.add(task)
            }
            "print" -> printTasksList(tasks)
            "edit" -> {
                printTasksList(tasks)
                if (tasks.isEmpty()) continue
                val index = getTaskIndex(tasks.size)
                when (getEditFieldName()) {
                    "priority" -> tasks[index].taskPriority = getPriority()
                    "date" -> {
                        tasks[index].taskDate = getDate()
                        tasks[index].updateDueValue()
                    }
                    "time" -> tasks[index].taskTime = getTime()
                    "task" -> tasks[index].text = getText()
                }
                println("The task is changed")
            }
            "delete" -> {
                printTasksList(tasks)
                if (tasks.isEmpty()) continue
                val index = getTaskIndex(tasks.size)
                tasks.removeAt(index)
                println("The task is deleted")
            }
            "end" -> break
            else -> println("The input action is invalid")
        }
    }
    file.writeText(jsonAdapter.toJson(tasks))

    println("Tasklist exiting!")
}

fun getTaskIndex(size: Int): Int {
    val maxIndex = size - 1
    while (true) {
        println("Input the task number (1-$size):")
        try {
            val index = readln().toInt() - 1
            if (index in 0..maxIndex) {
                return index
            }
        } catch (_: Exception) {}
        println("Invalid task number")
    }
}

fun getEditFieldName(): String {
    while (true) {
        println("Input a field to edit (priority, date, time, task):")
        val input = readln().trim()
        if (input in listOf("priority", "date", "time", "task")) {
            return input
        }
        println("Invalid field")
    }
}

fun getDate(): String {
    while (true) {
        println("Input the date (yyyy-mm-dd):")
        try {
            val (years, month, day) = readln().trim().split("-").map { it.toInt() }
            return LocalDate(years, month, day).toString()
        } catch (e: Exception) {
            println("The input date is invalid")
        }
    }
}

fun getTime(): String {
    while (true) {
        println("Input the time (hh:mm):")
        try {
            val (hour, minute) = readln().trim().split(":").map { it.toInt() }
            return LocalTime(hour, minute).toString()
        } catch (e: Exception) {
            println("The input time is invalid")
        }
    }
}

fun getPriority(): String {
    while (true) {
        println("Input the task priority (C, H, N, L):")
        val input = readln().trim().uppercase()
        if (input in Priority.values().map { it.name }) {
            return Priority.valueOf(input).color
        }
    }
}

fun getText(): MutableList<String> {
    val result = mutableListOf<String>()
    println("Input a new task (enter a blank line to end):")
    while (true) {
        val line = readln().trim()
        if (line.isEmpty()) {
            break
        }
        result.add(line)
    }
    val resultIterator = result.listIterator()
    while (resultIterator.hasNext()) {
        resultIterator.next().apply {
            if (length > 44) {
                resultIterator.set(substring(0..43))
                resultIterator.add(substring(44..lastIndex))
                resultIterator.previous()
            } else {
                resultIterator.set(this + " ".repeat(44 - this.length))
            }
        }
    }

    return result
}

fun printTasksList(tasks: List<Task>) {
    if (tasks.isEmpty()) {
        return println("No tasks have been input")
    }
    println("""
        +----+------------+-------+---+---+--------------------------------------------+
        | N  |    Date    | Time  | P | D |                   Task                     |
        +----+------------+-------+---+---+--------------------------------------------+
    """.trimIndent())

    var taskNumber = 1
    tasks.forEach { task ->
        val taskNumberWithOffset = "$taskNumber" + if (taskNumber < 10) " " else ""
        println("| $taskNumberWithOffset | ${task.taskDate} | ${task.taskTime} | ${task.taskPriority} | ${task.due} |${task.text[0]}|")
        for (i in 1..task.text.lastIndex) {
            println("|    |            |       |   |   |${task.text[i]}|")
        }
        println("+----+------------+-------+---+---+--------------------------------------------+")
        taskNumber++
    }
}

