package com.example.chefchomps.model

/**
 * Clase de datos que representa el flavonoide de un alimento.
 *
 * @param name Nombre del flavonoide que contiene el alimento.
 * @param amount Cantidad de flavonoide que contiene el alimento.
 * @param unit Unidad de medida del flavonoide del alimento.
 */
data class Flavonoid(
    val name: String,
    val amount: Double,
    val unit: String
)