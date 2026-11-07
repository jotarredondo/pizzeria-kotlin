package models

import annotations.Promocionable

enum class CategoriaProducto { PIZZA, COMBO, BEBIDA, INGREDIENTE }

/** La identidad comercial de un producto es nombre + categoría. */
open class Producto(
    val nombre: String,
    val precio: Double,
    val categoria: CategoriaProducto,
    val ingredientes: Map<String, Int> = emptyMap()
) {
    init {
        require(nombre.isNotBlank()) { "El nombre del producto no puede estar vacío." }
        require(precio.isFinite() && precio >= 0) { "El precio debe ser válido y no negativo." }
        require(ingredientes.values.all { it >= 0 }) { "Los ingredientes no pueden ser negativos." }
    }

    fun detallarProducto(formatearPrecio: (Double) -> String = { it.toString() }): String {
        val descripcion = if (ingredientes.isEmpty()) "No requiere preparación" else
            ingredientes.entries.joinToString(", ") { (nombre, cantidad) -> "$nombre: $cantidad" }
        return "$nombre (${categoria.name}) | Precio: ${formatearPrecio(precio)} | Ingredientes: $descripcion"
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is Producto) return false
        return categoria == other.categoria &&
            nombre.trim().lowercase() == other.nombre.trim().lowercase()
    }

    override fun hashCode(): Int =
        31 * nombre.trim().lowercase().hashCode() + categoria.hashCode()

    override fun toString(): String = "$nombre (${categoria.name})"
}

@Promocionable(porcentaje = 5)
class Pizza(nombre: String, precio: Double, ingredientes: Map<String, Int>) :
    Producto(nombre, precio, CategoriaProducto.PIZZA, ingredientes)

@Promocionable(porcentaje = 8)
class Combo(nombre: String, precio: Double, ingredientes: Map<String, Int>) :
    Producto(nombre, precio, CategoriaProducto.COMBO, ingredientes)

class Bebida(nombre: String, precio: Double) :
    Producto(nombre, precio, CategoriaProducto.BEBIDA)
