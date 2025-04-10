package com.example.chefchomps.model

/**
 * Clase de datos que representa la distribución calórica de un alimento.
 *
 * @param percentProtein Porcentaje de proteínas en el alimento.
 * @param percentFat Porcentaje de grasas en el alimento.
 * @param percentCarbs Porcentaje de carbohidratos en el alimento.
 */
data class CaloricBreakdown(
    val percentProtein: Double,
    val percentFat: Double,
    val percentCarbs: Double
)