package com.example.chefchomps.model

data class Receta(
    val id: Int,
    val title: String,
    val image: String,
    val imageType: String,
    val servings: Int,
    val readyInMinutes: Int,
    val cookingMinutes: Int,
    val preparationMinutes: Int,
    val license: String,
    val sourceName: String,
    val sourceUrl: String,
    val spoonacularSourceUrl: String,
    val healthScore: Double,
    val spoonacularScore: Double,
    val pricePerServing: Double,
    val analyzedInstructions: List<String> = emptyList(), // Lista vacía por defecto
    val cheap: Boolean,
    val creditsText: String,
    val cuisines: List<String> = emptyList(),
    val dairyFree: Boolean,
    val diets: List<String> = emptyList(),
    val gaps: String,
    val glutenFree: Boolean,
    val instructions: String,
    val ketogenic: Boolean,
    val lowFodmap: Boolean,
    val occasions: List<String> = emptyList(),
    val sustainable: Boolean,
    val vegan: Boolean,
    val vegetarian: Boolean,
    val veryHealthy: Boolean,
    val veryPopular: Boolean,
    val whole30: Boolean,
    val weightWatcherSmartPoints: Int,
    val dishTypes: List<String>,
    val extendedIngredients: List<Ingrediente>,
    val summary: String,
    val winePairing: WinePairing













)