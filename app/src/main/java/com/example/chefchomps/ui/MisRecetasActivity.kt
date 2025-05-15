package com.example.chefchomps.ui

import ChefChompsTema
import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.example.chefchomps.R
import com.example.chefchomps.logica.DatabaseHelper
import com.example.chefchomps.model.Recipe
import com.google.gson.Gson
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

// Constantes para SharedPreferences
private const val PREFS_NAME = "ChefChompsPrefs"
private const val KEY_DARK_THEME = "dark_theme"
private const val EDIT_RECIPE_REQUEST_CODE = 1001
/**
 * Clase que contiene todas las componible relacionadas con recetas creadas por el usuario
 */
class MisRecetasActivity : ComponentActivity() {
    // Variable para mantener la lista de recetas
    private val misRecetasState = mutableStateOf<List<Recipe>>(emptyList())
    private val databaseHelper = DatabaseHelper()
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        // Leer la preferencia del tema oscuro
        val sharedPref = getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        val savedDarkTheme = sharedPref.getBoolean(KEY_DARK_THEME, false)
        
        // Cargar recetas
        cargarRecetas()
        
        setContent {
            ChefChompsTema(darkTheme = savedDarkTheme) {
                Surface(modifier = Modifier.fillMaxSize()) {
                    MisRecetasScreen(
                        recetas = misRecetasState.value, 
                        onBack = { finish() },
                        onEdit = { receta ->
                            // Convertir la receta a JSON para enviarla a la actividad de edición
                            val gson = Gson()
                            val recetaJson = gson.toJson(receta)
                            val intent = Intent(this, NuevaReceta::class.java)
                            intent.putExtra("receta_json", recetaJson)
                            intent.putExtra("is_edit_mode", true)
                            startActivityForResult(intent, EDIT_RECIPE_REQUEST_CODE)
                        },
                        onDelete = { receta -> eliminarReceta(receta) }
                    )
                }
            }
        }
    }
    /**
     * Carga todas las recetas del usrio actual
     */
    @OptIn(DelicateCoroutinesApi::class)
    private fun cargarRecetas() {
        GlobalScope.launch(Dispatchers.Main) {
            try {
                val recetas = databaseHelper.obtenerRecetasUsuarioActual()
                misRecetasState.value = recetas
            } catch (e: Exception) {
                // Manejar error
            }
        }
    }
    
    @OptIn(DelicateCoroutinesApi::class)
    private fun eliminarReceta(receta: Recipe) {
        if (receta.id == null) return
        
        GlobalScope.launch(Dispatchers.Main) {
            try {
                val eliminado = databaseHelper.eliminarReceta(receta.id)
                if (eliminado) {
                    // Actualizar la lista eliminando la receta
                    misRecetasState.value = misRecetasState.value.filter { it.id != receta.id }
                }
            } catch (e: Exception) {
                // Manejar error
            }
        }
    }
    
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == EDIT_RECIPE_REQUEST_CODE && resultCode == RESULT_OK) {
            // La receta fue editada correctamente, recargar la lista de recetas
            cargarRecetas()
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class, DelicateCoroutinesApi::class)
@Composable
fun MisRecetasScreen(
    recetas: List<Recipe>,
    onBack: () -> Unit,
    onEdit: (Recipe) -> Unit,
    onDelete: (Recipe) -> Unit
) {
    val context = LocalContext.current
    val snackbarHostState = remember { SnackbarHostState() }
    val coroutineScope = rememberCoroutineScope()
    
    var isLoading by remember { mutableStateOf(true) }
    var recetaAEliminar by remember { mutableStateOf<Recipe?>(null) }
    var mostrarDialogoConfirmacion by remember { mutableStateOf(false) }

    // Cargar recetas cuando se inicia la actividad
    LaunchedEffect(Unit) {
        isLoading = true
        try {
            // No se necesita cargar recetas nuevamente, ya que se maneja en onCreate
        } catch (e: Exception) {
            coroutineScope.launch {
                snackbarHostState.showSnackbar("Error al cargar recetas: ${e.message}")
            }
        } finally {
            isLoading = false
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Mis Recetas") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Volver")
                    }
                }
            )
        },
        snackbarHost = { SnackbarHost(snackbarHostState) },
        floatingActionButton = {
            ExtendedFloatingActionButton(
                text = { Text("Nueva Receta") },
                icon = { Icon(Icons.Default.Add, contentDescription = null) },
                onClick = {
                    val intent = Intent(context, NuevaReceta::class.java)
                    (context as? ComponentActivity)?.startActivityForResult(intent, EDIT_RECIPE_REQUEST_CODE)
                }
            )
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp)
        ) {
            if (isLoading) {
                CircularProgressIndicator(
                    modifier = Modifier.align(Alignment.Center)
                )
            } else if (recetas.isEmpty()) {
                Column(
                    modifier = Modifier.align(Alignment.Center),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "No tienes recetas",
                        style = MaterialTheme.typography.headlineSmall
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Button(
                        onClick = {
                            val intent = Intent(context, NuevaReceta::class.java)
                            (context as? ComponentActivity)?.startActivityForResult(intent, EDIT_RECIPE_REQUEST_CODE)
                        }
                    ) {
                        Text("Crear nueva receta")
                    }
                }
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(recetas) { receta ->
                        RecetaCard(
                            receta = receta,
                            onEdit = { onEdit(receta) },
                            onDelete = {
                                recetaAEliminar = receta
                                mostrarDialogoConfirmacion = true
                            },
                            onClick = {
                                // Ir a ver el detalle de la receta
                                val gson = Gson()
                                val recetaJson = gson.toJson(receta)
                                val intent = Intent(context, PaginaDetalle::class.java)
                                intent.putExtra("receta_json", recetaJson)
                                intent.putExtra("from_firebase", true)
                                context.startActivity(intent)
                            }
                        )
                    }
                }
            }
            
            // Diálogo de confirmación para eliminar
            if (mostrarDialogoConfirmacion && recetaAEliminar != null) {
                AlertDialog(
                    onDismissRequest = { mostrarDialogoConfirmacion = false },
                    title = { Text("Eliminar receta") },
                    text = { Text("¿Estás seguro que deseas eliminar la receta '${recetaAEliminar!!.title}'? Esta acción no se puede deshacer.") },
                    confirmButton = {
                        TextButton(
                            onClick = {
                                mostrarDialogoConfirmacion = false
                                val recetaId = recetaAEliminar!!.id
                                if (recetaId != null) {
                                    onDelete(recetas.first { it.id == recetaId })
                                }
                            }
                        ) {
                            Text("Eliminar")
                        }
                    },
                    dismissButton = {
                        TextButton(
                            onClick = {
                                mostrarDialogoConfirmacion = false
                                recetaAEliminar = null
                            }
                        ) {
                            Text("Cancelar")
                        }
                    }
                )
            }
        }
    }
}

