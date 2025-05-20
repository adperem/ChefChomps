package com.example.chefchomps.ui

import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.example.chefchomps.R
import com.example.chefchomps.logica.ApiCLient
import com.example.chefchomps.model.Recipe
import com.example.chefchomps.ui.components.DetalleRecetaContent
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

/**
 * Actividad para mostrar el detalle completo de una receta
 */
class PaginaDetalle : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        val recetaId = intent.getIntExtra("receta_id", -1)
        val recetaTitle = intent.getStringExtra("receta_title") ?: ""
        val recetaImage = intent.getStringExtra("receta_image") ?: ""
        val recetaJson = intent.getStringExtra("receta_json")
        val fromFirebase = intent.getBooleanExtra("from_firebase", false)
        
        // Si tenemos JSON de Firebase, lo usamos directamente
        val recetaFromJson = if (recetaJson != null) {
            try {
                val gson = com.google.gson.Gson()
                gson.fromJson(recetaJson, Recipe::class.java)
            } catch (e: Exception) {
                Toast.makeText(this, "Error al procesar los datos de la receta", Toast.LENGTH_LONG).show()
                null
            }
        } else null
        
        // Si el ID es inválido y no tenemos datos de Firebase, mostrar un mensaje y terminar
        if (recetaId <= 0 && recetaFromJson == null) {
            Toast.makeText(this, "No se pudo cargar la receta: datos insuficientes", Toast.LENGTH_LONG).show()
            if (recetaTitle.isEmpty()) {
                finish()
                return
            }
        }
        
        setContent {
            ChefChompsAppTheme {
                Surface(modifier = Modifier.fillMaxSize()) {
                    if (recetaFromJson != null) {
                        // Si la receta viene de Firebase como JSON, la mostramos directamente
                        PaginaDetalleFirebase(receta = recetaFromJson)
                    } else if (recetaId > 0 || recetaTitle.isNotEmpty()) {
                        // Si no, seguimos el flujo original para buscar en la API
                        PaginaDetalle(recetaId = recetaId, titulo = recetaTitle, imagen = recetaImage)
                    } else {
                        Text(
                            text = "No se pudo cargar la receta",
                            style = MaterialTheme.typography.bodyLarge,
                            textAlign = TextAlign.Center,
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(16.dp)
                        )
                    }
                }
            }
        }
    }
}

/**
 * Muestra el detalle de una receta obtenida de Firebase
 *
 * @param receta Objeto Recipe completo desde Firebase
 * @param modifier Modificador Compose para personalizar el layout
 */
@Composable
fun PaginaDetalleFirebase(
    receta: Recipe,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp),
        contentAlignment = Alignment.Center
    ) {
        // Mostrar directamente los detalles de la receta de Firebase
        DetalleRecetaContent(recipe = receta)
    }
}

/**
 * Muestra el detalle de una receta obtenida de la API
 *
 * @param recetaId ID de la receta para buscar en la API
 * @param titulo Título de la receta (opcional, para mostrar datos básicos si falla la API)
 * @param imagen URL de la imagen (opcional)
 * @param modifier Modificador Compose para personalizar el layout
 */
@Composable
fun PaginaDetalle(
    recetaId: Int,
    titulo: String = "",
    imagen: String = "",
    modifier: Modifier = Modifier
) {
    var recipe by remember { mutableStateOf<Recipe?>(null) }
    var isLoading by remember { mutableStateOf(recetaId > 0)}
    var error by remember { mutableStateOf<String?>(null) }
    
    // Si tenemos un ID válido, intentamos cargar la información completa
    LaunchedEffect(recetaId) {
        if (recetaId > 0) {
            isLoading = true
            try {
                val result = withContext(Dispatchers.IO) {
                    ApiCLient.getRecipeInformation(recetaId)
                }
                recipe = result
                isLoading = false
            } catch (e: Exception) {
                error = e.message
                isLoading = false
            }
        } else if (titulo.isNotEmpty()) {
            // Si no tenemos ID pero tenemos título, creamos una receta parcial
            recipe = Recipe(
                id = recetaId,
                title = titulo,
                image = imagen,
                readyInMinutes = null,
                servings = null,
                summary = "No hay información detallada disponible para esta receta.",
                instructions = null
            )
            isLoading = false
        }
    }
    
    Box(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp),
        contentAlignment = Alignment.Center
    ) {
        when {
            isLoading -> {
                CircularProgressIndicator()
            }
            error != null -> {
                Text(
                    text = "Error: $error",
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.error,
                    textAlign = TextAlign.Center
                )
            }
            recipe != null -> {
                DetalleRecetaContent(recipe = recipe!!)
            }
            else -> {
                // Mostrar información parcial cuando sólo tenemos título e imagen
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .verticalScroll(rememberScrollState())
                ) {
                    Card(
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        if (imagen.isNotEmpty()) {
                            AsyncImage(
                                model = imagen,
                                contentDescription = titulo,
                                contentScale = ContentScale.Crop,
                                placeholder = painterResource(id = R.drawable.ic_launcher_foreground),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(200.dp)
                            )
                        }
                        
                        Text(
                            text = titulo,
                            style = MaterialTheme.typography.headlineMedium,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(16.dp)
                        )
                        
                        Text(
                            text = "Información no disponible para esta receta.",
                            style = MaterialTheme.typography.bodyLarge,
                            modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
                        )
                        
                        Spacer(modifier = Modifier.height(16.dp))
                    }
                }
            }
        }
    }
}