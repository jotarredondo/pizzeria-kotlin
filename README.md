# Pizzería Kotlin — Actividad Sumativa 1

**Asignatura:** Desarrollo de Aplicaciones Móviles I (PMY2201) — Duoc UC  
**Experiencia / semana:** Experiencia 1, semana 3  
**Actividad:** *Creando soluciones con Kotlin* — Instrucciones específicas, forma B  
**Tecnología:** Kotlin/JVM, aplicación de consola ejecutada en IntelliJ IDEA  

## 1. Descripción

Aplicación académica para gestionar pedidos de una pizzería. Permite registrar clientes, validar datos de entrada, crear pedidos, agregar una orden adicional de manera opcional, aplicar promociones, gestionar inventario por ingrediente, asignar un repartidor y generar resúmenes en consola.

Este proyecto continúa los desarrollos de las semanas 1 y 2 y demuestra las características avanzadas de Kotlin solicitadas en la evaluación sumativa: **Regex, Ranges, anotaciones, reflection, sobrecarga de operadores, desestructuración y comparación de objetos**.

> **Alcance:** es una aplicación de consola Kotlin/JVM, no una aplicación Android con interfaz gráfica. No utiliza base de datos, API de mapas, pasarela de pagos ni persistencia entre ejecuciones.

## 2. Requisitos para ejecutarlo

- IntelliJ IDEA con soporte para Kotlin.
- JDK configurado en el proyecto; la versión utilizada en las pruebas del entorno es **JDK 21**.
- Proyecto Kotlin/JVM con su configuración de dependencias correctamente cargada.

**Ejecución:**

1. Abrir la carpeta `pizzeria-kotlin` en IntelliJ IDEA.
2. Verificar que `src` sea la carpeta de código fuente y que se hayan cargado todos los paquetes.
3. Abrir `src/Main.kt` y ejecutar la función `main()`.
4. Seguir las instrucciones de la consola. Para finalizar el registro de clientes, responder `n`.
5. Al terminar, revisar el reporte diario y verificar que aparezca `Process finished with exit code 0`.

La inspección dinámica se realiza con **Java Reflection desde Kotlin** (`javaClass`, `getAnnotation`, `declaredFields` y `declaredMethods`), por lo que esta implementación no requiere agregar la biblioteca `kotlin-reflect`.

## 3. Estructura del código

```text
pizzeria-kotlin/
├── src/
│   ├── Main.kt
│   ├── annotations/
│   │   └── Promocionable.kt
│   ├── models/
│   │   ├── Cliente.kt
│   │   ├── DetallePedido.kt
│   │   ├── EstadoPedido.kt
│   │   ├── Inventario.kt
│   │   ├── Pedido.kt
│   │   ├── PeriodoPromocion.kt
│   │   ├── Producto.kt
│   │   ├── Repartidor.kt
│   │   └── Usuario.kt
│   ├── services/
│   │   ├── Demostraciones.kt
│   │   └── Reportes.kt
│   └── utils/
│       ├── Entradas.kt
│       └── Validaciones.kt
└── README.md
```

**Responsabilidades principales:**

| Archivo o componente | Responsabilidad |
|---|---|
| `Main.kt` | Coordinar la ejecución, el registro y la creación de pedidos. |
| `Entradas.kt` | Leer y volver a solicitar entradas inválidas. |
| `Validaciones.kt` | Validar correos, teléfonos, fechas y cantidades. |
| `Producto.kt` | Definir productos y categorías (`Pizza`, `Combo`, `Bebida`), y comparar productos. |
| `Cliente.kt` / `Usuario.kt` / `Repartidor.kt` | Modelar los participantes y demostrar herencia y polimorfismo. |
| `DetallePedido.kt` | Representar explícitamente un producto y su cantidad. |
| `Pedido.kt` | Calcular importes, aplicar descuentos, cambiar estados, combinar pedidos y permitir desestructuración. |
| `Inventario.kt` | Encapsular el stock y descontarlo únicamente si todos los ingredientes alcanzan. |
| `EstadoPedido.kt` | Centralizar los estados en un `enum class`. |
| `PeriodoPromocion.kt` | Evaluar fechas mediante un rango inclusivo de `LocalDate`. |
| `Promocionable.kt` | Declarar la anotación que identifica tipos de producto promocionables. |
| `Demostraciones.kt` | Mostrar reflection, anotaciones, igualdad, control de duplicados y polimorfismo. |
| `Reportes.kt` | Generar resúmenes y reporte diario, reutilizar la agrupación y centralizar el formato monetario. |

## 4. Funcionalidades y relación con la pauta de evaluación

