package models

class Cliente(
    nombre: String,
    contacto: String,
    val direccion: String,
    val correo: String? = null) : Usuario(nombre, contacto)
    {
    override fun mostrarInformacion(): String {
        return """
            ${super.mostrarInformacion()}
            Dirección: $direccion
            Correo: ${correo ?: "No informado"}
        """.trimIndent()
    }
}