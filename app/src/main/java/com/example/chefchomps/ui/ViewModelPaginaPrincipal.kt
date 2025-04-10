package com.example.chefchomps.ui

import androidx.lifecycle.ViewModel
import com.example.chefchomps.logica.ApiCLient
import com.example.chefchomps.logica.DatabaseHelper
import com.example.chefchomps.model.Recipe
import com.example.chefchomps.model.Usuario
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.launch

/**
 * Clase para guardar todos los datos cambiantes de la página principal
 * @param lrecipe lista de recetas
 * @param isMenuExpanded estado del menú desplegable
 * @param currentUser usuario actual si está logueado
 */
data class UIPrincipalPageData(
    val lrecipe: MutableList<Recipe> = ArrayList(),
    val isMenuExpanded: Boolean = false,
    val currentUser: Usuario? = null
)

/**
 * Clase donde se tocan los datos que hay de UI de la página principal
 */
class ViewModelPaginaPrincipal : ViewModel() {
    private val databaseHelper = DatabaseHelper.getInstance()

    private val _uiState = MutableStateFlow(UIPrincipalPageData())
    val uiState: StateFlow<UIPrincipalPageData> = _uiState.asStateFlow()

    init {
        // Observar cambios en el estado de autenticación
        databaseHelper.auth.addAuthStateListener { firebaseAuth ->
            val user = firebaseAuth.currentUser
            if (user != null) {
                loadUserProfile()
            } else {
                _uiState.update { it.copy(currentUser = null) }
            }
        }
    }

    private fun loadUserProfile() {
        viewModelScope.launch {
            try {
                val usuario = databaseHelper.getUserProfile()
                _uiState.update { it.copy(currentUser = usuario) }
            } catch (e: Exception) {
                _uiState.update { it.copy(currentUser = null) }
            }
        }
    }

    /**
     * Alterna el estado del menú desplegable
     */
    fun toggleMenu() {
        _uiState.update { it.copy(isMenuExpanded = !it.isMenuExpanded) }
    }

    /**
     * Borra toda la información asociada a recetas
     */
    fun clear() {
        _uiState.update { currentstate: UIPrincipalPageData ->
            val lrecip: MutableList<Recipe> = ArrayList()
            currentstate.copy(lrecipe = lrecip)
        }
    }

    /**
     * Añade recetas a la lista que ya tienes
     * @param list es el resultado de una lista de recetas
     */
    fun updatelist(
        list: Result<List<Recipe>> = runBlocking {
            ApiCLient.findRecipesByIngredients(
                ingredients = List(10) { "pineapple" }
            )
        }
    ) {
        _uiState.update { currentstate: UIPrincipalPageData ->
            val lrecip: MutableList<Recipe> = ArrayList()
            lrecip.addAll(currentstate.lrecipe)
            val rec: Result<List<Recipe>> = list
            if (rec.isSuccess) {
                rec.getOrNull()?.let { it1 -> lrecip.addAll(it1) }
            }
            currentstate.copy(lrecipe = lrecip)
        }
    }

    /**
     * Devuelve la lista de recetas
     * @return Devuelve una lista de recetas que estén en el ViewModel
     */
    fun getlist(): List<Recipe> {
        return uiState.value.lrecipe
    }

    /**
     * Cierra la sesión del usuario actual
     */
    fun signOut() {
        databaseHelper.auth.signOut()
    }
}