@Composable
fun RecetaCard(
    receta: Recipe,
    onClick: () -> Unit,
    onEdit: () -> Unit,
    onDelete: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column {
            if (receta.image != null && receta.image.isNotEmpty()) {
                AsyncImage(
                    model = receta.image,
                    contentDescription = receta.title,
                    contentScale = ContentScale.Crop,
                    placeholder = painterResource(id = R.drawable.ic_launcher_foreground),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(150.dp)
                )
            }
            
            Column(
                modifier = Modifier.padding(16.dp)
            ) {
                Text(
                    text = receta.title,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                
                Spacer(modifier = Modifier.height(8.dp))
                
                Text(
                    text = receta.summary ?: "Sin descripción",
                    style = MaterialTheme.typography.bodyMedium,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )
                
                Spacer(modifier = Modifier.height(8.dp))
                
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End
                ) {
                    IconButton(onClick = onEdit) {
                        Icon(
                            Icons.Default.Edit,
                            contentDescription = "Editar",
                            tint = MaterialTheme.colorScheme.primary
                        )
                    }
                    
                    Spacer(modifier = Modifier.width(8.dp))
                    
                    IconButton(onClick = onDelete) {
                        Icon(
                            Icons.Default.Delete,
                            contentDescription = "Eliminar",
                            tint = MaterialTheme.colorScheme.error
                        )
                    }
                }
            }
        }
    }
} 