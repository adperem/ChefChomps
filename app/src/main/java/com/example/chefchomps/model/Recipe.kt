package com.example.chefchomps.model

data class Recipe(
    val id: Int,
    val title: String,
    val image: String,
    val imageType: String,
    val servings: Int,
    val readyInMinutes: Int,
    val cookingMinutes: Int?,
    val preparationMinutes: Int?,
    val license: String?,
    val sourceName: String,
    val sourceUrl: String,
    val spoonacularSourceUrl: String,
    val healthScore: Double,
    val spoonacularScore: Double,
    val pricePerServing: Double,
    val analyzedInstructions: List<Instruction>, // Cambiado a List<Instruction>
    val cheap: Boolean,
    val creditsText: String,
    val cuisines: List<String>,
    val dairyFree: Boolean,
    val diets: List<String>,
    val gaps: String,
    val glutenFree: Boolean,
    val instructions: String,
    val ketogenic: Boolean,
    val lowFodmap: Boolean,
    val occasions: List<String>,
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

data class Instruction(
    val name: String,
    val steps: List<Step>
)

data class Step(
    val number: Int,
    val step: String,
    val ingredients: List<Ingredient>?,
    val equipment: List<Equipment>?
)

data class Equipment(
    val id: Int,
    val name: String,
    val localizedName: String,
    val image: String
)