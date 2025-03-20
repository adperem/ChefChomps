package com.example.chefchomps.ui

import androidx.lifecycle.ViewModel
import com.example.chefchomps.logica.ApiCLient
import com.example.chefchomps.model.Recipe
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

data class UIPrincipalPageData(
    val lrecipe:MutableList<Recipe> =ArrayList(),


)
class ViewModelPaginaPrincipal(): ViewModel() {

    private val _uiState = MutableStateFlow(UIPrincipalPageData())
    val uiState: StateFlow<UIPrincipalPageData> = _uiState.asStateFlow()

    suspend fun update(){
        _uiState.update{
            currentstate:UIPrincipalPageData->
            val lrecip:MutableList<Recipe> =ArrayList()
            lrecip.addAll(currentstate.lrecipe)
            for (i in 0..10){
                val rec : Result<Recipe> = ApiCLient.getRandomRecipe();
                if(rec.isSuccess){
                    rec.getOrNull()?.let { it1 -> lrecip.add(it1) }
                }
            }
            currentstate.copy(lrecipe=lrecip)

    }
    }
    fun getlist():List<Recipe>{
        return uiState.value.lrecipe
    }
}
