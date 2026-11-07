package models

class Repartidor(
    nombre: String,
    contacto: String,
    val zona: String,
    var disponible: Boolean = true
) : Usuario(nombre, contacto) {
    override fun mostrarInformacion(): String =
        "${super.mostrarInformacion()} | Zona: $zona"

    fun entregarPedido(idPedido: Int): String =
        if (disponible) "Entrega confirmada por $nombre para el pedido #$idPedido." else
            "El repartidor $nombre no se encuentra disponible."
}
