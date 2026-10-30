# Sistema de Gestión de Pizzería — Kotlin
Proyecto de consola desarrollado en Kotlin para la actividad de la Semana 2: Aplicando utilidades de Kotlin, de la asignatura Desarrollo de Aplicaciones Móviles I (PMY2201) de Duoc UC.
Descripción
El sistema simula la gestión diaria de pedidos de una pizzería. Permite registrar clientes, crear pedidos de pizza Pepperoni, comprobar ingredientes disponibles, calcular importes, asignar repartidores y consultar un reporte de los pedidos procesados.
Funcionalidades
- Registro de clientes con validación de teléfono y correo electrónico opcional.
- Ingreso de notas opcionales para el pedido.
- Configuración del precio de la pizza y de las cantidades iniciales de ingredientes.
- Validación de cantidades, precios y distancias mediante try-catch.
- Gestión del inventario por ingrediente mediante MutableMap.
- Confirmación o rechazo de pedidos según disponibilidad de ingredientes.
- Cálculo de subtotal, descuento del 10 % para pedidos de más de 3 pizzas e IVA del 19 %.
- Asignación del repartidor disponible más cercano según las distancias ingresadas por el usuario.
- Actualización del estado del pedido: En preparación, Listo, En camino, Entregado o Rechazado.
- Notificación simulada cuando el cliente proporciona un correo válido.
- Reporte diario de pedidos completados, pendientes y rechazados.
  Nota: La tasa del 19 % y las distancias introducidas manualmente se utilizan para fines de simulación. No existe integración con mapas, servicios de correo ni pasarelas de pago.

# Estructura del proyecto
pizzeria-kotlin/
├── src/
│   ├── Main.kt
│   └── models/
│       ├── Producto.kt
│       ├── Inventario.kt
│       ├── Usuario.kt
│       ├── Cliente.kt
│       ├── Repartidor.kt
│       └── Pedido.kt
└── README.md
# Requisitos y ejecución
- IntelliJ IDEA con soporte para Kotlin.
- JDK configurado en el proyecto.
1. Abrir la carpeta pizzeria-kotlin en IntelliJ IDEA.
2. Esperar a que el entorno cargue y configure el proyecto.
3. Abrir src/Main.kt.
4. Ejecutar la función main() mediante el botón Run.
5. Responder las preguntas que aparecen en la consola. Se pueden registrar varios pedidos en una misma ejecución.
   Flujo de uso
1. Ingresar el precio de la pizza y el inventario inicial, o presionar Enter para utilizar los valores sugeridos.
2. Registrar nombre, dirección, teléfono, correo opcional y notas del cliente.
3. Indicar la cantidad de pizzas solicitadas.
4. El sistema comprueba el inventario y descuenta los ingredientes solo si el pedido puede prepararse.
5. Para pedidos aceptados, ingresar las distancias estimadas de los repartidores y seleccionar el estado del pedido.
6. Revisar el resumen del pedido y decidir si se desea registrar otro.
7. Al finalizar, consultar el reporte general del día.
   Ejemplo de cálculo
   Para 4 pizzas a $9.990 cada una:
   Concepto	Monto
   Subtotal	$39.960,00
   Descuento (10 %)	-$3.996,00
   IVA (19 % sobre el valor descontado)	$6.833,16
   Total	$42.797,16


Si falta algún ingrediente, el pedido se rechaza, no se descuenta inventario y el importe a cobrar es $0.
Conceptos de Kotlin aplicados
- Variables val y var y tipos de datos.
- Estructuras de control if, when, for y do-while.
- Funciones reutilizables.
- Colecciones List, Array, Set y Map, junto con filter, find y forEach.
- Programación orientada a objetos: clases, herencia y sobrescritura de métodos.
- Manejo de excepciones con try-catch.
- Seguridad ante nulos con ?., ?: y let.
  Alcance
  Esta aplicación es una simulación de consola realizada con fines académicos. No incluye interfaz gráfica Android, base de datos ni persistencia de pedidos entre ejecuciones.