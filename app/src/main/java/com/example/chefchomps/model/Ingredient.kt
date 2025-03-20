package com.example.chefchomps.model

data class Ingredient(
    val id: Int,
    val original: String,
    val originalName: String,
    val name: String,
    val amount: Double,
    val unit: String,
    val unitShort: String,
    val unitLong: String,
    val possibleUnits: List<String>,
    val estimatedCost: EstimatedCost,
    val consistency: String,
    val shoppingListUnits: List<String>,
    val aisle: String,
    val image: String,
    val meta: List<String>,
    val nutrition: Nutrition,
    val categoryPath: List<String>
)