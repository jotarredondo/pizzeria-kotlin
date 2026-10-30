package models


open class Usuario(
    val nombre: String,
    val contacto: String)
    {
    open fun mostrarInformacion(): String {
        return "Nombre: $nombre | Contacto: $contacto"
    }
}
