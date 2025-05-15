package com.example.chefchomps.ui.profile

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.chefchomps.model.Usuario

/**
 * Pantalla de perfil de usuario que muestra la información del usuario autenticado.
 *
 * @param viewModel ViewModel que proporciona el estado de la UI y maneja las acciones del usuario.
 * @param onNavigateToLogin Función lambda para navegar a la pantalla de login cuando el usuario
 * no está autenticado o cierra sesión.
 * @param modifier Modificador para personalizar el diseño de la pantalla.
 */
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
                onDeleteAccount = viewModel::deleteUserAccount,
                modifier = modifier
            )
        }
    }
}

/**
 * Composable privado que muestra el formulario de perfil del usuario, permitiendo
 * visualizar y editar los datos del usuario, cerrar sesión o eliminar la cuenta.
 *
 * @param usuario Objeto [Usuario] con los datos actuales del usuario.
 * @param onSaveChanges Función lambda para guardar los cambios en el perfil.
 * @param onSignOut Función lambda para cerrar la sesión del usuario.
 * @param onDeleteAccount Función lambda para eliminar la cuenta del usuario.
 * @param modifier Modificador para personalizar el diseño del contenido.
 */
@Composable
private fun ProfileContent(
    usuario: Usuario,
    onSaveChanges: (Usuario) -> Unit,
    onSignOut: () -> Unit,
    onDeleteAccount: () -> Unit,
    modifier: Modifier = Modifier
) {
    var nombre by remember { mutableStateOf(usuario.nombre) }
    var apellidos by remember { mutableStateOf(usuario.apellidos) }
    var email by remember { mutableStateOf(usuario.email) }
    var password by remember { mutableStateOf(usuario.password) }
    var username by remember { mutableStateOf(usuario.username) }
    var isEditing by remember { mutableStateOf(false) }
    val context = LocalContext.current

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

        Spacer(modifier = Modifier.height(8.dp))

        Button(
            onClick = onSignOut,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Cerrar sesión")
        }

        Spacer(modifier = Modifier.height(16.dp))
        
        // Botón para eliminar cuenta
        var mostrarDialogoConfirmacion by remember { mutableStateOf(false) }
        
        Button(
            onClick = { mostrarDialogoConfirmacion = true },
            modifier = Modifier.fillMaxWidth(),
            colors = ButtonDefaults.buttonColors(
                containerColor = MaterialTheme.colorScheme.error
            )
        ) {
            Text("Eliminar mi cuenta")
        }
        
        // Diálogo de confirmación para eliminar cuenta
        if (mostrarDialogoConfirmacion) {
            AlertDialog(
                onDismissRequest = { mostrarDialogoConfirmacion = false },
                title = { Text("Eliminar cuenta") },
                text = { Text("¿Estás seguro que deseas eliminar tu cuenta? Esta acción no se puede deshacer y perderás todas tus recetas y datos personales.") },
                confirmButton = {
                    TextButton(
                        onClick = {
                            mostrarDialogoConfirmacion = false
                            onDeleteAccount()
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
}

/**
 * Vista previa del contenido del perfil con datos de ejemplo.
 */
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
        onDeleteAccount = {},
        modifier = Modifier
    )
}

/**
 * Vista previa de la pantalla de perfil en estado de carga.
 */
@Preview(showBackground = true)
@Composable
fun ProfileScreenLoadingPreview() {
    ProfileScreen(
        viewModel = ProfileViewModel(),
        onNavigateToLogin = {},
        modifier = Modifier
    )
} 