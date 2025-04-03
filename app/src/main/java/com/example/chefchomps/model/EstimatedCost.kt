package com.example.chefchomps.model

/**
 * Clase de datos que representa el coste de un alimento.
 *
 * @param value Valor del alimento.
 * @param unit Cantidad de unidades del alimento.
 */
data class EstimatedCost(
    val value: Double,
    val unit: String
)