package com.example.chefchomps.ui

import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import com.example.chefchomps.R
import com.example.chefchomps.logica.DatabaseHelper
import com.example.chefchomps.logica.DatabaseHelper.RegistroResultado
import kotlinx.coroutines.launch

class RegistroActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MaterialTheme {
                Surface(modifier = Modifier.fillMaxSize()) {
                    RegistroLayout(
                        showToast = { mensaje ->
                            Toast.makeText(this, mensaje, Toast.LENGTH_SHORT).show()
                        },
                        onBack = { finish() }
                    )
                }
            }
        }
    }
}

@Composable
fun RegistroLayout(showToast: (String) -> Unit, onBack: () -> Unit) {
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var nombre by remember { mutableStateOf("") }
    var apellido by remember { mutableStateOf("") }
    var isLoading by remember { mutableStateOf(false) }

    val databaseHelper = remember { DatabaseHelper() }
    val coroutineScope = rememberCoroutineScope()
    val context = LocalContext.current
    val activity = context as? ComponentActivity

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Start
        ) {
            IconButton(
                onClick = onBack,
                modifier = Modifier.size(48.dp)
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.ic_arrow_back),
                    contentDescription = "Volver atrás",
                    modifier = Modifier.size(24.dp)
                )
            }
        }
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text("Registro de usuario", style = MaterialTheme.typography.headlineSmall)

            Spacer(modifier = Modifier.height(16.dp))

            OutlinedTextField(
                value = email,
                onValueChange = { email = it },
                label = { Text("Email") },
                modifier = Modifier.fillMaxWidth(),
                isError = email.isNotBlank() && !android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches(),
                supportingText = {
                    if (email.isNotBlank() && !android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                        Text("Formato de email inválido")
                    }
                }
            )
            Spacer(modifier = Modifier.height(8.dp))

            OutlinedTextField(
                value = password,
                onValueChange = { password = it },
                label = { Text("Contraseña") },
                visualTransformation = PasswordVisualTransformation(),
                modifier = Modifier.fillMaxWidth(),
                isError = password.isNotBlank() && password.length < 6,
                supportingText = {
                    if (password.isNotBlank() && password.length < 6) {
                        Text("Mínimo 6 caracteres")
                    }
                }
            )
            Spacer(modifier = Modifier.height(8.dp))

            OutlinedTextField(
                value = nombre,
                onValueChange = { nombre = it },
                label = { Text("Nombre") },
                modifier = Modifier.fillMaxWidth(),
                isError = nombre.isNotBlank() && nombre.isEmpty(),
                supportingText = {
                    if (nombre.isNotBlank() && nombre.isEmpty()) {
                        Text("El nombre es obligatorio")
                    }
                }
            )
            Spacer(modifier = Modifier.height(8.dp))

            OutlinedTextField(
                value = apellido,
                onValueChange = { apellido = it },
                label = { Text("Apellido") },
                modifier = Modifier.fillMaxWidth(),
                isError = apellido.isNotBlank() && apellido.isEmpty(),
                supportingText = {
                    if (apellido.isNotBlank() && apellido.isEmpty()) {
                        Text("El apellido es obligatorio")
                    }
                }
            )

            Spacer(modifier = Modifier.height(16.dp))

            Button(
                onClick = {
                    coroutineScope.launch {
                        when {
                            email.isBlank() || password.isBlank() || nombre.isBlank() || apellido.isBlank() -> {
                                showToast("Por favor, completa todos los campos")
                            }
                            !android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches() -> {
                                showToast("Formato de email inválido")
                            }
                            password.length < 6 -> {
                                showToast("La contraseña debe tener al menos 6 caracteres")
                            }
                            else -> {
                                isLoading = true
                                val resultado = databaseHelper.registerUser(email, password, nombre, apellido)
                                isLoading = false
                                when (resultado) {
                                    RegistroResultado.EXITO -> {
                                        showToast("Registro exitoso")
                                        activity?.finish()
                                    }
                                    RegistroResultado.EMAIL_YA_REGISTRADO -> {
                                        showToast("El correo ya está registrado. Inicia sesión.")
                                    }
                                    RegistroResultado.ERROR -> {
                                        showToast("Error al registrar. Verifica tu conexión o intenta de nuevo.")
                                    }
                                }
                            }
                        }
                    }
                },
                modifier = Modifier.fillMaxWidth(),
                enabled = !isLoading
            ) {
                if (isLoading) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(20.dp),
                        color = MaterialTheme.colorScheme.onPrimary
                    )
                } else {
                    Text("Registrar")
                }
            }
        }
    }
}