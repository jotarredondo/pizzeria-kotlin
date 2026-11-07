import models.*
import services.Demostraciones
import services.Reportes
import utils.Entradas
import java.time.LocalDate

fun main() {
    println("=== PIZZERÍA KOTLIN | ACTIVIDAD SUMATIVA 1 ===")

    val pizza = Pizza("Pepperoni", 9990.0, mapOf(
        "Masa" to 1, "Salsa" to 1, "Queso" to 2, "Pepperoni" to 1
    ))
    val combo = Combo("Combo Familiar", 16990.0, mapOf(
        "Masa" to 2, "Salsa" to 2, "Queso" to 4, "Pepperoni" to 2
    ))
    val bebida = Bebida("Bebida 1.5L", 2490.0)

    // Dos tipos promocionables y uno regular. Set elimina duplicados por equals/hashCode.
    val catalogo = setOf<Producto>(pizza, combo, bebida)
    println("Catálogo de productos: ${catalogo.joinToString()}")

    val periodo = PeriodoPromocion(
        LocalDate.of(2026, 11, 1), LocalDate.of(2026, 11, 15)
    )
    println("Promoción de demostración: ${periodo.inicio} a ${periodo.fin} (ambas fechas inclusivas).")

    val inventario = Inventario(mapOf(
        "Masa" to 30, "Salsa" to 30, "Queso" to 60, "Pepperoni" to 25
    ))
    val repartidores = listOf(
        Repartidor("Carlos González", "+56987654321", "Providencia"),
        Repartidor("Daniela Soto", "+56987654322", "Ñuñoa"),
        Repartidor("Pedro Rojas", "+56987654323", "Santiago Centro")
    )

    // Lista mutable: los pedidos del día se agregan según la interacción del usuario.
    // Para cumplir la evidencia de arreglos se convierte a Array al emitir el reporte.
    val pedidosDelDia = mutableListOf<Pedido>()
    var idSiguiente = 1001

    do {
        println("\n--- REGISTRO DEL CLIENTE ---")
        val cliente = Entradas.registrarCliente()
        val fecha = Entradas.leerFecha()
        val cantidadInicial = Entradas.leerCantidad("Cantidad de pizzas")

        // Pedido inicial del cliente. No se consume inventario hasta confirmar.
        val pedidoInicial = Pedido(
            idSiguiente++, cliente, listOf(DetallePedido(pizza, cantidadInicial)), fecha, periodo
        )
        println("\nPedido #${pedidoInicial.idPedido} registrado: $cantidadInicial pizza(s).")

        // La segunda orden es opcional: el cliente decide si agregar productos.
        val pedidoFinal = if (Entradas.leerSiNo("¿Desea agregar otra orden a este pedido? (s/n)")) {
            val adicionales = mutableListOf<DetallePedido>()

            if (Entradas.leerSiNo("¿Agregar más pizzas Pepperoni? (s/n)")) {
                val cantidad = Entradas.leerCantidad("Cantidad de pizzas adicionales")
                adicionales.add(DetallePedido(pizza, cantidad))
            }
            if (Entradas.leerSiNo("¿Agregar combo familiar? (s/n)")) {
                val cantidad = Entradas.leerCantidad("Cantidad de combos")
                adicionales.add(DetallePedido(combo, cantidad))
            }
            if (Entradas.leerSiNo("¿Agregar bebida 1.5L? (s/n)")) {
                val cantidad = Entradas.leerCantidad("Cantidad de bebidas")
                adicionales.add(DetallePedido(bebida, cantidad))
            }

            if (adicionales.isEmpty()) {
                println("No se añadieron productos. Se conserva el pedido original.")
                pedidoInicial
            } else {
                // Dos objetos Pedido independientes; el operador + consolida sus productos.
                // Ambos son borradores: solo se verifica/descuenta stock del resultado.
                val ordenAdicional = Pedido(idSiguiente++, cliente, adicionales, fecha, periodo)
                val combinado = pedidoInicial + ordenAdicional
                println("\n--- PEDIDOS COMBINADOS CON + ---")
                println("#${pedidoInicial.idPedido} + #${ordenAdicional.idPedido} -> #${combinado.idPedido}")
                println("Antes: ${Reportes.dinero(pedidoInicial.calcularTotal())} + " +
                    Reportes.dinero(ordenAdicional.calcularTotal()))
                println("Después de combinar: ${Reportes.dinero(combinado.calcularTotal())}")
                combinado
            }
        } else {
            println("Se mantiene el pedido original sin combinar.")
            pedidoInicial
        }

        val aprobado = inventario.verificarYActualizarInventario(pedidoFinal)
        var repartidor: Repartidor? = null
        if (aprobado) {
            pedidoFinal.cambiarEstado(EstadoPedido.EN_PREPARACION)
            repartidor = asignarRepartidor(repartidores)
            pedidoFinal.cambiarEstado(escogerEstado(repartidor))
            if (pedidoFinal.estado == EstadoPedido.ENTREGADO && repartidor != null) {
                println(repartidor.entregarPedido(pedidoFinal.idPedido))
            }
        } else {
            pedidoFinal.cambiarEstado(EstadoPedido.RECHAZADO)
        }

        // Solo el pedido final se confirma y descuenta stock, con o sin orden adicional.
        pedidosDelDia.add(pedidoFinal)
        Reportes.resumen(pedidoFinal, repartidor, inventario)
        Demostraciones.mostrar(cliente, pizza, combo, repartidor)

        val continuar = Entradas.leerSiNo("\n¿Ingresar otro cliente? (s/n)")
    } while (continuar)

    Reportes.reporteDiario(pedidosDelDia.toTypedArray())
}

private fun asignarRepartidor(repartidores: List<Repartidor>): Repartidor? {
    println("\nDistancias estimadas a repartidores disponibles:")
    val distancias = repartidores.filter { it.disponible }.associateWith {
        Entradas.leerDistancia("Distancia a ${it.nombre}")
    }
    val minimo = distancias.values.minOrNull()
    return repartidores.find { it.disponible && distancias[it] == minimo }
}

private fun escogerEstado(repartidor: Repartidor?): EstadoPedido {
    println("\nEstado: 1) En preparación  2) Listo  3) En camino  4) Entregado")
    print("Opción [1]: ")
    return when (readlnOrNull()?.trim()) {
        "2" -> EstadoPedido.LISTO
        "3" -> if (repartidor != null) EstadoPedido.EN_CAMINO else EstadoPedido.LISTO
        "4" -> if (repartidor != null) EstadoPedido.ENTREGADO else EstadoPedido.LISTO
        else -> EstadoPedido.EN_PREPARACION
    }
}
