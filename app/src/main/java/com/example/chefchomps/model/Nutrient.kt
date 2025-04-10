package com.example.chefchomps.model

/**
 * Clase de datos que representa los nutrientes de un alimento.
 *
 * @param name Nombre del nutriente.
 * @param amount Cantidad de nutrientes que incluye el alimento.
 * @param unit Unidad de medida para la cantidad de nutrientes.
 * @param percentOfDailyNeeds Porcentaje de la cantidad necesaria diaria del nutriente.
 */
data class Nutrient(
    val name: String,
    val amount: Double,
    val unit: String,
    val percentOfDailyNeeds: Double
)