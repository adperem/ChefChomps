package com.example.chefchomps.model

// Clase para la información nutricional
data class Nutrition(
    val nutrients: List<Nutrient>,
    val properties: List<Property>,
    val flavonoids: List<Flavonoid>,
    val caloricBreakdown: CaloricBreakdown,
    val weightPerServing: WeightPerServing
)