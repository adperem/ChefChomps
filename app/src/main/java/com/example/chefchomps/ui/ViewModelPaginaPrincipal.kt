package com.example.chefchomps.ui

import androidx.lifecycle.ViewModel
import com.example.chefchomps.logica.ApiCLient
import com.example.chefchomps.model.Recipe
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.runBlocking

data class UIPrincipalPageData(
    val lrecipe:MutableList<Recipe> =ArrayList(),


)
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
     */
    fun updatelist(
        list: Result<List<Recipe>> =runBlocking{ApiCLient.findRecipesByIngredients(
            ingredients = List<String>(10,{"piña"})
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
     */
    fun getlist():List<Recipe>{
        return uiState.value.lrecipe
    }
}
