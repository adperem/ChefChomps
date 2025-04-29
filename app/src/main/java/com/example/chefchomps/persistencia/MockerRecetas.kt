package com.example.chefchomps.persistencia

import com.example.chefchomps.model.Recipe

class MockerRecetas (
){
fun Recetas(){
    val lrep = mutableListOf<Recipe>()
    for(i in 1..10){
        lrep.add(Recipe(
            id=i,
            title = "i"+i,
            image = TODO(),
            imageType = TODO(),
            servings = TODO(),
            readyInMinutes = TODO(),
            cookingMinutes = TODO(),
            preparationMinutes = TODO(),
            license = TODO(),
            sourceName = TODO(),
            sourceUrl = TODO(),
            spoonacularSourceUrl = TODO(),
            healthScore = TODO(),
            spoonacularScore = TODO(),
            pricePerServing = TODO(),
            analyzedInstructions = TODO(),
            cheap = TODO(),
            creditsText = TODO(),
            cuisines = TODO(),
            dairyFree = TODO(),
            diets = TODO(),
            gaps = TODO(),
            glutenFree = TODO(),
            instructions = TODO(),
            ketogenic = TODO(),
            lowFodmap = TODO(),
            occasions = TODO(),
            sustainable = TODO(),
            vegan = TODO(),
            vegetarian = TODO(),
            veryHealthy = TODO(),
            veryPopular = TODO(),
            whole30 = TODO(),
            weightWatcherSmartPoints = TODO(),
            dishTypes = TODO(),
            extendedIngredients = TODO(),
            summary = TODO(),
            winePairing = TODO(),
        ))
    }
    return
}
}