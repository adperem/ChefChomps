package com.example.chefchomps.model

// Clase para un nutriente
data class Nutrient(
    val name: String,
    val amount: Double,
    val unit: String,
    val percentOfDailyNeeds: Double
)