| Requisito de la semana 3 | Implementación / evidencia |
|---|---|
| **Regex: correo** | `Validaciones.validarCorreo()` y reintento obligatorio en `Entradas.leerCorreo()`. |
| **Regex: teléfono** | `Validaciones.formatearTelefono()` normaliza números válidos y solicita reingreso de los incorrectos. |
| **Ranges: cantidad** | `cantidad in 1..100`, con rechazo y reintento para cada cantidad ingresada. |
| **Ranges: fecha** | `PeriodoPromocion.contiene()` utiliza `fecha in inicio..fin`; se valida la fecha ingresada en formato `AAAA-MM-DD`. |
| **Anotaciones** | `@Promocionable(porcentaje = 5)` en `Pizza` y `@Promocionable(porcentaje = 8)` en `Combo`. |
| **Reflection** | Se inspeccionan métodos y propiedades de `Pizza`, `Combo` y `Pedido`; se recuperan anotaciones en ejecución. |
| **Sobrecarga `+`** | `operator fun plus(otro: Pedido)` combina órdenes del mismo cliente y fecha, agrupando productos y recalculando el total. |
| **Comparación `==`** | Kotlin invoca `equals()`; se compara `Producto` por nombre y categoría. |
| **Igualdad y `hashCode()`** | Sobrescritos en `Cliente` y `Producto`; se demuestra eliminación de duplicados con `Set`. |
| **Desestructuración** | Se utilizan los componentes de `Cliente` (nombre, correo, teléfono) y `Pedido` (cliente, detalles, total). |
| **Resumen integrado** | Se muestra el pedido combinado, descuentos, cliente desestructurado, productos anotados y pruebas de igualdad. |
| **Entrega** | Proyecto Kotlin compilable, organizado en paquetes y preparado para comprimirse en ZIP o RAR. |

> En Kotlin, `==` no se sobrecarga directamente con `operator fun`; su comportamiento personalizado se implementa sobrescribiendo `equals()`, junto con un `hashCode()` coherente.

## 5. Flujo de uso

1. **Registro:** el usuario ingresa nombre, teléfono, dirección, correo y fecha del pedido.
2. **Pedido inicial:** ingresa una cantidad de pizzas Pepperoni entre 1 y 100.
3. **Orden adicional opcional:** si responde `s`, puede agregar pizzas, combos y bebidas; si responde `n`, continúa con el pedido inicial.
4. **Combinación:** solo si se agregaron productos, se construye una segunda instancia de `Pedido` y se ejecuta `pedidoInicial + ordenAdicional`.
5. **Inventario:** se verifica la disponibilidad para **el pedido final**, sin descontar dos veces los ingredientes de los pedidos de origen.
6. **Despacho:** si hay ingredientes, se asigna el repartidor disponible con menor distancia estimada, ingresada manualmente, y se selecciona el estado del pedido.
7. **Salida:** se imprime el resumen comercial, la demostración técnica de la sumativa y, al finalizar, el reporte diario.

La segunda orden es **opcional** porque representa la decisión del cliente de agregar productos a una compra existente. La sobrecarga del operador `+` demuestra una operación de negocio coherente con el requisito académico.

### Ejemplo de uso del operador `+`

```text
Cantidad de pizzas [1-100]: 1
Pedido #1001 registrado: 1 pizza(s).
¿Desea agregar otra orden a este pedido? (s/n): s
¿Agregar más pizzas Pepperoni? (s/n): n
¿Agregar combo familiar? (s/n): s
Cantidad de combos [1-100]: 1
¿Agregar bebida 1.5L? (s/n): s
Cantidad de bebidas [1-100]: 1

--- PEDIDOS COMBINADOS CON + ---
#1001 + #1002 -> #1001
Después de combinar: $32.857,45
```

El pedido combinado conserva el identificador del pedido original. Los pedidos de origen se consideran borradores; solo el resultado final se registra para la entrega y modifica inventario.

## 6. Reglas de negocio y supuestos de demostración

Las instrucciones específicas solicitan promociones y totales, pero **no establecen fechas ni porcentajes concretos**. Para ejecutar y evaluar la aplicación se adoptaron estas reglas documentadas:

- **Promoción activa:** desde el **1 hasta el 15 de noviembre de 2026**, ambas fechas inclusive.
- **Pizza Pepperoni:** $9.990; clase anotada con `@Promocionable(5)`.
- **Combo Familiar:** $16.990; clase anotada con `@Promocionable(8)`.
- **Bebida 1.5L:** $2.490; sin descuento promocional.
- **Descuento por cantidad:** 10 % del subtotal si la cantidad total de **pizzas** supera tres unidades; combos y bebidas no se cuentan como pizzas.
- **Descuento promocional:** se calcula por línea promocionable únicamente cuando la fecha del pedido pertenece al período configurado.
- **IVA de simulación:** 19 % sobre el subtotal **después de los descuentos**; se suma al total calculado.
- **Control de inventario:** ingredientes verificados para la orden completa; si faltan unidades, se rechaza y no se modifica el stock.
- **Cobro ante rechazo:** el total calculado puede mostrarse como referencia, pero el monto a cobrar es **$0,00** y no se asigna repartidor.
- **Repartidor más cercano:** se selecciona mediante distancias proporcionadas por el usuario; no se consultan rutas reales.
- **Teléfono:** para números móviles chilenos se utiliza `+56 (9) XXXX-XXXX`; para ciertos números internacionales se admite el patrón `+XX (XXX) XXX-XXXX` descrito en la guía.

