package models

/** La identidad de clientes se determina por nombre y correo. */
class Cliente(
    nombre: String,
    contacto: String,
    val direccion: String,
    val correo: String
) : Usuario(nombre, contacto) {
    override fun mostrarInformacion(): String =
        "${super.mostrarInformacion()} | Dirección: $direccion | Correo: $correo"

    operator fun component1(): String = nombre
    operator fun component2(): String = correo
    operator fun component3(): String = contacto

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is Cliente) return false
        return nombre.trim().lowercase() == other.nombre.trim().lowercase() &&
            correo.trim().lowercase() == other.correo.trim().lowercase()
    }

    override fun hashCode(): Int =
        31 * nombre.trim().lowercase().hashCode() + correo.trim().lowercase().hashCode()
}
