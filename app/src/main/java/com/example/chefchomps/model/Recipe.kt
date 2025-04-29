package com.example.chefchomps.model

import java.util.Date

/**
 * Clase que contiene todos los datos de la receta que usa ApiService
 */
data class Recipe(
    val id: Int? = null,
    val title: String,
    val image: String? = null,
    val servings: Int? = null,
    val readyInMinutes: Int? = null,
    val cookingMinutes: Int? = null,
    val preparationMinutes: Int? = null,
    val healthScore: Double? = null,
    val spoonacularScore: Double? = null,
    val pricePerServing: Double? = null,
    val analyzedInstructions: List<Instruction>? = null,
    val cheap: Boolean? = null,
    val cuisines: List<String>? = null,
    val dairyFree: Boolean? = null,
    val diets: List<String>? = null,
    val glutenFree: Boolean? = null,
    val instructions: String? = null,
    val ketogenic: Boolean? = null,
    val lowFodmap: Boolean? = null,
    val vegan: Boolean? = null,
    val vegetarian: Boolean? = null,
    val veryHealthy: Boolean? = null,
    val veryPopular: Boolean? = null,
    val dishTypes: List<String>? = null,
    val extendedIngredients: List<Ingredient>? = null,
    val summary: String? = null,
    val userId: String? = null,
    val createdAt: Date = Date(),
    val winePairing: WinePairing? = null,
    val whole30: Boolean? = null,
    val weightWatcherSmartPoints: Int? = null,
    val occasions: List<String>? = null,
    val sustainable: Boolean? = null,
    val gaps: String? = null,
    val creditsText: String? = null,
    val license: String? = null,
    val sourceName: String? = null,
    val sourceUrl: String? = null,
    val spoonacularSourceUrl: String? = null,
    val imageType: String? = null
)

/**
 * Clase contenedor para las intrucciones para realizar una receta
 */
data class Instruction(
    val name: String,
    val steps: List<Step>
)

/**
 * Clase contenedor para las intrucciones más simples a realizar en una instrucción
 */
data class Step(
    val number: Int,
    val step: String,
    val ingredients: List<Ingredient>?,
    val equipment: List<Equipment>?
)

/**
 * Clase contenedor para los utensilios necesarios para realizar una receta
 */
data class Equipment(
    val id: Int,
    val name: String,
    val localizedName: String,
    val image: String
)