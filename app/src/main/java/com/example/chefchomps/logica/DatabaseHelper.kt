package com.example.chefchomps.logica

import com.example.chefchomps.model.Usuario
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await

/**
 * Clase para gestionar el registro e inicio de sesión de usuarios en Firebase.
 */
class DatabaseHelper {
    internal val auth = FirebaseAuth.getInstance()
    internal val db = FirebaseFirestore.getInstance()

    /**
     * Registra un nuevo usuario en la base de datos.
     *
     * @param email Correo electrónico del usuario.
     * @param password Contraseña del usuario.
     * @param nombre Nombre del usuario.
     * @param apellidos Apellidos del usuario.
     * @return Resultado del registro.
     */
    suspend fun registerUser(email: String, password: String, nombre: String, apellidos: String): RegistroResultado {
        return try {
            // Verificar si el email ya está registrado
            val snapshot = db.collection("usuarios")
                .whereEqualTo("email", email)
                .get()
                .await()

            if (!snapshot.isEmpty) {
                return RegistroResultado.EMAIL_YA_REGISTRADO
            }

            // Crear usuario en Firebase Auth
            val authResult = auth.createUserWithEmailAndPassword(email, password).await()
            val userId = authResult.user?.uid ?: throw Exception("Error al crear usuario")

            // Guardar datos adicionales en Firestore
            val usuario = Usuario(email, nombre, apellidos, password)
            db.collection("usuarios").document(userId).set(usuario).await()

            RegistroResultado.EXITO
        } catch (e: Exception) {
            e.printStackTrace()
            RegistroResultado.ERROR
        }
    }

    enum class RegistroResultado {
        EXITO,
        EMAIL_YA_REGISTRADO,
        ERROR
    }

    /**
     * Inicia sesión verificando las credenciales del usuario.
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
            e.printStackTrace()
            false
        }
    }

    /**
     * Recupera la contraseña del usuario.
     *
     * @param email Correo electrónico del usuario.
     * @return La contraseña del usuario si existe, null en caso contrario.
     */
    suspend fun recoverPassword(email: String): String? {
        return try {
            val querySnapshot = db.collection("usuarios")
                .whereEqualTo("email", email)
                .get()
                .await()

            val usuario = querySnapshot.documents.firstOrNull()?.toObject(Usuario::class.java)
            usuario?.password
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    /**
     * Obtiene el usuario actual si está autenticado.
     *
     * @return El usuario actual o null si no hay usuario autenticado.
     */
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

    /**
     * Obtiene el perfil completo del usuario actual desde Firestore.
     *
     * @return El perfil completo del usuario o null si no está autenticado.
     */
    suspend fun getUserProfile(): Usuario? {
        val userId = auth.currentUser?.uid ?: return null
        return try {
            val doc = db.collection("usuarios").document(userId).get().await()
            doc.toObject(Usuario::class.java)
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    /**
     * Cierra la sesión del usuario actual.
     */
    fun signOut() {
        auth.signOut()
    }
}