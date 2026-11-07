package services

import annotations.Promocionable
import models.*

object Demostraciones {
    /** Java reflection API invocada desde Kotlin: no requiere la librería kotlin-reflect. */
    private fun inspeccionarClase(tipo: Class<*>) {
        // Incluye miembros heredados para clases Pizza y Combo (que extienden Producto).
        val jerarquia = generateSequence(tipo) { it.superclass }
            .takeWhile { it != Any::class.java }.toList()
        val propiedades = jerarquia.flatMap { it.declaredFields.toList() }
            .filterNot { it.isSynthetic }.map { it.name }.distinct().sorted()
        val metodos = jerarquia.flatMap { it.declaredMethods.toList() }
            .filterNot { it.isSynthetic || it.name.contains('$') }
            .map { it.name }.distinct().sorted()
        println("Clase ${tipo.simpleName} -> Propiedades: $propiedades | Métodos: $metodos")
    }

    fun mostrar(cliente: Cliente, pizza: Pizza, combo: Combo, repartidor: Repartidor?) {
        println("\n========== DEMOSTRACIÓN TÉCNICA SUMATIVA ==========")
        println("REFLECTION Y ANOTACIONES (dos clases):")
        inspeccionarClase(pizza.javaClass)
        inspeccionarClase(combo.javaClass)
        inspeccionarClase(Pedido::class.java)
        for (producto in listOf(pizza, combo)) {
            val anotacion = producto.javaClass.getAnnotation(Promocionable::class.java)
            println("${producto.nombre}: @Promocionable = ${anotacion?.porcentaje ?: 0}%")
        }

        println("\nIGUALDAD / HASHCODE / CONTROL DE DUPLICADOS:")
        val clienteCopia = Cliente(cliente.nombre.uppercase(), cliente.contacto, cliente.direccion, cliente.correo)
        val productoCopia = Pizza(pizza.nombre.uppercase(), pizza.precio, pizza.ingredientes)
        println("Clientes iguales (==): ${cliente == clienteCopia}; únicos en Set: ${setOf(cliente, clienteCopia).size}")
        println("Productos iguales (==): ${pizza == productoCopia}; únicos en Set: ${setOf(pizza, productoCopia).size}")
        println("hashCode consistente: ${cliente.hashCode() == clienteCopia.hashCode()} y ${pizza.hashCode() == productoCopia.hashCode()}")

        println("\nPOLIMORFISMO (referencias Usuario):")
        val usuarios: List<Usuario> = listOfNotNull(cliente, repartidor)
        for (usuario: Usuario in usuarios) println(usuario.mostrarInformacion())
        println("==================================================")
    }
}
