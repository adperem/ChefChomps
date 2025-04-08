package com.example.chefchomps.logica

import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await

/**
 * Representa un usuario con sus datos básicos.
 *
 * @param email Correo electrónico del usuario.
 * @param nombre Nombre del usuario.
 * @param apellidos Apellidos del usuario.
 * @param password Contraseña del usuario.
 */
data class Usuario(
    val email: String = "",
    val nombre: String = "",
    val apellidos: String = "",
    val password: String = ""
)

/**
 * Clase para gestionar el registro e inicio de sesión de usuarios en Firebase.
 */
class DatabaseHelper {

    private val db = FirebaseFirestore.getInstance()

    /**
     * Registra un nuevo usuario en la base de datos.
     *
     * @param email Correo electrónico del usuario.
     * @param password Contraseña del usuario.
     * @param nombre Nombre del usuario.
     * @param apellidos Apellidos del usuario.
     * @return 'true' si el registro fue exitoso, 'false' en caso de error.
     */
    suspend fun registerUser(email: String, password: String, nombre: String, apellidos: String): Boolean {
        return try {
            val usuario = Usuario(email, nombre, apellidos, password)
            db.collection("usuarios").document(email).set(usuario).await()
            true
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }

    /**
     * Inicia sesion verificando las credenciales del usuario.
     *
     * @param email Correo electrónico del usuario.
     * @param password Contraseña del usuario.
     * @return 'true' si las credenciales son correctas, 'false' si son incorrectas o hay un error.
     */
    suspend fun loginUser(email: String, password: String): Boolean {
        return try {
            val documentSnapshot = db.collection("usuarios").document(email).get().await()

            if (documentSnapshot.exists()) {
                val usuario = documentSnapshot.toObject(Usuario::class.java)
                if (usuario?.password == password) {
                    return true
                }
            }

            false
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }

    suspend fun recoverPassword(email: String): String? {
        return try {
            val documentSnapshot = db.collection("usuarios").document(email).get().await()
            if (documentSnapshot.exists()) {
                val usuario = documentSnapshot.toObject(Usuario::class.java)
                usuario?.password
            } else {
                null
            }
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }
}