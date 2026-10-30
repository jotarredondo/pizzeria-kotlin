package models

data class Producto(
    val nombre: String,
    val precio: Double,
    val ingredientes: Map<String, Int>)
    {
    fun detallarProducto(): String {
        val detalleIngredientes = ingredientes.entries.joinToString(", ") {
            "${it.key}: ${it.value}"
        }

        return """
            Producto: $nombre
            Precio: $$precio
            Ingredientes por unidad: $detalleIngredientes""".trimIndent()
    }
}
