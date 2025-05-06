package com.example.chefchomps.ui

import ChefChompsTema
import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.Image
import androidx.compose.runtime.mutableStateOf
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ExitToApp
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.ExitToApp
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ListItem
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.em
import coil.compose.AsyncImage
import com.example.chefchomps.R
import com.example.chefchomps.logica.ApiCLient
import com.example.chefchomps.logica.ApiCLient.Companion.autocompleteRecipes
import com.example.chefchomps.logica.ApiCLient.Companion.getRandomRecipe
import com.example.chefchomps.logica.DatabaseHelper
import com.example.chefchomps.ui.Login
import com.example.chefchomps.model.Recipe
import com.example.chefchomps.persistencia.MockerRecetas
import com.example.chefchomps.ui.profile.ProfileScreen
import com.example.chefchomps.ui.profile.ProfileViewModel
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking

/**
 * Class que define la página principal de la app ChefChomps
 *
 */
class PaginaPrincipal : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            ChefChompsTema(darkTheme = false) {
                Surface(modifier = Modifier.fillMaxSize()) {
                    PaginaPrincipal()
                }
            }
        }
    }

    /**
     * Pagina de inicio para la aplicacion
     * @param modifier modificador que define comportamiento
     * @param uiState contiene todos los datos relacionados con la página principal
     * */
    @OptIn(ExperimentalMaterial3Api::class)
    @SuppressLint("NotConstructor")
    @Preview
    @Composable
    fun PaginaPrincipal(
        modifier: Modifier = Modifier,
        uiState: ViewModelPaginaPrincipal = ViewModelPaginaPrincipal()
    ) {
        val context = LocalContext.current
        val databaseHelper = remember { DatabaseHelper() }
        var showMenu by remember { mutableStateOf(false) }
        var showProfile by remember { mutableStateOf(false) }
        val profileViewModel = remember { ProfileViewModel() }

        var searchText by remember { mutableStateOf("") }
        var isSearching by remember { mutableStateOf(false) }
        val focusManager = LocalFocusManager.current
        val textFieldFocusRequester = remember { FocusRequester() }
        val state = rememberScrollState()
        val scrollBehavior = TopAppBarDefaults.enterAlwaysScrollBehavior()

        // Inicializar con recetas aleatorias si no hay recetas en el ViewModel
        LaunchedEffect(Unit) {
            if (uiState.getlist().isEmpty()) {
                uiState.updatelist(runBlocking { getRandomRecipe() })
            }
        }

        Scaffold(
            topBar = {
                TopAppBar(
                    title = { Text("ChefChomps") },
                    actions = {
                        IconButton(onClick = { showMenu = !showMenu }) {
                            Icon(Icons.Default.Menu, contentDescription = "Menú")
                        }
                        DropdownMenu(
                            expanded = showMenu,
                            onDismissRequest = { showMenu = false }
                        ) {
                            DropdownMenuItem(
                                text = { Text("Perfil") },
                                onClick = {
                                    showMenu = false
                                    showProfile = true
                                },
                                leadingIcon = {
                                    Icon(Icons.Default.Person, contentDescription = "Perfil")
                                }
                            )
                            DropdownMenuItem(
                                text = { Text("Buscar") },
                                onClick = {
                                    showMenu = false
                                    context.startActivity(Intent(context, Search::class.java))
                                },
                                leadingIcon = {
                                    Icon(Icons.Default.Search, contentDescription = "Buscar")
                                }
                            )
                            DropdownMenuItem(
                                text = { Text("Nueva Receta") },
                                onClick = {
                                    showMenu = false
                                    context.startActivity(Intent(context, NuevaReceta::class.java))
                                },
                                leadingIcon = {
                                    Icon(Icons.Default.Add, contentDescription = "Nueva Receta")
                                }
                            )
                            DropdownMenuItem(
                                text = { Text("Cerrar Sesión") },
                                onClick = {
                                    showMenu = false
                                    databaseHelper.signOut()
                                    context.startActivity(Intent(context, Login::class.java))
                                },
                                leadingIcon = {
                                    Icon(Icons.Default.ExitToApp, contentDescription = "Cerrar Sesión")
                                }
                            )
                        }
                    },
                    scrollBehavior = scrollBehavior
                )
            },
            modifier = modifier.padding(10.dp)
        ) { paddingValues ->
            Box(modifier = Modifier.padding(paddingValues)) {
                if (showProfile) {
                    ProfileScreen(
                        viewModel = profileViewModel,
                        onNavigateToLogin = {
                            showProfile = false
                            context.startActivity(Intent(context, Login::class.java))
                        }
                    )
                } else {
                    Column(modifier = Modifier.fillMaxWidth()) {
                        // Barra de búsqueda
                        OutlinedTextField(
                            value = searchText,
                            onValueChange = { searchText = it },
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 16.dp, vertical = 8.dp)
                                .focusRequester(textFieldFocusRequester),
                            placeholder = { Text("Buscar recetas...") },
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
                                        focusManager.clearFocus()
                                        uiState.clear()
                                        
                                        // Realizar búsqueda
                                        val results = runBlocking { 
                                            autocompleteRecipes(searchText) 
                                        }
                                        uiState.updatelist(results)
                                        isSearching = false
                                    }
                                }
                            )
                        )

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
                        }
                        
                        Spacer(modifier = Modifier.height(8.dp))
                        
                        // Lista de recetas
                        RecetasListFromViewModel(
                            viewModel = uiState,
                            modifier = modifier
                                .fillMaxWidth(),
                            onRecetaClick = { receta ->
                                // Navegación a la pantalla de detalle
                                val intent = Intent(context, PaginaDetalle::class.java)
                                intent.putExtra("receta_id", receta.id ?: -1)
                                intent.putExtra("receta_title", receta.title)
                                intent.putExtra("receta_image", receta.image ?: "")
                                context.startActivity(intent)
                            }
                        )
                    }
                }
            }
        }
    }
}