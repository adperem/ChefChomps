package com.example.chefchomps.ui

import ChefChompsTema
import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.chefchomps.R
import com.example.chefchomps.logica.ApiCLient.Companion.findRecipesByIngredients
import com.example.chefchomps.model.Recipe
import kotlinx.coroutines.runBlocking

// Constantes para SharedPreferences
private const val PREFS_NAME = "ChefChompsPrefs"
private const val KEY_DARK_THEME = "dark_theme"

/***
 * Clase que contiene la funcion para buscar por lista de String
 */
class Search :ComponentActivity(){
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        
        // Leer la preferencia del tema oscuro
        val sharedPref = getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        val savedDarkTheme = sharedPref.getBoolean(KEY_DARK_THEME, false)
        
        setContent {
            ChefChompsTema(darkTheme = savedDarkTheme){
                Surface (modifier = Modifier.fillMaxSize())
                {
                    Search()
                }
            }
        }
    }

    /***
     * Busca por una lista de string
     *
     * @param uiState modificador que define comportamiento (opcional)
     * @param funcion una funcion que recibe una lista de string(ingredientes) y devuelve lista de recetas (opcional)
     */
    @SuppressLint("NotConstructor")
    @Composable
    fun Search(
        uiState: ViewModelPaginaPrincipal = ViewModelPaginaPrincipal(),
        funcion: (List<String>) -> List<Recipe> = { ingredientes -> 
            runBlocking { findRecipesByIngredients(ingredientes) }
        }
    ){
        var text by remember { mutableStateOf("") }
        val listaIngredientes = remember { mutableStateListOf<String>() }
        var recetasEncontradas by remember { mutableStateOf<List<Recipe>>(emptyList()) }
        var mostrarRecetas by remember { mutableStateOf(false) }
        val focusManager = LocalFocusManager.current
        val textFieldFocusRequester = remember { FocusRequester() }
        val context = LocalContext.current

        Column(modifier = Modifier.fillMaxWidth()) {
            if (!mostrarRecetas) {
                // Pantalla de búsqueda de ingredientes
                Text(
                    text = "Buscar por ingredientes",
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    textAlign = TextAlign.Center
                )
                
                Row(modifier = Modifier.padding(horizontal = 8.dp)) {
                    TextField(
                        value = text,
                        onValueChange = { text = it },
                        label = { Text("Ingresa un ingrediente") },
                        keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
                        keyboardActions = KeyboardActions(
                            onDone = {
                                if (text.isNotBlank()) {
                                    listaIngredientes.add(text)
                                    text = ""
                                }
                            }),
                        modifier = Modifier
                            .weight(1f)
                            .focusRequester(textFieldFocusRequester)
                    )
                    IconButton(onClick = {
                        if (text.isNotBlank()) {
                            listaIngredientes.add(text)
                            text = ""
                        }
                    }) {
                        Icon(
                            painter = painterResource(id = R.drawable.baseline_add_24),
                            contentDescription = "Añadir ingrediente",
                        )
                    }
                }
                
                Spacer(modifier = Modifier.height(8.dp))
                
                Text(
                    text = "Ingredientes seleccionados:",
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
                    fontWeight = FontWeight.Bold
                )
                
                LazyColumn(
                    modifier = Modifier
                        .weight(1f)
                        .padding(horizontal = 16.dp)
                ) {
                    items(listaIngredientes) { ingrediente ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 4.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(text = ingrediente)
                            Spacer(modifier = Modifier.weight(1f))
                            IconButton(
                                onClick = {
                                    listaIngredientes.remove(ingrediente)
                                }
                            ) {
                                Icon(
                                    painter = painterResource(id = R.drawable.minus),
                                    contentDescription = "Eliminar ingrediente",
                                )
                            }
                        }
                        Divider()
                    }
                }
                
                Button(
                    onClick = {
                        focusManager.clearFocus()
                        if (listaIngredientes.isNotEmpty()) {
                            recetasEncontradas = funcion(listaIngredientes)
                            uiState.updatelist(recetasEncontradas)
                            mostrarRecetas = true
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                ) {
                    Text("Buscar recetas")
                }
            } else {
                // Pantalla de resultados de recetas
                Text(
                    text = "Recetas encontradas",
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    textAlign = TextAlign.Center
                )
                
                RecetasList(
                    recipes = recetasEncontradas,
                    isLoading = false,
                    emptyMessage = "No se encontraron recetas con estos ingredientes",
                    onRecetaClick = { receta ->
                        // Navegación a pantalla de detalle
                        val intent = Intent(context, PaginaDetalle::class.java)
                        intent.putExtra("receta_id", receta.id)
                        context.startActivity(intent)
                    },
                    modifier = Modifier
                        .weight(1f)
                        .padding(horizontal = 16.dp)
                )
                
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                ) {
                    Button(
                        onClick = {
                            mostrarRecetas = false
                        },
                        modifier = Modifier.weight(1f)
                    ) {
                        Text("Volver a buscar")
                    }
                    
                    Spacer(modifier = Modifier.padding(8.dp))
                    
                    Button(
                        onClick = {
                            val intent = Intent(context, PaginaPrincipal::class.java)
                            context.startActivity(intent)
                        },
                        modifier = Modifier.weight(1f)
                    ) {
                        Text("Ir a inicio")
                    }
                }
            }
        }
    }

    @Preview
    @Composable
    fun ExpandedSearchViewPreview() {
        Surface {
            Search()
        }
    }
}