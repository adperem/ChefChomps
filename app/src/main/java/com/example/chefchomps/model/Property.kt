package com.example.chefchomps.model
/**
 * Clase contenedor para las propiedades nutricionles
 * @param name nombre de la propiedad
 * @param amount cantidad de name
 * @param unit unidad de medida de name
 */
data class Property(
    val name: String,
    val amount: Double,
    val unit: String
)