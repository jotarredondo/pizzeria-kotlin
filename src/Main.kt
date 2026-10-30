import models.Cliente
import models.Inventario
import models.Pedido
import models.Producto
import models.Repartidor
import java.util.Locale

private val formatoTelefono = Regex("^\\+?[0-9]{8,15}$")
private val formatoCorreo = Regex(
    "^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$"
)

fun main() {
    println("=== SISTEMA DE GESTIÓN DE PIZZERÍA ===")

    // 1. CONFIGURACIÓN DEL PRODUCTO
    val precioPizza = leerDecimalPositivo(
        "Precio de la pizza Pepperoni", 9990.0
    )

    val pizzaPepperoni = Producto(
        nombre = "Pepperoni",
        precio = precioPizza,
        ingredientes = mapOf(
            "Masa" to 1,
            "Salsa" to 1,
            "Queso" to 2,
            "Pepperoni" to 1
        )
    )

    // 2. INVENTARIO INICIAL
    println("\n--- INVENTARIO INICIAL ---")

    val inventario = mutableMapOf(
        "Masa" to leerEnteroNoNegativo("Unidades de masa", 10),
        "Salsa" to leerEnteroNoNegativo("Unidades de salsa", 10),
        "Queso" to leerEnteroNoNegativo("Unidades de queso", 20),
        "Pepperoni" to leerEnteroNoNegativo("Unidades de pepperoni", 6)
    )

    val gestorInventario = Inventario()

    // 3. REPARTIDORES DISPONIBLES
    val repartidores = listOf(
        Repartidor("Carlos González", "+56987654321", "Providencia"),
        Repartidor("Daniela Soto", "+56987654322", "Ñuñoa"),
        Repartidor("Pedro Rojas", "+56987654323", "Santiago Centro")
    )

    // 4. REGISTRO DE PEDIDOS DEL DÍA
    val pedidosDelDia = mutableListOf<Pedido>()
    var siguienteId = 1001

    do {
        println("\n--- NUEVO PEDIDO #$siguienteId ---")

        // REGISTRO DEL CLIENTE
        val nombre = leerTexto(
            "Nombre del cliente", "Cliente no informado"
        )
        val direccion = leerTexto(
            "Dirección de entrega", "Dirección no informada"
        )
        val telefonoIngresado = leerTexto("Teléfono", "")

        val telefono = if (
            formatoTelefono.matches(telefonoIngresado)
        ) {
            telefonoIngresado
        } else {
            println("Teléfono inválido: se utilizará el valor predeterminado.")
            "(000) 000-0000"
        }

        val correoIngresado = leerTexto(
            "Correo electrónico (opcional)", ""
        )

        val correo: String? = when {
            correoIngresado.isBlank() -> null
            formatoCorreo.matches(correoIngresado) -> correoIngresado
            else -> {
                println("Correo inválido: se utilizará el valor predeterminado.")
                "correo@invalido.com"
            }
        }

        val notas = leerTexto(
            "Notas para el pedido (opcional)", ""
        ).ifBlank { null }

        val cliente = Cliente(nombre, telefono, direccion, correo)

        // CREACIÓN DEL PEDIDO
        val cantidadPizzas = leerEnteroPositivo(
            "Cantidad de pizzas"
        )

        val pedido = Pedido(
            idPedido = siguienteId,
            cliente = cliente,
            productos = List(cantidadPizzas) { pizzaPepperoni }
        )

        // VERIFICACIÓN DEL INVENTARIO
        val aceptado = gestorInventario.verificarYActualizarInventario(
            pizzaPepperoni,
            cantidadPizzas,
            inventario
        )

        val repartidorAsignado: Repartidor?

        if (aceptado) {
            pedido.cambiarEstado("En preparación")

            // ASIGNACIÓN DEL REPARTIDOR MÁS CERCANO
            println(
                "\nIngresa la distancia estimada desde el cliente a cada repartidor."
            )

            val distancias = repartidores.associateWith { repartidor ->
                leerDecimalNoNegativo(
                    "Distancia a ${repartidor.nombre} (km)"
                )
            }

            val distanciaMinima = repartidores
                .filter { it.disponible }
                .mapNotNull { distancias[it] }
                .minOrNull()

            repartidorAsignado = repartidores.find { repartidor ->
                repartidor.disponible &&
                        distancias[repartidor] == distanciaMinima
            }

            // CAMBIOS DE ESTADO
            println(
                "\nEstado del pedido: 1) En preparación  " +
                        "2) Listo  3) En camino  4) Entregado"
            )
            print("Elige estado [1]: ")

            val opcion = readlnOrNull()?.trim().orEmpty()

            val nuevoEstado = when (opcion) {
                "2" -> "Listo"
                "3" -> if (repartidorAsignado != null) {
                    "En camino"
                } else {
                    "Listo"
                }
                "4" -> if (repartidorAsignado != null) {
                    println(
                        repartidorAsignado.entregarPedido(pedido.idPedido)
                    )
                    "Entregado"
                } else {
                    "Listo"
                }
                else -> "En preparación"
            }

            pedido.cambiarEstado(nuevoEstado)

            // NOTIFICACIÓN SIMULADA
            correo?.takeIf {
                it != "correo@invalido.com"
            }?.let { correoValido ->
                println("Notificación simulada para: $correoValido")
            }

        } else {
            pedido.cambiarEstado("Rechazado")
            repartidorAsignado = null
        }

        // REGISTRAR Y MOSTRAR RESUMEN
        pedidosDelDia.add(pedido)
        mostrarResumen(pedido, repartidorAsignado, notas)

        println("\nInventario actual: $inventario")

        siguienteId++

        print("\n¿Registrar otro pedido? (s/n): ")
        val continuar = readlnOrNull()?.trim()?.lowercase() == "s"

    } while (continuar)

    // 5. REPORTE DEL DÍA: ARREGLOS, FOR Y FILTER
    val arregloPedidos: Array<Pedido> = pedidosDelDia.toTypedArray()

    println("\n=========== REPORTE DEL DÍA ===========")

    for (pedido in arregloPedidos) {
        val detallePizzas = pedido.productos.groupingBy { it.nombre }
            .eachCount().entries.joinToString(", ") { (nombre, cantidad) ->
                "$nombre x$cantidad"
            }

        val total = if (pedido.estado == "Rechazado") {
            0.0
        } else {
            pedido.calcularTotal()
        }

        println(
            "#${pedido.idPedido} | ${pedido.cliente.nombre} | " +
                    "$detallePizzas | ${dinero(total)} | ${pedido.estado}"
        )
    }

    val estadosCompletados = setOf("Entregado")
    val completados = arregloPedidos.filter {
        it.estado in estadosCompletados
    }
    val pendientes = arregloPedidos.filter {
        it.estado !in estadosCompletados && it.estado != "Rechazado"
    }
    val rechazados = arregloPedidos.filter {
        it.estado == "Rechazado"
    }

    println(
        "Completados: ${completados.size} | " +
                "Pendientes: ${pendientes.size} | " +
                "Rechazados: ${rechazados.size}"
    )
    println("======================================")
}

