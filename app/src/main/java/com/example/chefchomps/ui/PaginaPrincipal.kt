package com.example.chefchomps.ui

import LabeledMaterialSwitch
import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.Image
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
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
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
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
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.chefchomps.R
import com.example.chefchomps.logica.ApiCLient
import com.example.chefchomps.logica.ApiCLient.Companion.autocompleteRecipes
import com.example.chefchomps.logica.DatabaseHelper
import com.example.chefchomps.ui.profile.ProfileScreen
import com.example.chefchomps.ui.profile.ProfileViewModel
import kotlinx.coroutines.runBlocking

// Constantes para SharedPreferences
private const val PREFS_NAME = "ChefChompsPrefs"
private const val KEY_DARK_THEME = "dark_theme"

/**
 * Class que define la página principal de la app ChefChomps
 *
 */
class PaginaPrincipal : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        ThemeManager.initialize(this)

        setContent {
            ChefChompsAppTheme {
                Surface(modifier = Modifier.fillMaxSize()) {
                    PaginaPrincipal(
                        onThemeChange = { enabled ->
                            ThemeManager.setDarkTheme(enabled, this)
                        }
                    )
                }
            }
        }
    }

    /**
     * Pagina de inicio para la aplicacion
     *
     * @param modifier modificador que define comportamiento
     * @param uiState contiene todos los datos relacionados con la página principal
     * */
    @OptIn(ExperimentalMaterial3Api::class)
    @SuppressLint("NotConstructor")
    @Preview
    @Composable
    fun PaginaPrincipal(
        modifier: Modifier = Modifier,
        uiState: ViewModelPaginaPrincipal = ViewModelPaginaPrincipal(),
        onThemeChange: (Boolean) -> Unit = {}
    ) {
        val context = LocalContext.current
        val databaseHelper = remember { DatabaseHelper() }
        var showMenu by remember { mutableStateOf(false) }
        var showProfile by remember { mutableStateOf(false) }
        val profileViewModel = remember { ProfileViewModel() }
        val darkTheme by LocalDarkTheme.current

        var searchText by remember { mutableStateOf("") }
        var isSearching by remember { mutableStateOf(false) }
        val focusManager = LocalFocusManager.current
        val textFieldFocusRequester = remember { FocusRequester() }
        val scrollBehavior = TopAppBarDefaults.enterAlwaysScrollBehavior()

        // Inicializar con recetas aleatorias si no hay recetas en el ViewModel
        LaunchedEffect(darkTheme) {
            if (uiState.getlist().isEmpty()) {
                isSearching = true  // Mostrar indicador de carga mientras se obtienen las recetas
                try {
                    val randomRecipes = ApiCLient.getRandomRecipe()
                    uiState.updatelist(randomRecipes)
                } catch (e: Exception) {
                    // Manejar posible error
                } finally {
                    isSearching = false  // Ocultar indicador de carga
                }
            }
        }

        Scaffold(
            topBar = {
                TopAppBar(
                    title = { 
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.clickable {
                                // Si estamos en una vista diferente a la principal, volvemos a ella
                                if (showProfile) {
                                    showProfile = false
                                }
                                // Recargar recetas aleatorias si la lista está vacía
                                if (uiState.getlist().isEmpty()) {
                                    isSearching = true
                                    runBlocking {
                                        try {
                                            val randomRecipes = ApiCLient.getRandomRecipe()
                                            uiState.updatelist(randomRecipes)
                                        } finally {
                                            isSearching = false
                                        }
                                    }
                                }
                            }
                        ) {
                            Image(
                                painter = painterResource(id = R.drawable.chomper),
                                contentDescription = "Logo ChefChomps",
                                modifier = Modifier
                                    .size(40.dp)
                                    .padding(end = 8.dp)
                            )
                            Text("ChefChomps")
                        }
                    },
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
                                text = { Text("Filtrar") },
                                onClick = {
                                    showMenu = false
                                    context.startActivity(Intent(context, Search::class.java))
                                },
                                leadingIcon = {
                                    Icon(Icons.Default.Search, contentDescription = "Filtar")
                                }
                            )
                            DropdownMenuItem(
                                text = { Text("Buscar por usuario") },
                                onClick = {
                                    showMenu = false
                                    context.startActivity(Intent(context, BuscarPorUsuario::class.java))
                                },
                                leadingIcon = {
                                    Icon(Icons.Default.Person, contentDescription = "Buscar por usuario")
                                }
                            )
                            DropdownMenuItem(
                                text = { Text("Mis Recetas") },
                                onClick = {
                                    showMenu = false
                                    context.startActivity(Intent(context, MisRecetasActivity::class.java))
                                },
                                leadingIcon = {
                                    Icon(painterResource(id = R.drawable.ic_recipe), contentDescription = "Mis Recetas")
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
                                text = {
                                    LabeledMaterialSwitch(
                                        checked = darkTheme,
                                        onCheckedChange = { checked ->
                                            onThemeChange(checked)
                                            showMenu = false
                                        },
                                        label = if (darkTheme) "Modo Claro" else "Modo Oscuro",
                                        modifier = Modifier.weight(1f)
                                    )
                                },
                                onClick = {
                                    // Al hacer clic en el elemento completo, invertimos el estado actual
                                    onThemeChange(!darkTheme)
                                    showMenu = false
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
                        
                        // Título de sección
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 16.dp, vertical = 8.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = if (searchText.isEmpty()) "Recetas Recomendadas" else "Resultados de búsqueda",
                                style = MaterialTheme.typography.titleLarge,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.weight(1f)
                            )
                            
                            // Botón para recargar recetas aleatorias
                            if (searchText.isEmpty()) {
                                IconButton(
                                    onClick = {
                                        isSearching = true
                                        uiState.clear()
                                        runBlocking {
                                            try {
                                                val randomRecipes = ApiCLient.getRandomRecipe()
                                                uiState.updatelist(randomRecipes)
                                            } finally {
                                                isSearching = false
                                            }
                                        }
                                    }
                                ) {
                                    Icon(
                                        painter = painterResource(id = R.drawable.ic_refresh),
                                        contentDescription = "Recargar recetas",
                                        modifier = Modifier.size(24.dp)
                                    )
                                }
                            }
                        }
                        
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