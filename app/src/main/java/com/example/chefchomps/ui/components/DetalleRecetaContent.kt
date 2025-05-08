package com.example.chefchomps.ui.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.example.chefchomps.R
import com.example.chefchomps.model.Recipe
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.text.HtmlCompat
import android.widget.TextView
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.graphics.toArgb

/**
 * Componente reutilizable para mostrar los detalles completos de una receta.
 *
 * @param recipe La receta a mostrar
 * @param modifier Modificador para personalizar el diseño
 */
@Composable
fun DetalleRecetaContent(recipe: Recipe, modifier: Modifier = Modifier) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .verticalScroll(rememberScrollState())
    ) {
        if (recipe.image != null && recipe.image.isNotEmpty()) {
            AsyncImage(
                model = recipe.image,
                contentDescription = recipe.title,
                contentScale = ContentScale.Crop,
                placeholder = painterResource(id = R.drawable.ic_launcher_foreground),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp)
            )
        }
        
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = recipe.title,
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold
            )
            
            Spacer(modifier = Modifier.height(8.dp))
            
            Text(
                text = "Tiempo de preparación: ${recipe.readyInMinutes ?: 0} minutos",
                style = MaterialTheme.typography.bodyLarge
            )
            
            if (recipe.servings != null && recipe.servings > 0) {
                Text(
                    text = "Porciones: ${recipe.servings}",
                    style = MaterialTheme.typography.bodyLarge
                )
            }
            
            Spacer(modifier = Modifier.height(16.dp))
            
            Text(
                text = "Descripción",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold
            )
            
            Spacer(modifier = Modifier.height(8.dp))
            
            // Mostrar HTML para la descripción
            HtmlText(
                html = recipe.summary ?: "No hay descripción disponible",
                style = MaterialTheme.typography.bodyMedium
            )
            
            Spacer(modifier = Modifier.height(16.dp))
            
            Text(
                text = "Instrucciones",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold
            )
            
            Spacer(modifier = Modifier.height(8.dp))
            
            // Mostrar HTML para las instrucciones
            HtmlText(
                html = recipe.instructions ?: "No hay instrucciones disponibles",
                style = MaterialTheme.typography.bodyMedium
            )
        }
    }
}

/**
 * Componente para mostrar texto HTML en Compose
 */
@Composable
fun HtmlText(
    html: String,
    style: androidx.compose.ui.text.TextStyle = MaterialTheme.typography.bodyMedium,
    modifier: Modifier = Modifier
) {
    // Simplemente mostrar el texto sin procesar el HTML
    Text(
        text = html,
        style = style,
        modifier = modifier
    )
} 