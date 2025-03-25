package com.example.chefchomps.logica

import com.example.chefchomps.BuildConfig
import com.example.chefchomps.model.Ingredient
import com.example.chefchomps.model.Recipe
import com.example.chefchomps.model.WinePairing
import com.google.gson.JsonDeserializationContext
import com.google.gson.JsonDeserializer
import com.google.gson.JsonElement
import com.google.gson.reflect.TypeToken
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.lang.reflect.Type
import kotlinx.coroutines.awaitAll
import java.io.File
import java.util.Properties

class ApiCLient {

    companion object {

    private val BASE_URL = "https://api.spoonacular.com/" // Cambia esto según tu API
    private val API_KEY= BuildConfig.API_KEY

    private val retrofit: Retrofit by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    private val apiService: ApiService by lazy {
        retrofit.create(ApiService::class.java)
    }

    // Nuevo metodo para receta aleatoria
    suspend fun getRandomRecipe(): Result<Recipe> {
        return try {
            val response = apiService.getRandomRecipe(API_KEY)
            if (response.isSuccessful) {
                val recipes = response.body()?.recipes ?: emptyList()
                Result.success(recipes.firstOrNull() ?: throw Exception("No recipe returned"))
            } else {
                Result.failure(Exception("Error: ${response.code()} - ${response.message()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun findRecipesByIngredients(ingredients: List<String>): Result<List<Recipe>> {
        return try {
            val ingredientsQuery =
                ingredients.joinToString(",") // Convierte la lista en "carrots,tomatoes"
            val response = apiService.findRecipesByIngredients(
                apiKey = API_KEY,
                ingredients = ingredientsQuery,
                number = 10, // Valor fijo como en tu ejemplo
                ignorePantry = false // Valor fijo como en tu ejemplo
            )
            if (response.isSuccessful) {
                Result.success(response.body() ?: emptyList())
            } else {
                Result.failure(Exception("Error: ${response.code()} - ${response.message()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun getRecipeInformation(id: Int): Result<Recipe> {
        return try {
            val response = apiService.getRecipeInformation(id, API_KEY)
            if (response.isSuccessful) {
                Result.success(response.body() ?: throw Exception("No recipe information returned"))
            } else {
                Result.failure(Exception("Error: ${response.code()} - ${response.message()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun autocompleteRecipes(query: String): Result<List<Recipe>> = coroutineScope {
        return@coroutineScope try {
            val autocompleteResponse = apiService.autocompleteRecipes(API_KEY, query)
            if (autocompleteResponse.isSuccessful) {
                val suggestions = autocompleteResponse.body() ?: emptyList()
                val detailedRecipes = suggestions.map { suggestion ->
                    async { getRecipeInformation(suggestion.id) }
                }.awaitAll().mapNotNull { result ->
                    result.getOrNull() // Solo incluir recetas obtenidas con éxito
                }
                Result.success(detailedRecipes)
            } else {
                Result.failure(Exception("Error en autocompletado: ${autocompleteResponse.code()} - ${autocompleteResponse.message()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}


}

class RecipeDeserializer : JsonDeserializer<Recipe> {
    override fun deserialize(json: JsonElement, typeOfT: Type, context: JsonDeserializationContext): Recipe {
        val jsonObject = json.asJsonObject
        val analyzedInstructions = jsonObject.getAsJsonArray("analyzedInstructions")
            ?.flatMap { instruction ->
                instruction.asJsonObject.getAsJsonArray("steps")
                    ?.map { it.asJsonObject.get("step").asString } ?: emptyList()
            } ?: emptyList()

        // Asumimos valores por defecto para campos faltantes como winePairing y otros
        return Recipe(
            id = jsonObject.get("id").asInt,
            title = jsonObject.get("title").asString,
            image = jsonObject.get("image").asString,
            imageType = jsonObject.get("imageType").asString,
            servings = jsonObject.get("servings").asInt,
            readyInMinutes = jsonObject.get("readyInMinutes").asInt,
            cookingMinutes = jsonObject.get("cookingMinutes")?.asInt,
            preparationMinutes = jsonObject.get("preparationMinutes")?.asInt,
            license = jsonObject.get("license")?.asString,
            sourceName = jsonObject.get("sourceName").asString,
            sourceUrl = jsonObject.get("sourceUrl").asString,
            spoonacularSourceUrl = jsonObject.get("spoonacularSourceUrl").asString,
            healthScore = jsonObject.get("healthScore").asDouble,
            spoonacularScore = jsonObject.get("spoonacularScore").asDouble,
            pricePerServing = jsonObject.get("pricePerServing").asDouble,
            analyzedInstructions = analyzedInstructions,
            cheap = jsonObject.get("cheap").asBoolean,
            creditsText = jsonObject.get("creditsText").asString,
            cuisines = jsonObject.getAsJsonArray("cuisines")?.map { it.asString } ?: emptyList(),
            dairyFree = jsonObject.get("dairyFree").asBoolean,
            diets = jsonObject.getAsJsonArray("diets")?.map { it.asString } ?: emptyList(),
            gaps = jsonObject.get("gaps").asString,
            glutenFree = jsonObject.get("glutenFree").asBoolean,
            instructions = jsonObject.get("instructions").asString,
            ketogenic = jsonObject.get("ketogenic")?.asBoolean ?: false, // Valor por defecto
            lowFodmap = jsonObject.get("lowFodmap").asBoolean,
            occasions = jsonObject.getAsJsonArray("occasions")?.map { it.asString } ?: emptyList(),
            sustainable = jsonObject.get("sustainable").asBoolean,
            vegan = jsonObject.get("vegan").asBoolean,
            vegetarian = jsonObject.get("vegetarian").asBoolean,
            veryHealthy = jsonObject.get("veryHealthy").asBoolean,
            veryPopular = jsonObject.get("veryPopular").asBoolean,
            whole30 = jsonObject.get("whole30")?.asBoolean ?: false, // Valor por defecto
            weightWatcherSmartPoints = jsonObject.get("weightWatcherSmartPoints").asInt,
            dishTypes = jsonObject.getAsJsonArray("dishTypes")?.map { it.asString } ?: emptyList(),
            extendedIngredients = context.deserialize(jsonObject.get("extendedIngredients"), object : TypeToken<List<Ingredient>>() {}.type),
            summary = jsonObject.get("summary").asString,
            winePairing = WinePairing() // Valor por defecto
        )
    }
}