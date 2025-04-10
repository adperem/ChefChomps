package com.example.chefchomps.ui

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.chefchomps.model.Recipe

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PaginaPrincipal(
    viewModel: ViewModelPaginaPrincipal,
    onNavigateToLogin: () -> Unit,
    onNavigateToProfile: () -> Unit,
    modifier: Modifier = Modifier
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("ChefChomps") },
                actions = {
                    IconButton(onClick = { viewModel.toggleMenu() }) {
                        Icon(
                            imageVector = if (uiState.isMenuExpanded) Icons.Default.Close else Icons.Default.Menu,
                            contentDescription = "Menú"
                        )
                    }
                }
            )
        }
    ) { paddingValues ->
        Box(modifier = Modifier.padding(paddingValues)) {
            // Contenido principal
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp)
            ) {
                // Lista de recetas
                RecipeList(recipes = uiState.lrecipe)
            }

            // Menú desplegable
            if (uiState.isMenuExpanded) {
                DropdownMenu(
                    expanded = uiState.isMenuExpanded,
                    onDismissRequest = { viewModel.toggleMenu() },
                    modifier = Modifier.fillMaxWidth(0.7f)
                ) {
                    // Opciones de autenticación
                    if (uiState.currentUser == null) {
                        DropdownMenuItem(
                            text = { Text("Iniciar sesión") },
                            onClick = {
                                viewModel.toggleMenu()
                                onNavigateToLogin()
                            },
                            leadingIcon = {
                                Icon(Icons.Default.Login, contentDescription = null)
                            }
                        )
                        DropdownMenuItem(
                            text = { Text("Registrarse") },
                            onClick = {
                                viewModel.toggleMenu()
                                onNavigateToLogin()
                            },
                            leadingIcon = {
                                Icon(Icons.Default.PersonAdd, contentDescription = null)
                            }
                        )
                    } else {
                        DropdownMenuItem(
                            text = { Text("Mi perfil") },
                            onClick = {
                                viewModel.toggleMenu()
                                onNavigateToProfile()
                            },
                            leadingIcon = {
                                Icon(Icons.Default.Person, contentDescription = null)
                            }
                        )
                        DropdownMenuItem(
                            text = { Text("Cerrar sesión") },
                            onClick = {
                                viewModel.toggleMenu()
                                viewModel.signOut()
                            },
                            leadingIcon = {
                                Icon(Icons.Default.Logout, contentDescription = null)
                            }
                        )
                    }

                    Divider()

                    // Opciones de filtros
                    DropdownMenuItem(
                        text = { Text("Buscar por ingredientes") },
                        onClick = {
                            viewModel.toggleMenu()
                            // TODO: Implementar búsqueda por ingredientes
                        },
                        leadingIcon = {
                            Icon(Icons.Default.Search, contentDescription = null)
                        }
                    )
                    DropdownMenuItem(
                        text = { Text("Recetas aleatorias") },
                        onClick = {
                            viewModel.toggleMenu()
                            // TODO: Implementar recetas aleatorias
                        },
                        leadingIcon = {
                            Icon(Icons.Default.Refresh, contentDescription = null)
                        }
                    )
                    DropdownMenuItem(
                        text = { Text("Autocompletar búsqueda") },
                        onClick = {
                            viewModel.toggleMenu()
                            // TODO: Implementar autocompletado
                        },
                        leadingIcon = {
                            Icon(Icons.Default.AutoAwesome, contentDescription = null)
                        }
                    )
                }
            }
        }
    }
}

@Composable
fun RecipeList(
    recipes: List<Recipe>,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        recipes.forEach { recipe ->
            RecipeItem(recipe = recipe)
        }
    }
}

@Composable
fun RecipeItem(
    recipe: Recipe,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth(),
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Text(
                text = recipe.title,
                style = MaterialTheme.typography.titleLarge
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = recipe.instructions ?: "Sin instrucciones disponibles",
                style = MaterialTheme.typography.bodyMedium
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun PaginaPrincipalPreview() {
    PaginaPrincipal(
        viewModel = ViewModelPaginaPrincipal(),
        onNavigateToLogin = {},
        onNavigateToProfile = {},
        modifier = Modifier
    )
}