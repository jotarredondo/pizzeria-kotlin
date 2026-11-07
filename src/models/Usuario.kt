package models

open class Usuario(val nombre: String, val contacto: String) {
    open fun mostrarInformacion(): String = "Nombre: $nombre | Teléfono: $contacto"
}
