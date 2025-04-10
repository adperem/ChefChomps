package com.example.chefchomps.model


/**
 * Clase contenedor que define la cantidad de gente a la que se puede alimentar con el plato
 * @param number cantidad de porciones
 * @param size tamaño de la porcion
 * @param unit unidad en las que se mide
 * @param raw cadena que contiene el resto de datos en el formato original que se envio
 */
data class Servings(
    val number: Int,
    val size: Int,
    val unit: String,
    val raw: String
)