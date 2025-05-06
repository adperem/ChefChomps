package com.example.chefchomps.ui

import ChefChompsTema
import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ExitToApp
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.example.chefchomps.R
import com.example.chefchomps.logica.DatabaseHelper
import com.example.chefchomps.persistencia.MockerRecetas
import com.example.chefchomps.ui.profile.ProfileScreen
import com.example.chefchomps.ui.profile.ProfileViewModel

/**
 * Class que define la página principal de la app ChefChomps
 *
 */
class PaginaPrincipal : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            var darkTheme by remember { mutableStateOf(false) }

            ChefChompsTema(darkTheme = darkTheme) {
                Surface(modifier = Modifier.fillMaxSize()) {
                    PaginaPrincipal(darkTheme = darkTheme, onThemeChange = { darkTheme = it })
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
    @Composable
    fun PaginaPrincipal(
        modifier: Modifier = Modifier,
        uiState: ViewModelPaginaPrincipal = ViewModelPaginaPrincipal(),
        darkTheme: Boolean,
        onThemeChange: (Boolean) -> Unit
    ) {
        val context = LocalContext.current
        val databaseHelper = remember { DatabaseHelper() }
        var showMenu by remember { mutableStateOf(false) }
        var showProfile by remember { mutableStateOf(false) }
        val profileViewModel = remember { ProfileViewModel() }

        //uiState.updatelist(runBlocking { getRandomRecipe() } )
        var text by remember { mutableStateOf("") }
        val focusManager = LocalFocusManager.current
        val textFieldFocusRequester = remember { FocusRequester() }
        val state = rememberScrollState()
        val scrollBehavior = TopAppBarDefaults.enterAlwaysScrollBehavior()

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
                                text = { Text(if (darkTheme) "Modo Claro" else "Modo Oscuro") },
                                onClick = {
                                    onThemeChange(!darkTheme)
                                    showMenu = false },
                                leadingIcon = {
                                    Icon(
                                        painter = painterResource(
                                            if (darkTheme) R.drawable.ic_sun else R.drawable.ic_moon
                                        ),
                                        contentDescription = "Cambiar tema"
                                    )
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
                    LazyColumn(
                        modifier = modifier
                            .padding(paddingValues)
                            .fillMaxWidth()
                    ) {
                        items(
                            items = MockerRecetas.Recetas(),
                            key = { item -> item.title }
                        ) { aux ->
                            RowReceta(aux)
                        }
                    }
                }
            }
        }
    }
}