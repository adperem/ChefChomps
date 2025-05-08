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
    Text(
        text = formatHtmlTags(html),
        style = style,
        modifier = modifier
    )
}

/**
 * Función para formatear etiquetas HTML específicas
 */
private fun formatHtmlTags(html: String): String {
    var result = html
    
    // Procesar listas ordenadas y desordenadas
    result = processLists(result)
    
    // Procesar párrafos
    result = result.replace(Regex("<p>(.*?)</p>", RegexOption.DOT_MATCHES_ALL), "$1\n\n")
    
    // Procesar saltos de línea
    result = result.replace("<br>", "\n").replace("<br/>", "\n").replace("<br />", "\n")
    
    // Procesar encabezados - sin caracteres de formato
    result = result.replace(Regex("<h1>(.*?)</h1>"), "$1\n")
        .replace(Regex("<h2>(.*?)</h2>"), "$1\n")
        .replace(Regex("<h3>(.*?)</h3>"), "$1\n")
        .replace(Regex("<h4>(.*?)</h4>"), "$1\n")
    
    // Procesar negritas e itálicas - sin caracteres de formato
    result = result.replace(Regex("<b>(.*?)</b>"), "$1")
        .replace(Regex("<strong>(.*?)</strong>"), "$1")
        .replace(Regex("<i>(.*?)</i>"), "$1")
        .replace(Regex("<em>(.*?)</em>"), "$1")
    
    // Eliminar etiquetas HTML restantes
    result = result.replace(Regex("<[^>]*>"), "")
    
    // Reemplazar entidades HTML comunes
    result = result.replace("&nbsp;", " ")
        .replace("&amp;", "&")
        .replace("&lt;", "<")
        .replace("&gt;", ">")
        .replace("&quot;", "\"")
        .replace("&apos;", "'")
        .replace("&#39;", "'")
        .replace("&ndash;", "-")
        .replace("&mdash;", "—")
        .replace("&lsquo;", "'")
        .replace("&rsquo;", "'")
        .replace("&ldquo;", """)
        .replace("&rdquo;", """)
        .replace("&bull;", "•")
        .replace("&hellip;", "...")
        .replace(Regex("&#\\d+;")) { matchResult ->
            try {
                val number = matchResult.value.substring(2, matchResult.value.length - 1).toInt()
                number.toChar().toString()
            } catch (e: Exception) {
                matchResult.value
            }
        }
    
    return result.trim()
}

/**
 * Procesa listas HTML (ordenadas y desordenadas)
 */
private fun processLists(html: String): String {
    var result = html
    
    // Procesar listas desordenadas
    val ulPattern = Regex("<ul>(.*?)</ul>", RegexOption.DOT_MATCHES_ALL)
    result = result.replace(ulPattern) { matchResult ->
        val listContent = matchResult.groupValues[1]
        processListItems(listContent, false)
    }
    
    // Procesar listas ordenadas
    val olPattern = Regex("<ol>(.*?)</ol>", RegexOption.DOT_MATCHES_ALL)
    result = result.replace(olPattern) { matchResult ->
        val listContent = matchResult.groupValues[1]
        processListItems(listContent, true)
    }
    
    return result
}

/**
 * Procesa los elementos de una lista
 */
private fun processListItems(listContent: String, isOrdered: Boolean): String {
    val liPattern = Regex("<li>(.*?)</li>", RegexOption.DOT_MATCHES_ALL)
    val items = mutableListOf<String>()
    var index = 1
    
    val matches = liPattern.findAll(listContent)
    matches.forEach { match ->
        val itemContent = match.groupValues[1]
        val prefix = if (isOrdered) "${index++}. " else "• "
        items.add("$prefix$itemContent")
    }
    
    return items.joinToString("\n") + "\n\n"
} 