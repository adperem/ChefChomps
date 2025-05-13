package com.example.chefchomps.ui.components

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.outlined.Star
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Outline
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.example.chefchomps.R
import com.example.chefchomps.logica.DatabaseHelper
import com.example.chefchomps.model.Comentario
import com.example.chefchomps.model.Recipe
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Locale

/**
 * Componente reutilizable para mostrar los detalles completos de una receta.
 *
 * @param recipe La receta a mostrar
 * @param modifier Modificador para personalizar el diseño
 */
@Composable
fun DetalleRecetaContent(recipe: Recipe, modifier: Modifier = Modifier) {
    // Estado para gestionar comentarios
    var comentarios by remember { mutableStateOf<List<Comentario>>(emptyList()) }
    var isLoading by remember { mutableStateOf(false) }
    var comentarioUsuario by remember { mutableStateOf<Comentario?>(null) }
    val databaseHelper = remember { DatabaseHelper() }
    val coroutineScope = rememberCoroutineScope()
    val databasegelper =DatabaseHelper()
    // Cargar comentarios
    LaunchedEffect(recipe.id) {
        if (recipe.id != null) {
            isLoading = true
            try {
                comentarios = databaseHelper.obtenerComentarios(recipe.id)
                comentarioUsuario = databaseHelper.obtenerComentarioUsuario(recipe.id)
            } catch (e: Exception) {
                // Si hay algún error al cargar los comentarios, mantenemos la lista vacía
                comentarios = emptyList()
                comentarioUsuario = null
            } finally {
                isLoading = false
            }
        }
    }
    
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
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = recipe.title,
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.weight(1f)
                )
                
                // Mostrar valoración promedio con estrellas solo si hay valoración
                if (recipe.valoracionPromedio != null && recipe.valoracionPromedio > 0 && (recipe.cantidadValoraciones ?: 0) > 0) {
                    Text(
                        text = String.format("%.1f", recipe.valoracionPromedio),
                        style = MaterialTheme.typography.bodyLarge,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    RatingBar(rating = recipe.valoracionPromedio.toFloat(), modifier = Modifier.height(24.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "(${recipe.cantidadValoraciones})",
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
            }
            
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
            
            Spacer(modifier = Modifier.height(24.dp))
            
            // Sección de comentarios y valoraciones - solo se muestra si hay comentarios o si el usuario puede comentar
            Text(
                text = "Comentarios y valoraciones",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold
            )
            
            Spacer(modifier = Modifier.height(8.dp))
            
            // Formulario para añadir comentario si no hay uno del usuario actual
            if (databasegelper.getCurrentUser()!=null) {
                if (comentarioUsuario == null) {
                    ComentarioForm(
                        onSubmit = { texto, valoracion ->
                            recipe.id?.let { recetaId ->
                                coroutineScope.launch {
                                    isLoading = true
                                    try {
                                        val result = databaseHelper.agregarComentario(
                                            recetaId = recetaId,
                                            texto = texto,
                                            valoracion = valoracion
                                        )
                                        if (result) {
                                            // Recargar comentarios
                                            comentarios =
                                                databaseHelper.obtenerComentarios(recetaId)
                                            comentarioUsuario =
                                                databaseHelper.obtenerComentarioUsuario(recetaId)
                                        }
                                    } catch (e: Exception) {
                                        // Manejar el error silenciosamente
                                    } finally {
                                        isLoading = false
                                    }
                                }
                            }
                        }
                    )
                } else {
                    // Mostrar el comentario del usuario actual
                    Text(
                        text = "Tu valoración:",
                        style = MaterialTheme.typography.bodyLarge,
                        fontWeight = FontWeight.Bold
                    )

                    // Usar comentarioUsuario de forma segura
                    comentarioUsuario?.let { comentario ->
                        ComentarioItem(
                            comentario = comentario,
                            esPropio = true,
                            onDelete = {
                                coroutineScope.launch {
                                    isLoading = true
                                    try {
                                        if (databaseHelper.eliminarComentario(comentario.id)) {
                                            comentarioUsuario = null
                                            // Recargar comentarios
                                            recipe.id?.let { recetaId ->
                                                comentarios =
                                                    databaseHelper.obtenerComentarios(recetaId)
                                            }
                                        }
                                    } catch (e: Exception) {
                                        // Manejar el error silenciosamente
                                    } finally {
                                        isLoading = false
                                    }
                                }
                            }
                        )
                    }

                    Spacer(modifier = Modifier.height(16.dp))
                }
            }else{
                Text(
                    text = "Necesitas una cuenta antes de poder comentar",
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.Bold
                )
            }
            // Indicador de carga
            if (isLoading) {
                CircularProgressIndicator(
                    modifier = Modifier
                        .align(Alignment.CenterHorizontally)
                        .padding(16.dp)
                )
            }
            
            // Lista de comentarios
            if (comentarios.isNotEmpty()) {
                // Filtramos con seguridad para evitar errores de referencias nulas
                val comentariosDeOtros = comentarios.filter { it.id != comentarioUsuario?.id }
                
                if (comentariosDeOtros.isNotEmpty()) {
                    Text(
                        text = "Opiniones de otros usuarios:",
                        style = MaterialTheme.typography.bodyLarge,
                        fontWeight = FontWeight.Bold
                    )
                    
                    Spacer(modifier = Modifier.height(8.dp))
                    
                    comentariosDeOtros.forEach { comentario ->
                        ComentarioItem(comentario = comentario, esPropio = false)
                        Spacer(modifier = Modifier.height(8.dp))
                    }
                }
            } else if (!isLoading) {
                Text(
                    text = "No hay comentarios aún. ¡Sé el primero en comentar!",
                    style = MaterialTheme.typography.bodyMedium,
                    modifier = Modifier.padding(vertical = 16.dp)
                )
            }
        }
    }
}

