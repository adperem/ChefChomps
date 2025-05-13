package com.example.chefchomps.ui

import ChefChompsTema
import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Divider
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
import androidx.compose.runtime.LaunchedEffect
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
import com.example.chefchomps.model.Usuario
import com.google.gson.Gson
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking

// Constantes para SharedPreferences
private const val PREFS_NAME = "ChefChompsPrefs"
private const val KEY_DARK_THEME = "dark_theme"

class BuscarPorUsuario : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        // Leer la preferencia del tema oscuro
        val sharedPref = getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        val savedDarkTheme = sharedPref.getBoolean(KEY_DARK_THEME, false)
        
        setContent {
            ChefChompsTema(darkTheme = savedDarkTheme) {
                Surface(modifier = Modifier.fillMaxSize()) {
                    BuscarPorUsuarioScreen(onBack = { finish() })
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class, DelicateCoroutinesApi::class)
@Composable
fun BuscarPorUsuarioScreen(onBack: () -> Unit) {
    val context = LocalContext.current
    val databaseHelper = remember { DatabaseHelper() }
    var searchText by remember { mutableStateOf("") }
    var isSearching by remember { mutableStateOf(false) }
    var userRecipes by remember { mutableStateOf<List<Recipe>>(emptyList()) }
    var usuariosSugeridos by remember { mutableStateOf<List<Usuario>>(emptyList()) }
    var usuarioSeleccionado by remember { mutableStateOf<Usuario?>(null) }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    val focusManager = LocalFocusManager.current
    val textFieldFocusRequester = remember { FocusRequester() }
    
    // Para manejar el debounce (evitar demasiadas búsquedas mientras se escribe)
    var searchJob by remember { mutableStateOf<Job?>(null) }
    
    // Efecto para buscar usuarios mientras se escribe
    LaunchedEffect(searchText) {
        if (searchText.length >= 2) {
            // Cancelar búsqueda anterior si existe
            searchJob?.cancel()
            
            // Iniciar nueva búsqueda con debounce
            searchJob = GlobalScope.launch(Dispatchers.Main) {
                delay(300) // Esperar 300ms para evitar búsquedas excesivas
                isSearching = true
                try {
                    usuariosSugeridos = databaseHelper.buscarUsuariosPorTexto(searchText)
                } catch (e: Exception) {
                    usuariosSugeridos = emptyList()
                } finally {
                    isSearching = false
                }
            }
        } else {
            usuariosSugeridos = emptyList()
        }
    }
    
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
                    usuarioSeleccionado = null
                    userRecipes = emptyList()
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
                            usuarioSeleccionado = null
                            userRecipes = emptyList()
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
                            
                            // Usar el usuario seleccionado si existe, o buscar por texto
                            if (usuarioSeleccionado != null) {
                                buscarRecetasPorUsuario(
                                    databaseHelper, 
                                    usuarioSeleccionado!!.id,
                                    onRecetasFound = { recetas -> 
                                        userRecipes = recetas
                                        isSearching = false
                                    },
                                    onError = { error ->
                                        errorMessage = error
                                        userRecipes = emptyList()
                                        isSearching = false
                                    }
                                )
                            } else {
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
                    }
                )
            )
            
            // Mostrar sugerencias de usuarios si hay texto y no se ha seleccionado un usuario
            if (searchText.isNotEmpty() && usuarioSeleccionado == null && usuariosSugeridos.isNotEmpty()) {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    LazyColumn(
                        modifier = Modifier.heightIn(max = 200.dp)
                    ) {
                        items(usuariosSugeridos) { usuario ->
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable {
                                        usuarioSeleccionado = usuario
                                        searchText = usuario.username
                                        // Buscar recetas del usuario seleccionado
                                        buscarRecetasPorUsuario(
                                            databaseHelper, 
                                            usuario.id,
                                            onRecetasFound = { recetas -> 
                                                userRecipes = recetas
                                            },
                                            onError = { error ->
                                                errorMessage = error
                                                userRecipes = emptyList()
                                            }
                                        )
                                    }
                                    .padding(vertical = 8.dp, horizontal = 16.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    Icons.Default.Person,
                                    contentDescription = "Usuario",
                                    modifier = Modifier.padding(end = 8.dp),
                                    tint = MaterialTheme.colorScheme.primary
                                )
                                
                                Column {
                                    Text(
                                        text = usuario.username,
                                        style = MaterialTheme.typography.bodyLarge
                                    )
                                    Text(
                                        text = "${usuario.nombre} ${usuario.apellidos}",
                                        style = MaterialTheme.typography.bodyMedium,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                            }
                            Divider()
                        }
                    }
                }
            }
            
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
                        "Ingrese nombre de usuario para buscar sus recetas" 
                    else if (usuarioSeleccionado != null && userRecipes.isEmpty())
                        "No se encontraron recetas para el usuario '${usuarioSeleccionado!!.username}'"
                    else if (errorMessage == null && userRecipes.isEmpty())
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

/**
 * Función auxiliar para buscar recetas por usuario
 */
@OptIn(DelicateCoroutinesApi::class)
private fun buscarRecetasPorUsuario(
    databaseHelper: DatabaseHelper,
    userId: String,
    onRecetasFound: (List<Recipe>) -> Unit,
    onError: (String) -> Unit
) {
    GlobalScope.launch(Dispatchers.Main) {
        try {
            val recetas = databaseHelper.buscarRecetasPorIdUsuario(userId) ?: emptyList()
            onRecetasFound(recetas)
        } catch (e: Exception) {
            onError("Error al buscar recetas: ${e.message}")
        }
    }
} 