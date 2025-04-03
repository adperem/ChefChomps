package com.example.chefchomps.model

/**
 * Clase que representa la información nutricional
 *
 * @param nutrients Lista de nutrientes del alimento.
 * @param properties Lista de propiedades del alimento.
 * @param flavonoids Lista de flavonoides del alimento.
 * @param caloricBreakdown Distribución calórica del alimento.
 * @param weightPerServing Peso del alimento por porción.
 */
data class Nutrition(
    val nutrients: List<Nutrient>,
    val properties: List<Property>,
    val flavonoids: List<Flavonoid>,
    val caloricBreakdown: CaloricBreakdown,
    val weightPerServing: WeightPerServing
)