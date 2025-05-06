package com.example.chefchomps.ui

import ChefChompsTema
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

class PaginaDetalle : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        val recetaId = intent.getIntExtra("receta_id", -1)
        val recetaTitle = intent.getStringExtra("receta_title") ?: ""
        val recetaImage = intent.getStringExtra("receta_image") ?: ""
        
        // Si el ID es inválido, mostrar un mensaje y terminar
        if (recetaId <= 0) {
            Toast.makeText(this, "No se pudo cargar la receta: ID inválido", Toast.LENGTH_LONG).show()
            if (recetaTitle.isEmpty()) {
                finish()
                return
            }
        }
        
        setContent {
            ChefChompsTema(darkTheme = false) {
                Surface(modifier = Modifier.fillMaxSize()) {
                    if (recetaId > 0 || recetaTitle.isNotEmpty()) {
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