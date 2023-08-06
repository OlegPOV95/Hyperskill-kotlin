package contacts

class Organization : Contact() {
    override val type: String = "organization"
    override var name: String = ""
    private var address: String = ""
    override val changeableFields: List<String> = listOf("name", "address") + super.changeableFields

    override fun build() {
        setName()
        setAddress()
        super.build()
    }

    override fun getInfo(): String {
        return "Organization name: $name\n" +
               "Address: $address\n" +
               super.getInfo()
    }

    override fun edit(field: String) {
        when (field) {
            "name" -> setName()
            "address" -> setAddress()
        }
        super.edit(field)
    }

    override fun getSearchString(): String {
        return name + address + super.getSearchString()
    }

    private fun setName() {
        println("Enter the organization name:")
        name = readln().trim()
    }

    private fun setAddress() {
        println("Enter the address:")
        address = readln().trim()
    }
}