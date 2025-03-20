package com.example.chefchomps.model

data class Product(
    val id: Int,
    val title: String,
    val breadcrumbs: List<String>,
    val imageType: String,
    val badges: List<String>,
    val importantBadges: List<String>,
    val ingredientCount: Int,
    val generatedText: String?,
    val ingredientList: String,
    val ingredients: List<Ingredient>,
    val likes: Int,
    val aisle: String,
    val nutrition: Nutrition,
    val price: Double,
    val servings: Servings,
    val spoonacularScore: Double
)