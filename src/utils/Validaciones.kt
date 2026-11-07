package utils

import java.time.LocalDate
import java.time.format.DateTimeParseException

object Validaciones {
    private val correoRegex = Regex("^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$")
    private val caracteresTelefono = Regex("^\\+?[0-9() -]+$")

    fun validarCorreo(correo: String): Boolean = correoRegex.matches(correo.trim())

    /**
     * +XX (XXX) XXX-XXXX para internacionales de 12 dígitos.
     * +56 (9) XXXX-XXXX para móviles de Chile (9 dígitos nacionales).
     */
    fun formatearTelefono(telefono: String): String? {
        val entrada = telefono.trim()
        if (!caracteresTelefono.matches(entrada)) return null
        val digitos = entrada.filter(Char::isDigit)
        val numeroChileno = when {
            digitos.length == 9 && digitos.startsWith("9") -> digitos
            digitos.length == 11 && digitos.startsWith("569") -> digitos.drop(2)
            else -> null
        }
        if (numeroChileno != null) {
            return "+56 (${numeroChileno[0]}) ${numeroChileno.substring(1, 5)}-${numeroChileno.substring(5)}"
        }
        if (entrada.startsWith("+") && digitos.length == 12) {
            return "+${digitos.substring(0, 2)} (${digitos.substring(2, 5)}) " +
                "${digitos.substring(5, 8)}-${digitos.substring(8)}"
        }
        return null
    }

    fun cantidadPermitida(cantidad: Int): Boolean = cantidad in 1..100

    fun parsearFecha(texto: String): LocalDate? = try {
        LocalDate.parse(texto.trim())
    } catch (_: DateTimeParseException) {
        null
    }
}
