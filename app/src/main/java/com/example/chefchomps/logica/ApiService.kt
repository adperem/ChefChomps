package com.example.chefchomps.logica
import com.example.chefchomps.model.Recipe
import retrofit2.http.GET
import retrofit2.http.Query
import retrofit2.Response
import retrofit2.http.Path

interface ApiService {

    // Nuevo metodo para receta aleatoria
    @GET("recipes/random")
    suspend fun getRandomRecipe(
        @Query("apiKey") apiKey: String,
        @Query("number") number: Int = 1 // Por defecto 1, según tu ejemplo
    ): Response<RandomRecipeResponse>

    @GET("recipes/findByIngredients")
    suspend fun findRecipesByIngredients(
        @Query("apiKey") apiKey: String,
        @Query("ingredients") ingredients: String, // Lista de ingredientes separada por comas
        @Query("number") number: Int = 10, // Número de resultados, por defecto 10
        @Query("ignorePantry") ignorePantry: Boolean = false // Ignorar ingredientes de despensa
    ): Response<List<Recipe>> // Devuelve una lista directamente, no envuelta en un objeto

    @GET("recipes/{id}/information")
    suspend fun getRecipeInformation(
        @Path("id") id: Int,
        @Query("apiKey") apiKey: String
    ): Response<Recipe> // Devuelve directamente un objeto Recipe
    @GET("recipes/autocomplete")
    suspend fun autocompleteRecipes(
        @Query("apiKey") apiKey: String,
        @Query("query") query: String,
        @Query("number") number: Int = 10
    ): Response<List<AutocompleteRecipe>> // Devuelve una lista de AutocompleteRecipe

}

data class RandomRecipeResponse(
    val recipes: List<Recipe>
)
data class AutocompleteRecipe(
    val id: Int,
    val title: String,
    val imageType: String
)
