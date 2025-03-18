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
import com.example.chefchomps.persistencia.DatabaseConnection

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            LoginLayout {
                Toast.makeText(this, it, Toast.LENGTH_SHORT).show()
            }
        }
    }
}

@Composable
fun LoginLayout(showToast: (String) -> Unit) {
    var emailInput by remember { mutableStateOf("") }
    var passwordInput by remember { mutableStateOf("") }

    val dbConnection = DatabaseConnection()
    val dbHelper = DatabaseHelper(dbConnection)

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        TextField(
            value = emailInput,
            onValueChange = { emailInput = it },
            label = { Text("Email") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(16.dp))

        TextField(
            value = passwordInput,
            onValueChange = { passwordInput = it },
            label = { Text("Contraseña") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = {
                val success = dbHelper.registerUser(emailInput, passwordInput, "Juan", "Pérez")
                if (success) {
                    showToast("Registro exitoso")
                } else {
                    showToast("Error al registrar")
                }
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Registrar")
        }

        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = {
                val success = dbHelper.loginUser(emailInput, passwordInput)
                if (success) {
                    showToast("Inicio de sesión exitoso")
                } else {
                    showToast("Usuario o contraseña incorrectos")
                }
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Iniciar sesión")
        }
    }
}

@Preview(showBackground = true)
@Composable
fun DefaultPreview() {
    LoginLayout {
    }
}