// FUNCIONES REUTILIZABLES

private fun leerTexto(
    etiqueta: String,
    valorPredeterminado: String
): String {
    print("$etiqueta: ")
    return readlnOrNull()?.trim().orEmpty()
        .ifBlank { valorPredeterminado }
}

private fun leerEnteroPositivo(etiqueta: String): Int {
    while (true) {
        print("$etiqueta: ")
        try {
            val numero = readlnOrNull()?.trim()?.toInt()
                ?: throw NumberFormatException("Debe ingresar un número entero")

            require(numero > 0) {
                "La cantidad debe ser mayor que cero"
            }
            return numero

        } catch (error: NumberFormatException) {
            println("Entrada inválida: ingresa un número entero.")
        } catch (error: IllegalArgumentException) {
            println("Entrada inválida: ${error.message}")
        }
    }
}

private fun leerEnteroNoNegativo(
    etiqueta: String,
    defecto: Int
): Int {
    while (true) {
        print("$etiqueta [Enter = $defecto]: ")
        val entrada = readlnOrNull()?.trim().orEmpty()

        if (entrada.isBlank()) return defecto

        try {
            val numero = entrada.toInt()
            require(numero >= 0) {
                "El stock no puede ser negativo"
            }
            return numero

        } catch (error: NumberFormatException) {
            println("Entrada inválida: debes ingresar un número entero.")
        } catch (error: IllegalArgumentException) {
            println("Entrada inválida: ${error.message}")
        }
    }
}

