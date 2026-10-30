package models


class Repartidor(
    nombre: String,
    contacto: String,
    val zona: String,
    var disponible: Boolean = true) : Usuario(nombre, contacto)
    {
    override fun mostrarInformacion(): String {
        return "${super.mostrarInformacion()} | Zona: $zona"
    }

    fun entregarPedido(idPedido: Int): String {
        return if (disponible) {
            "Repartidor $nombre: entrega del pedido $idPedido realizada."
        } else {
            "El repartidor $nombre no está disponible."
        }
    }
}