package models

class Pedido(
    val idPedido: Int,
    val cliente: Cliente,
    val productos: List<Producto>,
    var estado: String = "En preparación"
) {

    fun calcularSubtotal(): Double {
        return productos.sumOf { it.precio }
    }

    fun calcularDescuento(): Double {
        val subtotal = calcularSubtotal()

        return if (productos.size > 3) {
            subtotal * 0.10
        } else {
            0.0
        }
    }

    fun calcularImpuesto(tasa: Double = 0.19): Double {
        val montoNeto = calcularSubtotal() - calcularDescuento()
        return montoNeto * tasa
    }

    fun calcularTotal(tasa: Double = 0.19): Double {
        return calcularSubtotal() -
                calcularDescuento() +
                calcularImpuesto(tasa)
    }

    fun cambiarEstado(nuevoEstado: String) {
        estado = nuevoEstado
    }
}
