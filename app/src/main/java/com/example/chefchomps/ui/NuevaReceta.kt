package com.example.chefchomps.ui

import ChefChompsTema
import android.app.Activity.RESULT_OK
import android.content.Context
import android.net.Uri
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.example.chefchomps.R
import com.example.chefchomps.logica.DatabaseHelper
import com.example.chefchomps.model.Ingredient
import com.example.chefchomps.model.Recipe
import com.google.gson.Gson
import kotlinx.coroutines.launch


// Constantes para SharedPreferences
private const val PREFS_NAME = "ChefChompsPrefs"
private const val KEY_DARK_THEME = "dark_theme"
/**
 * Clase que contiene la composable para poder crear nuevas recetas
 */
class NuevaReceta : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        // Leer la preferencia del tema oscuro
        val sharedPref = getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        val savedDarkTheme = sharedPref.getBoolean(KEY_DARK_THEME, false)
        
        // Obtener los datos de la receta si estamos en modo edición
        val isEditMode = intent.getBooleanExtra("is_edit_mode", false)
        val recetaJson = intent.getStringExtra("receta_json")
        val recetaParaEditar = if (isEditMode && recetaJson != null) {
            try {
                Gson().fromJson(recetaJson, Recipe::class.java)
            } catch (e: Exception) {
                Log.e("NuevaReceta", "Error al parsear receta: ${e.message}")
                null
            }
        } else null
        
        setContent {
            ChefChompsTema(darkTheme = savedDarkTheme) {
                Surface(modifier = Modifier.fillMaxSize()) {
                    NuevaRecetaScreen(
                        onBack = { finish() },
                        isEditMode = isEditMode,
                        recetaParaEditar = recetaParaEditar
                    )
                }
            }
        }
    }
}

