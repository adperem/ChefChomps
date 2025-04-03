package com.example.chefchomps.logica
import com.example.chefchomps.model.Recipe
import retrofit2.http.GET
import retrofit2.http.Query
import retrofit2.Response
import retrofit2.http.Path

interface ApiService {
    /**
     *  Nuevo metodo para receta aleatoria
     *  @param apiKey valor de la key al API spoonacular
     *  @param number numero de elementos que recoje
     *  @return devuelve una respuesta que es una lista de recetas
     */
    @GET("recipes/random")
    suspend fun getRandomRecipe(
        @Query("apiKey") apiKey: String,
        @Query("number") number: Int = 10 // Por defecto 10, según tu ejemplo
    ): Response<getRandomRecipe>

    /**
     *  Metodo para recojer recetas de determinados ingredientes
     *  @param apiKey valor de la key al API spoonacular
     *  @param ingredients es una cadena de ingredientes separadas por coma
     *  @param number numero de elementos que recoge
     *  @return devuelve una respuesta que es una lista de recetas
     */
    @GET("recipes/findByIngredients")
    suspend fun findRecipesByIngredients(
        @Query("apiKey") apiKey: String,
        @Query("ingredients") ingredients: String, // Lista de ingredientes separada por comas
        @Query("number") number: Int = 10, // Número de resultados, por defecto 10
        @Query("ignorePantry") ignorePantry: Boolean = false // Ignorar ingredientes de despensa
    ): Response<List<Recipe>> // Devuelve una lista directamente, no envuelta en un objeto
    /**
     *  Metodo para recoger recetas de determinados ingredientes
     *  @param apiKey valor de la key al API spoonacular
     *  @param id es el número asignado a una receta
     *  @return devuelve una respuesta que es una receta
     */
    @GET("recipes/{id}/information")
    suspend fun getRecipeInformation(
        @Path("id") id: Int,
        @Query("apiKey") apiKey: String
    ): Response<Recipe> // Devuelve directamente un objeto Recipe

    /**
     *  Metodo para recoger las posibles continuaciones a la cadena
     *  @param apiKey valor de la key al API spoonacular
     *  @param number numero de elementos que recoge
     *  @param query es la cadena de la que se quiere autocompletar
     *  @return devuelve una respuesta que es una receta
     */
    @GET("recipes/autocomplete")
    suspend fun autocompleteRecipes(
        @Query("apiKey") apiKey: String,
        @Query("query") query: String,
        @Query("number") number: Int = 10
    ): Response<List<AutocompleteRecipe>> // Devuelve una lista de AutocompleteRecipe

}

/**
 * clase wraper del resultado de randomrecipe
 */
data class getRandomRecipe(
    val recipes: List<Recipe>
)

/**
 * clase wraper del resultado de AutocompleteRecipe
 */
data class AutocompleteRecipe(
    val id: Int,
    val title: String,
    val imageType: String
)
