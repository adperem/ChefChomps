package com.example.chefchomps.persistencia


import com.example.chefchomps.model.Ingredient
import com.example.chefchomps.model.Instruction
import com.example.chefchomps.model.Recipe
import com.example.chefchomps.model.Step
class MockerRecetas (
){
    companion object{
fun Recetas(): MutableList<Recipe> {
    val lrep = mutableListOf<Recipe>()
    val analyzedInstructions= mutableListOf<Instruction>()
    val steps= mutableListOf<Step>()
    val ingredients= mutableListOf<Ingredient>()
    for(i in 1..10){
        ingredients.add(
            Ingredient(
                id = i,
                original = "null$i",
                originalName = "null$i",
                name = "null$i",
                amount = 125.0+i,
                unit = null,
                unitShort = null,
                unitLong = null,
                possibleUnits = null,
                estimatedCost = null,
                consistency = null,
                shoppingListUnits = null,
                aisle = null,
                image = null,
                meta = null,
                nutrition = null,
                categoryPath = null,
            )
        )

    }
    for(i in 1..10){
        steps.add(
            Step(
                number = i,
                step = "($i)jiuojdfsjfoisjdfgfdgdoajs$i",
                ingredients = ingredients,
                equipment = null,
            )
            )

    }
    for(i in 1..10){
        analyzedInstructions.add(
            Instruction(
                name = "df ghfbsdjfb$i",
                steps = steps
            )
        )
    }
    for(i in 1..10){
        lrep.add(
            Recipe(
                id = i,
                title = "i" + i,
                image="https://nt.gov.au/__data/assets/image/0007/227491/lemon.jpg",
                imageType = "jpg",
                vegan = false,
                analyzedInstructions = analyzedInstructions,
        ))
    }
    return lrep;
}}
}