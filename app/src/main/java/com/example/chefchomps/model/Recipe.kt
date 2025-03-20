package com.example.chefchomps.model

data class Recipe(
    val id: Int,
    val title: String,
    val image: String,
    val imageType: String,
    val servings: Int,
    val readyInMinutes: Int,
    val cookingMinutes: Int?, // Nullable porque puede ser null en el JSON
    val preparationMinutes: Int?, // Nullable porque puede ser null en el JSON
    val license: String?, // Nullable porque puede ser null en el JSON
    val sourceName: String,
    val sourceUrl: String,
    val spoonacularSourceUrl: String,
    val healthScore: Double,
    val spoonacularScore: Double,
    val pricePerServing: Double,
    val analyzedInstructions: List<String> = emptyList(), // Lista de pasos como texto
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
    val extendedIngredients: List<Ingredient>,
    val summary: String,
    val winePairing: WinePairing
)