**Consideraciones técnicas:** cada cantidad ingresada se valida individualmente entre 1 y 100. La suma de dos órdenes válidas puede superar 100 unidades: el sistema permite la consolidación y luego verifica la capacidad del inventario. Los importes se calculan con `Double`; la salida usa dos decimales y pueden aparecer diferencias de un centavo por redondeo al comparar totales individuales con el total combinado recalculado. En un sistema comercial se consideraría `BigDecimal`.

## 7. Justificación de decisiones de diseño (feedback de la semana 2)

Se consideraron las oportunidades de mejora señaladas por el docente:

1. **`MutableList` como colección principal de pedidos:** la cantidad de clientes registrados durante una ejecución no es fija. `MutableList<Pedido>` permite incorporar elementos mediante `add()`. La `List` de solo lectura se usa cuando los datos no necesitan modificarse (por ejemplo, repartidores); para evidenciar el uso de arreglos, se transforma la colección a `Array<Pedido>` con `toTypedArray()` al generar el reporte.
2. **Estados centralizados:** `EstadoPedido` utiliza `enum class`, evitando cadenas de texto inconsistentes.
3. **Polimorfismo comprobable:** una `List<Usuario>` contiene objetos `Cliente` y `Repartidor`, e invoca `mostrarInformacion()` mediante referencias del tipo base.
4. **Detalle de productos reutilizado:** el resumen utiliza `detallarProducto()`.
5. **Agrupación reutilizable:** `Reportes.agruparProductos()` se utiliza tanto en el resumen como en el reporte diario.
6. **Responsabilidades separadas:** lectura y validación en `utils`, entidades en `models` y visualización/demostración en `services`; `Main.kt` coordina el flujo.
7. **Cantidades explícitas:** `DetallePedido` representa la relación producto–cantidad sin duplicar instancias del mismo producto.
8. **Inventario encapsulado:** `Inventario` almacena su `MutableMap` de forma privada y expone una copia de solo lectura mediante `consultarStock()`.
9. **Correo válido sin valores ficticios:** el sistema vuelve a solicitar un correo inválido en vez de guardarlo como `correo@invalido.com`.
10. **Formato monetario centralizado:** `Reportes.dinero()` utiliza `NumberFormat` con `Locale` de Chile y dos decimales.

## 8. Pruebas funcionales realizadas

Las siguientes situaciones fueron ejecutadas y verificadas en consola durante el desarrollo:

| Caso | Datos utilizados | Resultado observado |
|---|---|---|
| Entrada inválida | Correo sin `@` y teléfono con formato incorrecto | Mensajes de error y reingreso hasta obtener valores válidos. |
| Cantidad fuera de rango | `102`, seguida de `99` | Rechaza `102` y acepta `99`. |
| Pedido combinado con promoción | Fecha `2026-11-06`; una pizza inicial más un combo y una bebida adicionales | Se ejecuta el operador `+`; total **$32.857,45**. |
| Stock insuficiente | Primer pedido de 99 pizzas y orden adicional de 5 pizzas más una bebida | Pedido rechazado; inventario permanece `{Masa=30, Salsa=30, Queso=60, Pepperoni=25}`, cobro `$0,00`. |
| Pedido simple fuera de promoción | Fecha `2026-10-01`; dos pizzas; segunda orden `n` | No combina órdenes; descuento promocional `$0,00`; total **$23.776,20**. |
| Asignación de repartidor | Distancias Carlos 4 km, Daniela 3 km y Pedro 2 km | Selecciona Pedro Rojas. |
| Comparación / duplicados | Clientes y productos equivalentes | `==` devuelve `true`, `Set` conserva un elemento por identidad lógica y `hashCode` es consistente. |
| Reflection y anotaciones | Ejecución del flujo principal | Muestra propiedades y métodos de `Pizza`, `Combo` y `Pedido`, y porcentajes de `@Promocionable`. |

También se recomienda, como control antes de la entrega, comprobar las fechas de inicio y fin del período promocional, un pedido sin orden adicional y el reporte con varios clientes.

## 9. Entrega académica

Las instrucciones específicas requieren **comprimir el proyecto Kotlin en ZIP o RAR y subirlo a la plataforma**.

Antes de comprimir:

1. Ejecutar **Build → Rebuild Project** en IntelliJ IDEA.
2. Ejecutar `Main.kt` y confirmar que termina sin errores.
3. Incluir esta documentación `README.md` en la raíz del proyecto, al mismo nivel de `src`.
4. Comprimir la **carpeta completa del proyecto IntelliJ** con sus archivos de configuración necesarios. Es posible excluir carpetas temporales o generadas (`out`, `.class`, etc.).

No se solicita obligatoriamente un informe en PDF ni capturas de pantalla en las instrucciones específicas de la actividad.
