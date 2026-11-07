package models

import java.time.LocalDate

data class PeriodoPromocion(val inicio: LocalDate, val fin: LocalDate) {
    init { require(!fin.isBefore(inicio)) { "La fecha final debe ser posterior o igual a la inicial." } }

    /** Rango inclusivo; ejercicio académico. */
    fun contiene(fecha: LocalDate): Boolean = fecha in inicio..fin
}
