package models

/** El mapa de stock es privado; el exterior obtiene únicamente una copia de lectura. */
class Inventario(stockInicial: Map<String, Int>) {
    private val stock = stockInicial.toMutableMap()

    init { require(stock.values.all { it >= 0 }) { "El stock inicial no puede ser negativo." } }

    fun consultarStock(): Map<String, Int> = stock.toMap()

    fun verificarYActualizarInventario(pedido: Pedido): Boolean {
        val necesarios = mutableMapOf<String, Int>()
        for (detalle in pedido.detalles) {
            for ((ingrediente, porUnidad) in detalle.producto.ingredientes) {
                necesarios[ingrediente] = (necesarios[ingrediente] ?: 0) +
                    porUnidad * detalle.cantidad
            }
        }

        val faltantes = necesarios.filter { (ingrediente, necesario) ->
            (stock[ingrediente] ?: 0) < necesario
        }
        return when {
            faltantes.isNotEmpty() -> {
                println("Pedido rechazado. Ingredientes insuficientes:")
                for ((ingrediente, necesario) in faltantes) {
                    val disponible = stock[ingrediente] ?: 0
                    println("- $ingrediente: disponible $disponible; necesita $necesario; reponer ${necesario - disponible}.")
                }
                false
            }
            else -> {
                // Descuento atómico: solo se actualiza si todos los ingredientes alcanzan.
                for ((ingrediente, necesario) in necesarios) {
                    stock[ingrediente] = (stock[ingrediente] ?: 0) - necesario
                }
                println("Pedido confirmado. Inventario actualizado.")
                true
            }
        }
    }
}
