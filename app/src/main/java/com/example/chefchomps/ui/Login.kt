package com.example.chefchomps.ui

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
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
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.chefchomps.R
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
        // Inicializar ThemeManager para cargar la preferencia del tema
        ThemeManager.initialize(this)

        setContent {
            ChefChompsAppTheme { // Usar el tema global
                Surface(modifier = Modifier.fillMaxSize()) {
                    LoginLayout { message ->
                        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
                    }
                }
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
    var showPassword by remember { mutableStateOf(false) }

    val databaseHelper = remember { DatabaseHelper() }
    val coroutineScope = rememberCoroutineScope()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Image(painter = painterResource(R.drawable.chomper), contentDescription = "")

        Text("INICIAR SESIÓN", style = MaterialTheme.typography.headlineMedium)

        Spacer(modifier = Modifier.height(32.dp))

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
            visualTransformation = if (showPassword) {
                VisualTransformation.None
            } else {
                PasswordVisualTransformation()
            },
            trailingIcon = {
                IconButton(onClick = { showPassword = !showPassword }) {
                    Icon(
                        painter = painterResource(
                            if (showPassword) R.drawable.ic_visibility_off else R.drawable.ic_visibility
                        ),
                        contentDescription = if (showPassword) "Ocultar contraseña" else "Mostrar contraseña"
                    )
                }
            },
            modifier = Modifier.fillMaxWidth()
        )


        Spacer(modifier = Modifier.height(16.dp))

        val context = LocalContext.current

        Button(
            onClick = {
                context.startActivity(Intent(context, RegistroActivity::class.java))
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Registrarte")
        }

        Button(
            onClick = {
                coroutineScope.launch {
                    val success = databaseHelper.loginUser(email, password)
                    if (success) {
                        showToast("Inicio de sesión exitoso")
                        context.startActivity(Intent(context, PaginaPrincipal::class.java))
                        (context as? ComponentActivity)?.finish()
                    } else {
                        showToast("Usuario o contraseña incorrectos")
                    }
                }
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Iniciar sesión")
        }

        Button(
            onClick = {
                val intent = Intent(context, PasswordResetActivity::class.java)
                context.startActivity(intent)
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Recuperar contraseña")
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
