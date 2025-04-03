package com.example.chefchomps.logica

import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.chefchomps.logica.DatabaseHelper
import kotlinx.coroutines.launch

/**
 * Actividad principal para la pantalla de inicio de sesión y registro del usuario.
 */
class Login : ComponentActivity() {
    /**
     * Metodo de ciclo de vida que se ejecuta al crear la actividad.
     *
     * @param savedInstanceState Estado previamente guardado de la actividad.
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            LoginLayout { message ->
                Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
            }
        }
    }
}

/**
 * Composable que representa el diseño de la pantalla de inicio de sesión y registro.
 *
 * @param showToast Función de callback para mostrar mensajes emergentes.
 */
@Composable
fun LoginLayout(showToast: (String) -> Unit) {
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    val firebaseHelper = remember { DatabaseHelper() }
    val coroutineScope = rememberCoroutineScope()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        TextField(
            value = email,
            onValueChange = { email = it },
            label = { Text("Email") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(16.dp))

        TextField(
            value = password,
            onValueChange = { password = it },
            label = { Text("Contraseña") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = {
                coroutineScope.launch {
                    val success = firebaseHelper.registerUser(email, password, "Pepe", "Perez")
                    if (success) {
                        showToast("Registro exitoso")
                    } else {
                        showToast("Error al registrar")
                    }
                }
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Registrar")
        }

        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = {
                coroutineScope.launch {
                    val success = firebaseHelper.loginUser(email, password)
                    if (success) {
                        showToast("Inicio de sesión exitoso")
                    } else {
                        showToast("Usuario o contraseña incorrectos")
                    }
                }
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Iniciar sesión")
        }
    }
}

/**
 * Previsualización del diseño de la pantalla de inicio de sesión y registro.
 */
@Preview(showBackground = true)
@Composable
fun DefaultPreview() {
    LoginLayout {}
}