/**
 * Componible reusable para poder crear nuevas recetas
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NuevaRecetaScreen(
    onBack: () -> Unit,
    isEditMode: Boolean = false,
    recetaParaEditar: Recipe? = null
) {
    // Inicializar valores, usar datos de la receta si estamos en modo edición
    var titulo by remember { mutableStateOf(recetaParaEditar?.title ?: "") }
    var imagenUri by remember { mutableStateOf<Uri?>(null) }
    var imagenUrl by remember { mutableStateOf(recetaParaEditar?.image) }
    var ingredientes by remember { 
        mutableStateOf(
            recetaParaEditar?.extendedIngredients ?: 
            listOf(Ingredient(0, "", "", "", 0.0, "", "", "", null, null, "", null, "", "", null, null, null))
        ) 
    }
    var pasos by remember { 
        mutableStateOf(
            recetaParaEditar?.instructions?.split("\n") ?: 
            listOf("")
        ) 
    }
    var tiempoPreparacion by remember { 
        mutableStateOf(
            recetaParaEditar?.readyInMinutes?.toString() ?: 
            ""
        ) 
    }
    var porciones by remember { 
        mutableStateOf(
            recetaParaEditar?.servings?.toString() ?: 
            ""
        ) 
    }
    var descripcion by remember { 
        mutableStateOf(
            recetaParaEditar?.summary ?: 
            ""
        ) 
    }
    var esVegetariana by remember { 
        mutableStateOf(
            recetaParaEditar?.vegetarian ?: 
            false
        ) 
    }
    var esVegana by remember { 
        mutableStateOf(
            recetaParaEditar?.vegan ?: 
            false
        ) 
    }
    var tipoPlato by remember { 
        mutableStateOf(
            recetaParaEditar?.dishTypes?.firstOrNull() ?: 
            "Principal"
        ) 
    }
    var glutenFree by remember { 
        mutableStateOf(
            recetaParaEditar?.glutenFree ?: 
            false
        ) 
    }
    var isLoading by remember { mutableStateOf(false) }

    val context = LocalContext.current
    val databaseHelper = remember { DatabaseHelper() }
    val coroutineScope = rememberCoroutineScope()
    val snackbarHostState = remember { SnackbarHostState() }

    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        uri?.let { 
            imagenUri = it
            imagenUrl = null  // Si seleccionamos una nueva imagen, limpiamos la URL anterior
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(if (isEditMode) "Editar Receta" else "Nueva Receta") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Volver")
                    }
                }
            )
        },
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            item {
                OutlinedTextField(
                    value = titulo,
                    onValueChange = { titulo = it },
                    label = { Text("Título*") },
                    modifier = Modifier.fillMaxWidth()
                )
            }

            item {
                OutlinedTextField(
                    value = descripcion,
                    onValueChange = { descripcion = it },
                    label = { Text("Descripción") },
                    modifier = Modifier.fillMaxWidth(),
                    maxLines = 3
                )
            }

            item {
                Button(
                    onClick = { launcher.launch("image/*") },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Seleccionar imagen")
                }

                if (imagenUri != null) {
                    AsyncImage(
                        model = imagenUri,
                        contentDescription = "Imagen de la receta",
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(200.dp)
                    )
                } else if (imagenUrl != null && imagenUrl!!.isNotEmpty()) {
                    AsyncImage(
                        model = imagenUrl,
                        contentDescription = "Imagen de la receta",
                        placeholder = painterResource(id = R.drawable.ic_launcher_foreground),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(200.dp)
                    )
                }
            }

            item {
                Text("Ingredientes*", style = MaterialTheme.typography.titleMedium)
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    ingredientes.forEachIndexed { index, ingrediente ->
                        Column {
                            OutlinedTextField(
                                value = ingrediente.name,
                                onValueChange = { newValue ->
                                    ingredientes = ingredientes.toMutableList().apply {
                                        this[index] = ingrediente.copy(name = newValue)
                                    }
                                },
                                label = { Text("Nombre ingrediente ${index + 1}") },
                                modifier = Modifier.fillMaxWidth()
                            )
                            OutlinedTextField(
                                value = ingrediente.amount.toString(),
                                onValueChange = { newValue ->
                                    ingredientes = ingredientes.toMutableList().apply {
                                        this[index] = ingrediente.copy(amount = newValue.toDoubleOrNull() ?: 0.0)
                                    }
                                },
                                label = { Text("Cantidad") },
                                modifier = Modifier.fillMaxWidth(),
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                            )
                            ingrediente.unit?.let {
                                OutlinedTextField(
                                    value = it,
                                    onValueChange = { newValue ->
                                        ingredientes = ingredientes.toMutableList().apply {
                                            this[index] = ingrediente.copy(unit = newValue)
                                        }
                                    },
                                    label = { Text("Unidad (ej. taza, cucharada)") },
                                    modifier = Modifier.fillMaxWidth()
                                )
                            }

                            if (index > 0) {
                                IconButton(
                                    onClick = {
                                        ingredientes = ingredientes.toMutableList().apply {
                                            removeAt(index)
                                        }
                                    },
                                    modifier = Modifier.align(Alignment.End)
                                ) {
                                    Icon(
                                        painter = painterResource(R.drawable.ic_delete),
                                        contentDescription = "Eliminar"
                                    )
                                }
                            }
                        }
                    }

                    Button(
                        onClick = {
                            ingredientes = ingredientes + Ingredient(0, "", "", "", 0.0, "", "", "", null, null, "", null, "", "", null, null, null)
                        },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Icon(Icons.Default.Add, contentDescription = "Añadir")
                        Text("Añadir ingrediente")
                    }
                }
            }

            item {
                Text("Pasos de preparación*", style = MaterialTheme.typography.titleMedium)
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    pasos.forEachIndexed { index, paso ->
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text("${index + 1}.", modifier = Modifier.padding(end = 8.dp))
                            OutlinedTextField(
                                value = paso,
                                onValueChange = { newValue ->
                                    pasos = pasos.toMutableList().apply {
                                        this[index] = newValue
                                    }
                                },
                                modifier = Modifier.weight(1f),
                                label = { Text("Paso ${index + 1}") }
                            )
                            if (index > 0) {
                                IconButton(
                                    onClick = {
                                        pasos = pasos.toMutableList().apply {
                                            removeAt(index)
                                        }
                                    }
                                ) {
                                    Icon(
                                        painter = painterResource(R.drawable.ic_delete),
                                        contentDescription = "Eliminar"
                                    )
                                }
                            }
                        }
                    }

                    Button(
                        onClick = { pasos = pasos + "" },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Icon(Icons.Default.Add, contentDescription = "Añadir")
                        Text("Añadir paso")
                    }
                }
            }

            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    OutlinedTextField(
                        value = tiempoPreparacion,
                        onValueChange = { tiempoPreparacion = it.filter { c -> c.isDigit() } },
                        label = { Text("Tiempo (min)*") },
                        modifier = Modifier.weight(1f),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                    )

                    OutlinedTextField(
                        value = porciones,
                        onValueChange = { porciones = it.filter { c -> c.isDigit() } },
                        label = { Text("Porciones*") },
                        modifier = Modifier.weight(1f),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                    )
                }
            }

            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    FilterChip(
                        selected = esVegetariana,
                        onClick = { esVegetariana = !esVegetariana },
                        label = { Text("Vegetariana") },
                        modifier = Modifier.weight(1f)
                    )

                    FilterChip(
                        selected = esVegana,
                        onClick = { esVegana = !esVegana },
                        label = { Text("Vegana") },
                        modifier = Modifier.weight(1f)
                    )
                }
            }

            item {
                var expanded by remember { mutableStateOf(false) }
                val tiposPlato = listOf("Principal", "Entrante", "Postre", "Acompañamiento", "Desayuno")

                Box(modifier = Modifier.fillMaxWidth()) {
                    OutlinedTextField(
                        value = tipoPlato,
                        onValueChange = {},
                        label = { Text("Tipo de plato") },
                        modifier = Modifier.fillMaxWidth(),
                        readOnly = true,
                        trailingIcon = {
                            IconButton(onClick = { expanded = true }) {
                                Icon(
                                    painter = painterResource(R.drawable.ic_arrow_drop_down),
                                    contentDescription = "Seleccionar"
                                )
                            }
                        }
                    )
                    DropdownMenu(
                        expanded = expanded,
                        onDismissRequest = { expanded = false }
                    ) {
                        tiposPlato.forEach { item ->
                            DropdownMenuItem(
                                text = { Text(item) },
                                onClick = {
                                    tipoPlato = item
                                    expanded = false
                                }
                            )
                        }
                    }
                }
            }

            item {
                Button(
                    onClick = {
                        if (titulo.isBlank()) {
                            coroutineScope.launch {
                                snackbarHostState.showSnackbar("El título es obligatorio")
                            }
                            return@Button
                        }

                        if (ingredientes.isEmpty() || ingredientes.all { it.name.isBlank() }) {
                            coroutineScope.launch {
                                snackbarHostState.showSnackbar("Debe agregar al menos un ingrediente")
                            }
                            return@Button
                        }

                        if (pasos.isEmpty() || pasos.all { it.isBlank() }) {
                            coroutineScope.launch {
                                snackbarHostState.showSnackbar("Debe agregar al menos un paso")
                            }
                            return@Button
                        }

                        isLoading = true
                        coroutineScope.launch {
                            try {
                                val tiempo = tiempoPreparacion.toIntOrNull() ?: 0
                                val porcionesInt = porciones.toIntOrNull() ?: 1

                                if (isEditMode && recetaParaEditar != null) {
                                    // Actualizar receta existente
                                    val recetaActualizada = recetaParaEditar.copy(
                                        title = titulo,
                                        summary = descripcion,
                                        readyInMinutes = tiempo,
                                        servings = porcionesInt,
                                        instructions = pasos.joinToString("\n"),
                                        extendedIngredients = ingredientes,
                                        vegetarian = esVegetariana,
                                        vegan = esVegana,
                                        dishTypes = listOf(tipoPlato),
                                        glutenFree = glutenFree,
                                        image = imagenUrl
                                    )

                                    val actualizado = databaseHelper.actualizarReceta(recetaActualizada, imagenUri)
                                    
                                    if (actualizado) {
                                        snackbarHostState.showSnackbar("Receta actualizada correctamente")
                                        // Establecer resultado OK para que la actividad anterior sepa que la edición fue exitosa
                                        (context as? ComponentActivity)?.setResult(RESULT_OK)
                                        onBack()
                                    } else {
                                        snackbarHostState.showSnackbar("No se pudo actualizar la receta")
                                    }
                                } else {
                                    // Crear nueva receta
                                    val receta = databaseHelper.subirReceta(
                                        titulo = titulo,
                                        imagenUri = imagenUri,
                                        ingredientes = ingredientes,
                                        pasos = pasos,
                                        tiempoPreparacion = tiempo,
                                        descripcion = descripcion,
                                        porciones = porcionesInt,
                                        esVegetariana = esVegetariana,
                                        esVegana = esVegana,
                                        tipoPlato = tipoPlato,
                                        glutenFree = glutenFree
                                    )

                                    if (receta != null) {
                                        snackbarHostState.showSnackbar("Receta guardada correctamente")
                                        // Establecer resultado OK para que la actividad anterior sepa que la creación fue exitosa
                                        (context as? ComponentActivity)?.setResult(RESULT_OK)
                                        onBack()
                                    } else {
                                        snackbarHostState.showSnackbar("Error al guardar la receta")
                                    }
                                }
                            } catch (e: Exception) {
                                snackbarHostState.showSnackbar("Error: ${e.message}")
                            } finally {
                                isLoading = false
                            }
                        }
                    },
                    modifier = Modifier.fillMaxWidth(),
                    enabled = !isLoading
                ) {
                    if (isLoading) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(24.dp),
                            color = MaterialTheme.colorScheme.onPrimary
                        )
                    } else {
                        Text(if (isEditMode) "Actualizar receta" else "Guardar receta")
                    }
                }
            }
        }
    }
}