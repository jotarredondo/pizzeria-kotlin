package utils

import models.Cliente
import java.time.LocalDate

object Entradas {
    fun leerTexto(etiqueta: String): String {
        while (true) {
            print("$etiqueta: ")
            val texto = readlnOrNull()?.trim().orEmpty()
            if (texto.isNotBlank()) return texto
            println("Error: este campo es obligatorio.")
        }
    }

    fun leerCorreo(): String {
        while (true) {
            print("Correo electrónico: ")
            val correo = readlnOrNull()?.trim().orEmpty()
            if (Validaciones.validarCorreo(correo)) return correo
            println("Error: correo inválido. Ejemplo válido: cliente@dominio.com")
        }
    }

    fun leerTelefono(): String {
        while (true) {
            print("Teléfono (ej. 978675645): ")
            val telefono = Validaciones.formatearTelefono(readlnOrNull().orEmpty())
            if (telefono != null) return telefono
            println("Error: teléfono inválido. Reingresa un número válido.")
        }
    }

    fun registrarCliente(): Cliente = Cliente(
        nombre = leerTexto("Nombre del cliente"),
        contacto = leerTelefono(),
        direccion = leerTexto("Dirección de entrega"),
        correo = leerCorreo()
    )

    fun leerFecha(): LocalDate {
        while (true) {
            print("Fecha del pedido (AAAA-MM-DD): ")
            val fecha = Validaciones.parsearFecha(readlnOrNull().orEmpty())
            if (fecha != null) return fecha
            println("Error: la fecha debe ser válida y usar el formato AAAA-MM-DD.")
        }
    }

    fun leerCantidad(etiqueta: String): Int {
        while (true) {
            print("$etiqueta [1-100]: ")
            try {
                val cantidad = readlnOrNull()?.trim()?.toInt()
                    ?: throw NumberFormatException("No es entero")
                if (Validaciones.cantidadPermitida(cantidad)) return cantidad
                println("Error: la cantidad debe estar entre 1 y 100.")
            } catch (_: NumberFormatException) {
                println("Error: ingresa un número entero válido.")
            }
        }
    }

    fun leerSiNo(etiqueta: String): Boolean {
        while (true) {
            print("$etiqueta: ")
            when (readlnOrNull()?.trim()?.lowercase()) {
                "s", "si", "sí" -> return true
                "n", "no" -> return false
                else -> println("Error: responde s o n.")
            }
        }
    }

    fun leerDistancia(etiqueta: String): Double {
        while (true) {
            print("$etiqueta (km): ")
            try {
                val distancia = readlnOrNull()?.replace(',', '.')?.trim()?.toDouble()
                    ?: throw NumberFormatException("Vacía")
                require(distancia.isFinite() && distancia >= 0) { "Distancia negativa o no finita." }
                return distancia
            } catch (_: NumberFormatException) {
                println("Error: ingresa una distancia numérica.")
            } catch (_: IllegalArgumentException) {
                println("Error: la distancia debe ser mayor o igual a cero.")
            }
        }
    }
}
