package contacts

class Person: Contact() {
    override val type = "person"
    override var name: String = ""
    private var surname: String = ""
    private var birthDate: String = "[no data]"
    private var gender: String = "[no data]"
    override val changeableFields: List<String> = listOf("name", "surname", "birth", "gender") + super.changeableFields

    override fun build() {
        setName()
        setSurname()
        setBirthDate()
        setGender()
        super.build()
    }

    override fun getInfo(): String {
        return  "Name: $name\n" +
                "Surname: $surname\n" +
                "Birth date: $birthDate\n" +
                "Gender: $gender\n" +
                super.getInfo()
    }

    override fun getContactName(): String {
        return super.getContactName() + if (surname.isNotEmpty()) " $surname" else ""
    }

    override fun getSearchString(): String {
        return name + surname + birthDate + gender + super.getSearchString()
    }

    override fun edit(field: String) {
        when(field) {
            "name" -> setName()
            "surname" -> setSurname()
            "birth" -> setBirthDate()
            "gender" -> setGender()
        }
        super.edit(field)
    }

    private fun setName() {
        println("Enter the name:")
        name = readln().trim()
    }

    private fun setSurname() {
        println("Enter the surname:")
        surname = readln().trim()
    }

    private fun setBirthDate() {
        println("Enter the birth date:")
        birthDate = readln().trim().ifEmpty {
                println("Bad birth date!")
                "[no data]"
            }
    }

    private fun setGender() {
        println("Enter the gender (M, F):")
        val input = readln().trim()
        gender = if (input in listOf("M", "F")) {
            input
        } else {
            println("Bad gender!")
            "[no data]"
        }
    }

}