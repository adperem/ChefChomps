package com.example.chefchomps.ui

import androidx.lifecycle.ViewModel
import com.example.chefchomps.logica.ApiCLient
import com.example.chefchomps.model.Recipe
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.runBlocking

/**
 * Clase para guardar todos los datos cambiantes de la página principal
 * @param lrecipe lista de recetas
 */
data class UIPrincipalPageData(
    val lrecipe:MutableList<Recipe> =ArrayList(),
)

/**
 * Clase donde se tocan los datos que hay de UI de la página principal
 */
class ViewModelPaginaPrincipal(): ViewModel() {

    private val _uiState = MutableStateFlow(UIPrincipalPageData())
    val uiState: StateFlow<UIPrincipalPageData> = _uiState.asStateFlow()

    /**
     * Borra toda la información asociada a recetas
     */
    fun clear()
    {
        _uiState.update{
                currentstate:UIPrincipalPageData->
            val lrecip:MutableList<Recipe> =ArrayList()
            currentstate.copy(lrecipe=lrecip)
        }
    }
    /**
     * Añade recetas a la lista que ya tienes
     * @param list es el resultado de una lista de recetas
     */
    fun updatelist(
        list: Result<List<Recipe>> =runBlocking{ApiCLient.findRecipesByIngredients(
            ingredients = List<String>(10,{"pineapple"})
        )}
    ){
        _uiState.update{
                currentstate:UIPrincipalPageData->
            val lrecip:MutableList<Recipe> =ArrayList()
            lrecip.addAll(currentstate.lrecipe)
            val rec : Result<List<Recipe>> = list;
            if(rec.isSuccess){
                rec.getOrNull()?.let { it1 -> lrecip.addAll(it1) } }

            currentstate.copy(lrecipe=lrecip)

        }
    }
    /**
     * Devuelve la lista de recetas
     * @return Devuelve una lista de recetas que esten en el ViewModel
     */
    fun getlist():List<Recipe>{
        return uiState.value.lrecipe
    }
}
