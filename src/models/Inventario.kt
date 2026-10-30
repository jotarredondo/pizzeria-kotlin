package models

class Inventario {

    fun verificarYActualizarInventario(
        producto: Producto,
        cantidadPizzas: Int,
        inventario: MutableMap<String, Int>): Boolean {

        // Calcular ingredientes necesarios para el pedido
        val ingredientesNecesarios = producto.ingredientes.mapValues {
                (_, cantidadPorPizza) -> cantidadPorPizza * cantidadPizzas }

        // Identificar ingredientes insuficientes
        val ingredientesFaltantes = ingredientesNecesarios.filter {
                (ingrediente, cantidadNecesaria) ->
            val disponible = inventario[ingrediente] ?: 0
            disponible < cantidadNecesaria }

        // Evaluar disponibilidad mediante when
        return when {
            cantidadPizzas <= 0 -> {
                println("Error: La cantidad de pizzas debe ser mayor a cero.")
                false
            }

            ingredientesFaltantes.isNotEmpty() -> {
                println("Pedido rechazado. Ingredientes insuficientes:")

                ingredientesFaltantes.forEach { (ingrediente, necesario) ->
                    val disponible = inventario[ingrediente] ?: 0
                    val faltante = necesario - disponible

                    println(
                        "$ingrediente: disponibles $disponible, " +
                                "necesarios $necesario. Reabastecer $faltante unidades."
                    )
                }

                false
            }

            else -> {
                // Descontar ingredientes utilizados
                ingredientesNecesarios.forEach { (ingrediente, necesario) ->
                    inventario[ingrediente] =
                        (inventario[ingrediente] ?: 0) - necesario
                }

                println("Pedido confirmado. Inventario actualizado.")
                true
            }
        }
    }


}