@Composable
fun ComentarioForm(onSubmit: (String, Int) -> Unit) {
    var texto by remember { mutableStateOf("") }
    var valoracion by remember { mutableIntStateOf(5) }
    
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(
                text = "Añadir valoración",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
            
            Spacer(modifier = Modifier.height(8.dp))
            
            // Selección de valoración
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Tu valoración:",
                    modifier = Modifier.padding(end = 8.dp)
                )
                
                (1..5).forEach { index ->
                    IconButton(
                        onClick = { valoracion = index },
                        modifier = Modifier.size(32.dp)
                    ) {
                        Icon(
                            imageVector = if (index <= valoracion) Icons.Default.Star else Icons.Outlined.Star,
                            contentDescription = "Valoración $index",
                            tint = if (index <= valoracion) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)
                        )
                    }
                }
            }
            
            Spacer(modifier = Modifier.height(8.dp))
            
            // Campo de texto para el comentario
            OutlinedTextField(
                value = texto,
                onValueChange = { texto = it },
                label = { Text("Tu comentario (opcional)") },
                modifier = Modifier.fillMaxWidth(),
                minLines = 3
            )
            
            Spacer(modifier = Modifier.height(16.dp))
            
            Button(
                onClick = {
                    onSubmit(texto, valoracion)
                },
                modifier = Modifier.align(Alignment.End),
                enabled = valoracion > 0
            ) {
                Text("Enviar")
            }
        }
    }
}

@Composable
fun ComentarioItem(
    comentario: Comentario,
    esPropio: Boolean,
    onDelete: () -> Unit = {}
) {
    var mostrarDialogoConfirmacion by remember { mutableStateOf(false) }
    
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (esPropio) MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.2f) else MaterialTheme.colorScheme.surface
        )
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                // Nombre de usuario y fecha
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = comentario.nombreUsuario,
                        style = MaterialTheme.typography.bodyLarge,
                        fontWeight = FontWeight.Bold
                    )
                    
                    Text(
                        text = SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault())
                            .format(comentario.fecha.toDate()),
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                    )
                }
                
                // Valoración
                Row {
                    RatingBar(rating = comentario.valoracion.toFloat())
                }
                
                // Botón de eliminar si es propio
                if (esPropio) {
                    IconButton(
                        onClick = { mostrarDialogoConfirmacion = true }
                    ) {
                        Icon(
                            imageVector = Icons.Default.Delete,
                            contentDescription = "Eliminar comentario",
                            tint = MaterialTheme.colorScheme.error
                        )
                    }
                }
            }
            
            // Comentario
            if (comentario.texto.isNotEmpty()) {
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = comentario.texto,
                    style = MaterialTheme.typography.bodyMedium
                )
            }
        }
    }
    
    // Diálogo de confirmación para eliminar
    if (mostrarDialogoConfirmacion) {
        AlertDialog(
            onDismissRequest = { mostrarDialogoConfirmacion = false },
            title = { Text("Eliminar comentario") },
            text = { Text("¿Estás seguro que deseas eliminar tu comentario?") },
            confirmButton = {
                TextButton(
                    onClick = {
                        mostrarDialogoConfirmacion = false
                        onDelete()
                    }
                ) {
                    Text("Eliminar")
                }
            },
            dismissButton = {
                TextButton(
                    onClick = { mostrarDialogoConfirmacion = false }
                ) {
                    Text("Cancelar")
                }
            }
        )
    }
}

@Composable
fun RatingBar(
    rating: Float,
    modifier: Modifier = Modifier
) {
    Row(modifier = modifier) {
        val fullStars = rating.toInt()
        val fractionalPart = rating - fullStars
        
        // Mostramos estrellas completas
        repeat(fullStars) {
            Icon(
                imageVector = Icons.Default.Star,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary
            )
        }
        
        // Mostramos una estrella parcialmente llena para la parte fraccionaria
        if (fractionalPart > 0) {
            // En lugar de usar StarHalf, usamos un Box con una estrella llena
            // y aplicamos un clip para mostrar solo la parte proporcional
            Box(modifier = Modifier.size(24.dp)) {
                // Estrella vacía de fondo
                Icon(
                    imageVector = Icons.Outlined.Star,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.fillMaxSize()
                )
                
                // Estrella llena con clip para mostrar la parte proporcional
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .clip(
                            ClipShape(fraction = fractionalPart)
                        )
                ) {
                    Icon(
                        imageVector = Icons.Default.Star,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.fillMaxSize()
                    )
                }
            }
        }
        
        // Mostramos estrellas vacías para completar 5 estrellas
        repeat(5 - fullStars - (if (fractionalPart > 0) 1 else 0)) {
            Icon(
                imageVector = Icons.Outlined.Star,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary
            )
        }
    }
}

// Clase de forma personalizada para recortar la estrella
private class ClipShape(private val fraction: Float) : Shape {
    override fun createOutline(
        size: Size,
        layoutDirection: LayoutDirection,
        density: Density
    ): Outline {
        return Outline.Rectangle(
            Rect(
                left = 0f,
                top = 0f,
                right = size.width * fraction,
                bottom = size.height
            )
        )
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