private fun leerDecimalPositivo(
    etiqueta: String,
    defecto: Double
): Double {
    while (true) {
        print("$etiqueta [Enter = ${dinero(defecto)}]: ")
        val entrada = readlnOrNull()?.trim().orEmpty()

        if (entrada.isBlank()) return defecto

        try {
            val numero = entrada.replace(',', '.').toDouble()
            require(numero.isFinite() && numero > 0) {
                "El precio debe ser mayor que cero"
            }
            return numero

        } catch (error: NumberFormatException) {
            println("Entrada inválida: debes ingresar un precio numérico.")
        } catch (error: IllegalArgumentException) {
            println("Entrada inválida: ${error.message}")
        }
    }
}

private fun leerDecimalNoNegativo(etiqueta: String): Double {
    while (true) {
        print("$etiqueta: ")

        try {
            val numero = readlnOrNull()?.trim()
                ?.replace(',', '.')?.toDouble()
                ?: throw NumberFormatException("Distancia no informada")

            require(numero.isFinite() && numero >= 0) {
                "La distancia no puede ser negativa"
            }
            return numero

        } catch (error: NumberFormatException) {
            println("Entrada inválida: ingresa una distancia numérica.")
        } catch (error: IllegalArgumentException) {
            println("Entrada inválida: ${error.message}")
        }
    }
}

private fun mostrarResumen(
    pedido: Pedido,
    repartidor: Repartidor?,
    notas: String?
) {
    println("\n=========== RESUMEN PEDIDO #${pedido.idPedido} ===========")
    println("Cliente: ${pedido.cliente.nombre}")
    println("Dirección: ${pedido.cliente.direccion}")
    println("Teléfono: ${pedido.cliente.contacto}")
    println("Correo: ${pedido.cliente.correo ?: "No informado"}")

    println("Productos:")
    pedido.productos.groupingBy { it.nombre }
        .eachCount().forEach { (nombre, cantidad) ->
            val precioUnitario = pedido.productos
                .first { it.nombre == nombre }.precio

            println(
                "  $nombre x$cantidad | " +
                        "Precio unitario: ${dinero(precioUnitario)}"
            )
        }

    println("Notas: ${notas ?: "Sin notas"}")

    if (pedido.estado != "Rechazado") {
        println("Subtotal: ${dinero(pedido.calcularSubtotal())}")
        println("Descuento: ${dinero(pedido.calcularDescuento())}")
        println("IVA (19%): ${dinero(pedido.calcularImpuesto())}")
        println("Total: ${dinero(pedido.calcularTotal())}")
    } else {
        println("Total a cobrar: ${dinero(0.0)} (pedido rechazado)")
    }

    println("Estado: ${pedido.estado}")
    println("Repartidor: ${repartidor?.nombre ?: "Sin asignar"}")
    println("================================================")
}

private fun dinero(valor: Double): String =
    "$" + String.format(
        Locale.forLanguageTag("es-CL"),
        "%,.2f",
        valor
    )
