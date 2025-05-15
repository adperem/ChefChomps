package com.example.chefchomps.ui

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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.chefchomps.model.Recipe
import com.example.chefchomps.ui.components.DetalleRecetaContent
import com.google.gson.Gson

class VerReceta : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        // Obtener la receta serializada del intent
        val recetaJson = intent.getStringExtra("receta_json")
        
        if (recetaJson.isNullOrEmpty()) {
            finish()
            return
        }
        
        // Deserializar la receta
        val receta = try {
            Gson().fromJson(recetaJson, Recipe::class.java)
        } catch (e: Exception) {
            finish()
            return
        }
        
        setContent {
            ChefChompsAppTheme {
                Surface(modifier = Modifier.fillMaxSize()) {
                    VerRecetaScreen(
                        receta = receta,
                        onBack = { finish() },
                        onContinuar = {
                            // Navegar a la página principal
                            val intent = Intent(this, PaginaPrincipal::class.java)
                            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK
                            startActivity(intent)
                            finish()
                        }
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun VerRecetaScreen(
    receta: Recipe,
    onBack: () -> Unit,
    onContinuar: () -> Unit
) {
    val context = LocalContext.current
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Receta Creada") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Volver")
                    }
                },
                actions = {
                    // Botón para compartir
                    IconButton(onClick = {
                        val shareIntent = Intent().apply {
                            action = Intent.ACTION_SEND
                            putExtra(Intent.EXTRA_SUBJECT, "¡Mira mi receta de ${receta.title}!")
                            putExtra(Intent.EXTRA_TEXT, "He creado una nueva receta en ChefChomps: ${receta.title}\n\n${receta.summary}")
                            type = "text/plain"
                        }
                        context.startActivity(Intent.createChooser(shareIntent, "Compartir receta"))
                    }) {
                        Icon(Icons.Default.Share, contentDescription = "Compartir")
                    }
                }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .verticalScroll(rememberScrollState())
        ) {
            // Mensaje de éxito
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "¡Tu receta ha sido creada con éxito!",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Center,
                    color = MaterialTheme.colorScheme.primary
                )
            }
            
            // Detalles de la receta
            DetalleRecetaContent(recipe = receta)
            
            // Botón para continuar
            Spacer(modifier = Modifier.height(24.dp))
            Button(
                onClick = onContinuar,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp)
            ) {
                Text("Continuar")
            }
            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}