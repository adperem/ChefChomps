package com.example.chefchomps.ui.profile

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.chefchomps.model.Usuario

@Composable
fun ProfileScreen(
    viewModel: ProfileViewModel,
    onNavigateToLogin: () -> Unit,
    modifier: Modifier = Modifier
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    when (val state = uiState) {
        is ProfileUiState.Loading -> {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }
        }
        is ProfileUiState.NotAuthenticated -> {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Text(
                    text = "No has iniciado sesión",
                    style = MaterialTheme.typography.headlineMedium
                )
                Spacer(modifier = Modifier.height(16.dp))
                Button(onClick = onNavigateToLogin) {
                    Text("Ir al login")
                }
            }
        }
        is ProfileUiState.Error -> {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = state.message,
                    color = MaterialTheme.colorScheme.error
                )
            }
        }
        is ProfileUiState.Success -> {
            ProfileContent(
                usuario = state.usuario,
                onSaveChanges = viewModel::updateUserProfile,
                onSignOut = viewModel::signOut,
                modifier = modifier
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ProfileContent(
    usuario: Usuario,
    onSaveChanges: (Usuario) -> Unit,
    onSignOut: () -> Unit,
    modifier: Modifier = Modifier
) {
    var nombre by remember { mutableStateOf(usuario.nombre) }
    var apellidos by remember { mutableStateOf(usuario.apellidos) }
    var email by remember { mutableStateOf(usuario.email) }
    var password by remember { mutableStateOf(usuario.password) }
    var username by remember { mutableStateOf(usuario.username) }
    var isEditing by remember { mutableStateOf(false) }

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text(
            text = "Mi Perfil",
            style = MaterialTheme.typography.headlineMedium
        )

        OutlinedTextField(
            value = nombre,
            onValueChange = { if (isEditing) nombre = it },
            label = { Text("Nombre") },
            enabled = isEditing,
            modifier = Modifier.fillMaxWidth()
        )

        OutlinedTextField(
            value = apellidos,
            onValueChange = { if (isEditing) apellidos = it },
            label = { Text("Apellidos") },
            enabled = isEditing,
            modifier = Modifier.fillMaxWidth()
        )

        OutlinedTextField(
            value = username,
            onValueChange = { if (isEditing) username = it },
            label = { Text("Nombre de usuario") },
            enabled = isEditing,
            modifier = Modifier.fillMaxWidth()
        )

        OutlinedTextField(
            value = email,
            onValueChange = { if (isEditing) email = it },
            label = { Text("Email") },
            enabled = isEditing,
            modifier = Modifier.fillMaxWidth()
        )

        OutlinedTextField(
            value = password,
            onValueChange = { if (isEditing) password = it },
            label = { Text("Contraseña") },
            visualTransformation = PasswordVisualTransformation(),
            enabled = isEditing,
            modifier = Modifier.fillMaxWidth()
        )

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            Button(
                onClick = { 
                    if (isEditing) {
                        onSaveChanges(Usuario(email, nombre, apellidos, password, username))
                    }
                    isEditing = !isEditing 
                }
            ) {
                Text(if (isEditing) "Guardar" else "Editar")
            }

            if (isEditing) {
                Button(
                    onClick = { 
                        nombre = usuario.nombre
                        apellidos = usuario.apellidos
                        email = usuario.email
                        password = usuario.password
                        username = usuario.username
                        isEditing = false
                    }
                ) {
                    Text("Cancelar")
                }
            }
        }

        Button(
            onClick = onSignOut,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Cerrar sesión")
        }
    }
}

@Preview(showBackground = true)
@Composable
fun ProfileScreenPreview() {
    val previewUsuario = Usuario(
        email = "usuario@ejemplo.com",
        nombre = "Juan",
        apellidos = "Pérez",
        password = "********",
        username = "juanperez"
    )
    
    ProfileContent(
        usuario = previewUsuario,
        onSaveChanges = {},
        onSignOut = {},
        modifier = Modifier
    )
}

@Preview(showBackground = true)
@Composable
fun ProfileScreenLoadingPreview() {
    ProfileScreen(
        viewModel = ProfileViewModel(),
        onNavigateToLogin = {},
        modifier = Modifier
    )
} 