package models

import annotations.Promocionable
import java.time.LocalDate

class Pedido(
    val idPedido: Int,
    val cliente: Cliente,
    val detalles: List<DetallePedido>,
    val fecha: LocalDate,
    val periodoPromocion: PeriodoPromocion,
    var estado: EstadoPedido = EstadoPedido.PENDIENTE
) {
    init { require(detalles.isNotEmpty()) { "Un pedido debe tener al menos un producto." } }

    fun calcularSubtotal(): Double = detalles.sumOf { it.producto.precio * it.cantidad }

    fun calcularDescuentoCantidad(): Double =
        if (detalles.filter { it.producto.categoria == CategoriaProducto.PIZZA }.sumOf { it.cantidad } > 3) {
            calcularSubtotal() * 0.10
        } else 0.0

    fun calcularDescuentoPromocional(): Double {
        if (!periodoPromocion.contiene(fecha)) return 0.0
        return detalles.sumOf { detalle ->
            val anotacion = detalle.producto.javaClass.getAnnotation(Promocionable::class.java)
            val porcentaje = anotacion?.porcentaje ?: 0
            detalle.producto.precio * detalle.cantidad * porcentaje / 100.0
        }
    }

    fun calcularDescuento(): Double = minOf(
        calcularSubtotal(), calcularDescuentoCantidad() + calcularDescuentoPromocional()
    )

    /** IVA académico: 19% sobre el valor neto una vez descontadas las promociones. */
    fun calcularImpuesto(tasa: Double = 0.19): Double =
        (calcularSubtotal() - calcularDescuento()) * tasa

    fun calcularTotal(tasa: Double = 0.19): Double =
        calcularSubtotal() - calcularDescuento() + calcularImpuesto(tasa)

    fun cambiarEstado(nuevoEstado: EstadoPedido) { estado = nuevoEstado }

    /** Une pedidos del mismo cliente y fecha; solo el resultado se procesa en inventario. */
    operator fun plus(otro: Pedido): Pedido {
        require(cliente == otro.cliente) { "Solo se pueden combinar pedidos del mismo cliente." }
        require(fecha == otro.fecha && periodoPromocion == otro.periodoPromocion) {
            "Los pedidos deben tener la misma fecha y período promocional."
        }
        val acumulados = linkedMapOf<Producto, DetallePedido>()
        for (detalle in detalles + otro.detalles) {
            val existente = acumulados[detalle.producto]
            if (existente != null) {
                require(existente.producto.precio == detalle.producto.precio &&
                    existente.producto.ingredientes == detalle.producto.ingredientes) {
                    "No se pueden sumar precios o recetas diferentes del mismo producto."
                }
            }
            acumulados[detalle.producto] = DetallePedido(
                existente?.producto ?: detalle.producto,
                (existente?.cantidad ?: 0) + detalle.cantidad
            )
        }
        return Pedido(idPedido, cliente, acumulados.values.toList(), fecha, periodoPromocion)
    }

    // Desestructuración: val (cliente, productos, total) = pedido
    operator fun component1(): Cliente = cliente
    operator fun component2(): List<DetallePedido> = detalles
    operator fun component3(): Double = calcularTotal()
}
