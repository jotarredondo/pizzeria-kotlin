package annotations

/** Identifica categorías de producto que pueden recibir promoción. */
@Target(AnnotationTarget.CLASS)
@Retention(AnnotationRetention.RUNTIME)
annotation class Promocionable(val porcentaje: Int)
