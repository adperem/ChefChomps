package com.example.chefchomps.model

/**
 * Clase que define el tamaño de cada porcion
 * @param amount cantidad de unit
 * @param unit unidad de peso
 */
data class WeightPerServing(
    val amount: Int,
    val unit: String
)