package services

import annotations.Promocionable
import models.*
import java.text.NumberFormat
import java.util.Locale

object Reportes {
    private val formatoMoneda: NumberFormat = NumberFormat
        .getCurrencyInstance(Locale.forLanguageTag("es-CL"))
        .apply { minimumFractionDigits = 2; maximumFractionDigits = 2 }

    fun dinero(monto: Double): String = formatoMoneda.format(monto)

    /** Reutilizada por el resumen individual y el reporte diario. */
    fun agruparProductos(detalles: List<DetallePedido>): Map<Producto, Int> {
        val cantidades = linkedMapOf<Producto, Int>()
        for ((producto, cantidad) in detalles) {
            cantidades[producto] = (cantidades[producto] ?: 0) + cantidad
        }
        return cantidades
    }

    fun resumen(pedido: Pedido, repartidor: Repartidor?, inventario: Inventario) {
        println("\n========== RESUMEN DEL PEDIDO #${pedido.idPedido} ==========")

        val (cliente, productos, total) = pedido // Desestructuración de Pedido
        val (nombre, correo, telefono) = cliente // Desestructuración de Cliente
        println("Cliente desestructurado: $nombre | $correo | $telefono")
        println("Dirección: ${cliente.direccion} | Fecha: ${pedido.fecha}")
        println("Estado: ${pedido.estado.descripcion}")

        println("Productos:")
        for ((producto, cantidad) in agruparProductos(productos)) {
            val promocion = producto.javaClass.getAnnotation(Promocionable::class.java)
            val mensaje = if (promocion != null) "PROMOCIONABLE ${promocion.porcentaje}%" else "Sin promoción"
            println("- ${producto.nombre} x$cantidad (${dinero(producto.precio)} c/u) | $mensaje")
            println("  ${producto.detallarProducto(::dinero)}")
        }

        println("Promoción por fecha vigente: ${pedido.periodoPromocion.contiene(pedido.fecha)}")
        if (pedido.estado != EstadoPedido.RECHAZADO) {
            println("Subtotal: ${dinero(pedido.calcularSubtotal())}")
            println("Descuento por cantidad: ${dinero(pedido.calcularDescuentoCantidad())}")
            println("Descuento promocional: ${dinero(pedido.calcularDescuentoPromocional())}")
            println("Descuentos totales: ${dinero(pedido.calcularDescuento())}")
            println("IVA (19%): ${dinero(pedido.calcularImpuesto())}")
            println("Total calculado (desestructurado): ${dinero(total)}")
        } else {
            println("Total calculado (referencial): ${dinero(total)}")
            println("Total a cobrar: ${dinero(0.0)} (rechazado)")
        }
        println("Repartidor: ${repartidor?.nombre ?: "Sin asignar"}")
        println("Inventario restante: ${inventario.consultarStock()}")
    }

    fun reporteDiario(pedidos: Array<Pedido>) {
        println("\n============== REPORTE DIARIO ==============")
        for (pedido in pedidos) {
            val productos = agruparProductos(pedido.detalles).entries.joinToString(", ") { (p, c) -> "${p.nombre} x$c" }
            val totalCobro = if (pedido.estado == EstadoPedido.RECHAZADO) 0.0 else pedido.calcularTotal()
            println("#${pedido.idPedido} | ${pedido.cliente.nombre} | $productos | ${dinero(totalCobro)} | ${pedido.estado.descripcion}")
        }
        println("Entregados: ${pedidos.count { it.estado == EstadoPedido.ENTREGADO }}")
        println("Pendientes: ${pedidos.count { it.estado != EstadoPedido.ENTREGADO && it.estado != EstadoPedido.RECHAZADO }}")
        println("Rechazados: ${pedidos.count { it.estado == EstadoPedido.RECHAZADO }}")
        println("=============================================")
    }
}
