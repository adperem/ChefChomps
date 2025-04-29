package com.example.chefchomps.model

/**
 * Clase de datos que representa un ingrediente, con información detallada sobre su nombre,
 * cantidad, unidad, costo estimado, nutrición y otros atributos relacionados con la preparación
 * y compra del ingrediente.
 *
 * @param id El identificador único del ingrediente.
 * @param original El nombre original del ingrediente tal como aparece en la receta.
 * @param originalName El nombre original del ingrediente en su formato completo.
 * @param name El nombre común del ingrediente.
 * @param amount La cantidad del ingrediente utilizada en la receta, expresada como un número decimal.
 * @param unit La unidad de medida de la cantidad del ingrediente.
 * @param unitShort La unidad de medida abreviada del ingrediente.
 * @param unitLong La unidad de medida extendida del ingrediente.
 * @param possibleUnits Lista de unidades de medida posibles para el ingrediente.
 * @param estimatedCost El costo estimado del ingrediente, representado por un objeto 'EstimatedCost'.
 * @param consistency La consistencia del ingrediente.
 * @param shoppingListUnits Lista de unidades recomendadas para la lista de compras.
 * @param aisle El pasillo en la tienda donde se encuentra el ingrediente.
 * @param image URL de la imagen del ingrediente.
 * @param meta Lista de metadatos relacionados con el ingrediente.
 * @param nutrition Información nutricional del ingrediente, representada por un objeto 'Nutrition'.
 * @param categoryPath Lista de categorías en las que el ingrediente está clasificado.
 */
data class Ingredient(
    val id: Int,
    val original: String,
    val originalName: String,
    val name: String,
    val amount: Double,
    val unit: String?,
    val unitShort: String?,
    val unitLong: String?,
    val possibleUnits: List<String>?,
    val estimatedCost: EstimatedCost?,
    val consistency: String?,
    val shoppingListUnits: List<String>?,
    val aisle: String?,
    val image: String?,
    val meta: List<String>?,
    val nutrition: Nutrition?,
    val categoryPath: List<String>?
)