package com.example.chefchomps.logica

import com.example.chefchomps.model.Usuario
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await

/**
 * Clase para gestionar el registro e inicio de sesión de usuarios en Firebase.
 */
class DatabaseHelper public constructor() {

    private val auth = FirebaseAuth.getInstance()
    internal val firestore = FirebaseFirestore.getInstance()

    companion object {
        @Volatile
        private var INSTANCE: DatabaseHelper? = null

        fun getInstance(): DatabaseHelper {
            return INSTANCE ?: synchronized(this) {
                INSTANCE ?: DatabaseHelper().also { INSTANCE = it }
            }
        }
    }

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
            val result = auth.createUserWithEmailAndPassword(email, password).await()
            val user = Usuario(email, nombre, apellidos, password)
            firestore.collection("usuarios").document(result.user?.uid ?: "").set(user).await()
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
            auth.signInWithEmailAndPassword(email, password).await()
            true
        } catch (e: Exception) {
            false
        }
    }

    fun getCurrentUser(): Usuario? {
        val firebaseUser = auth.currentUser
        return if (firebaseUser != null) {
            Usuario(
                email = firebaseUser.email ?: "",
                nombre = "",  // Estos campos se cargarán desde Firestore
                apellidos = "",
                password = ""
            )
        } else null
    }

    suspend fun getUserProfile(): Usuario? {
        val userId = auth.currentUser?.uid ?: return null
        return try {
            val doc = firestore.collection("usuarios").document(userId).get().await()
            doc.toObject(Usuario::class.java)
        } catch (e: Exception) {
            null
        }
    }
}