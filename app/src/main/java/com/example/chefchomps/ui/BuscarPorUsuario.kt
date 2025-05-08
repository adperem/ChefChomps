package com.example.chefchomps.ui

import ChefChompsTema
import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import com.example.chefchomps.logica.DatabaseHelper
import com.example.chefchomps.model.Recipe
import kotlinx.coroutines.runBlocking
import com.google.gson.Gson

class BuscarPorUsuario : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            ChefChompsTema(darkTheme = false) {
                Surface(modifier = Modifier.fillMaxSize()) {
                    BuscarPorUsuarioScreen(onBack = { finish() })
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BuscarPorUsuarioScreen(onBack: () -> Unit) {
    val context = LocalContext.current
    val databaseHelper = remember { DatabaseHelper() }
    var searchText by remember { mutableStateOf("") }
    var isSearching by remember { mutableStateOf(false) }
    var userRecipes by remember { mutableStateOf<List<Recipe>>(emptyList()) }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    val focusManager = LocalFocusManager.current
    val textFieldFocusRequester = remember { FocusRequester() }
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Buscar por Usuario") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Volver")
                    }
                }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp)
        ) {
            // Campo de búsqueda
            OutlinedTextField(
                value = searchText,
                onValueChange = { 
                    searchText = it
                    errorMessage = null
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .focusRequester(textFieldFocusRequester),
                placeholder = { Text("Ingrese nombre de usuario...") },
                leadingIcon = { 
                    Icon(
                        Icons.Default.Search,
                        contentDescription = "Buscar"
                    )
                },
                trailingIcon = {
                    if (searchText.isNotEmpty()) {
                        IconButton(onClick = {
                            searchText = ""
                            errorMessage = null
                            focusManager.clearFocus()
                        }) {
                            Icon(
                                Icons.Default.Clear,
                                contentDescription = "Limpiar"
                            )
                        }
                    }
                },
                singleLine = true,
                shape = RoundedCornerShape(24.dp),
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
                keyboardActions = KeyboardActions(
                    onSearch = {
                        if (searchText.isNotEmpty()) {
                            isSearching = true
                            errorMessage = null
                            focusManager.clearFocus()
                            
                            // Proceso de búsqueda en dos pasos
                            runBlocking {
                                try {
                                    // Paso 1: Buscar el ID del usuario por su nombre
                                    val userId = databaseHelper.buscarIdUsuarioPorNombre(searchText)
                                    
                                    if (userId != null) {
                                        // Paso 2: Buscar recetas asociadas al ID del usuario
                                        userRecipes = databaseHelper.buscarRecetasPorIdUsuario(userId) ?: emptyList()
                                    } else {
                                        // Usuario no encontrado
                                        userRecipes = emptyList()
                                        errorMessage = "Usuario '$searchText' no encontrado"
                                    }
                                } catch (e: Exception) {
                                    // Error en la búsqueda
                                    userRecipes = emptyList()
                                    errorMessage = "Error al buscar: ${e.message}"
                                } finally {
                                    isSearching = false
                                }
                            }
                        }
                    }
                )
            )
            
            // Mostrar mensaje de error si existe
            errorMessage?.let {
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = it,
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.bodyMedium
                )
            }
            
            Spacer(modifier = Modifier.height(16.dp))
            
            // Indicador de carga
            if (isSearching) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            } else {
                // Mostrar resultados
                RecetasList(
                    recipes = userRecipes,
                    emptyMessage = if (searchText.isEmpty()) 
                        "Ingrese un nombre de usuario para buscar sus recetas" 
                    else if (errorMessage == null)
                        "No se encontraron recetas para el usuario '$searchText'"
                    else
                        "",
                    onRecetaClick = { receta ->
                        // Convertir la receta a JSON para enviar todos los detalles
                        val gson = Gson()
                        val recetaJson = gson.toJson(receta)
                        
                        // Navegación a la pantalla de detalle con la receta completa
                        val intent = Intent(context, PaginaDetalle::class.java)
                        intent.putExtra("receta_json", recetaJson)
                        // Indicamos que la receta viene de Firebase, no de la API
                        intent.putExtra("from_firebase", true)
                        context.startActivity(intent)
                    }
                )
            }
        }
    }
} 