package contacts
import com.squareup.moshi.JsonAdapter
import java.io.File
import com.squareup.moshi.Moshi
import com.squareup.moshi.Types
import com.squareup.moshi.adapters.PolymorphicJsonAdapterFactory
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory


class Book(private val database: File) {
    private val contacts: MutableList<Contact> = mutableListOf()
    private val jsonAdapter = getJsonAdapter()

    init {
        readDatabaseFile()
    }

    fun add(contact: Contact) {
        contacts.add(contact)
        saveContactsToFile()
        println("The record added.\n")
    }

    fun getContact(contactId: Int): Contact? {
        return contacts.getOrNull(contactId)
    }

    fun deleteContact(contactId: Int) {
        if (contactId in 0..contacts.lastIndex) {
            contacts.removeAt(contactId)
            saveContactsToFile()
            println("The record removed!\n")
        }
    }

    fun count() {
        println("The Phone Book has ${contacts.size} records.\n")
    }

    fun printContactList() {
        for (i in 0..contacts.lastIndex) {
            println("${i + 1}. " + contacts[i].getContactName())
        }
        println()
    }

    fun search(query: String): MutableMap<Int, String> {
        val result: MutableMap<Int, String> = mutableMapOf()
        for (i in 0..contacts.lastIndex) {
            if (contacts[i].getSearchString().lowercase().contains(query.lowercase())) {
                result[i] = contacts[i].getContactName()
            }
        }
        return result
    }

    private fun getJsonAdapter(): JsonAdapter<List<Contact?>> {
        val adapterFactory = PolymorphicJsonAdapterFactory
            .of(Contact::class.java, "type")
            .withSubtype(Person::class.java, "person")
            .withSubtype(Organization::class.java, "organization")
        val moshi: Moshi = Moshi.Builder()
            .add(adapterFactory)
            .add(KotlinJsonAdapterFactory())
            .build()
        val types = Types.newParameterizedType(List::class.java, Contact::class.java)
        return moshi.adapter(types)
    }

    private fun readDatabaseFile() {
        jsonAdapter.fromJson(database.readText())?.forEach {
            if (it != null) contacts.add(it)
        }
    }

    fun saveContactsToFile() {
        val str = jsonAdapter.toJson(contacts)
        database.writeText(str)
    }
}