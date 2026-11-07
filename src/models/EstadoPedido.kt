package models

enum class EstadoPedido(val descripcion: String) {
    PENDIENTE("Pendiente"),
    EN_PREPARACION("En preparación"),
    LISTO("Listo"),
    EN_CAMINO("En camino"),
    ENTREGADO("Entregado"),
    RECHAZADO("Rechazado")
}
