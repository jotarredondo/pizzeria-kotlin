package models

/** Cantidad explícita de un producto en un pedido. */
data class DetallePedido(val producto: Producto, val cantidad: Int) {
    init { require(cantidad > 0) { "La cantidad debe ser positiva." } }
}
