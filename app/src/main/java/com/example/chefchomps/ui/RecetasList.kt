package com.example.chefchomps.ui

import android.content.Intent
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.chefchomps.model.Recipe

/**
 * Componente reutilizable para mostrar una lista de recetas.
 * 
 * @param recipes Lista de recetas a mostrar
 * @param isLoading Indica si se están cargando los datos
 * @param emptyMessage Mensaje a mostrar cuando no hay recetas
 * @param onRecetaClick Función a llamar cuando se hace clic en una receta (opcional)
 * @param modifier Modificador para personalizar el diseño
 */
@Composable
fun RecetasList(
    recipes: List<Recipe>,
    isLoading: Boolean = false,
    emptyMessage: String = "No se encontraron recetas",
    onRecetaClick: ((Recipe) -> Unit)? = null,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        when {
            isLoading -> {
                CircularProgressIndicator()
            }
            recipes.isEmpty() -> {
                Text(
                    text = emptyMessage,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    textAlign = TextAlign.Center
                )
            }
            else -> {
                LazyColumn(
                    modifier = Modifier.fillMaxSize()
                ) {
                    items(
                        items = recipes,
                        key = { recipe -> recipe.id ?: recipe.title.hashCode() }
                    ) { recipe ->
                        // Sólo permito hacer clic si hay un ID o si hay un callback personalizado
                        val enableClick = recipe.id != null || onRecetaClick != null
                        if (onRecetaClick != null) {
                            RowReceta(
                                recipe = recipe,
                                onClick = { 
                                    // Si el ID es nulo, informamos al usuario
                                    if (recipe.id == null) {
                                        // Podríamos mostrar un Toast o un Snackbar aquí
                                        // pero por ahora simplemente llamamos al callback
                                    }
                                    onRecetaClick(recipe) 
                                },
                                enabled = enableClick
                            )
                        } else {
                            RowReceta(recipe = recipe)
                        }
                    }
                }
            }
        }
    }
}

/**
 * Versión simplificada que utiliza un ViewModel directamente
 */
@Composable
fun RecetasListFromViewModel(
    viewModel: ViewModelPaginaPrincipal,
    isLoading: Boolean = false,
    emptyMessage: String = "No se encontraron recetas",
    onRecetaClick: ((Recipe) -> Unit)? = null,
    modifier: Modifier = Modifier
) {
    RecetasList(
        recipes = viewModel.getlist(),
        isLoading = isLoading,
        emptyMessage = emptyMessage,
        onRecetaClick = onRecetaClick,
        modifier = modifier
    )
} 