package com.example.chefchomps.logica

import com.google.firebase.auth.FirebaseAuth
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

    val db = FirebaseFirestore.getInstance()
    internal val auth = FirebaseAuth.getInstance()

    /**
     * Registra un nuevo usuario en la base de datos.
     *
     * @param email Correo electrónico del usuario.
     * @param password Contraseña del usuario.
     * @param nombre Nombre del usuario.
     * @param apellidos Apellidos del usuario.
     * @return 'true' si el registro fue exitoso, 'false' en caso de error.
     */
    suspend fun registerUser(email: String, password: String, nombre: String, apellidos: String): RegistroResultado {
        return try {
            val snapshot = db.collection("usuarios")
                .whereEqualTo("email", email)
                .get()
                .await()

            if (!snapshot.isEmpty) {
                return RegistroResultado.EMAIL_YA_REGISTRADO
            }

            val userId = db.runTransaction { transaction ->
                val metadataRef = db.collection("metadata").document("contadorUsuarios")
                val metadataSnapshot = transaction.get(metadataRef)

                val ultimoId = metadataSnapshot.getLong("ultimoId") ?: 0
                val nuevoId = ultimoId + 1

                transaction.update(metadataRef, "ultimoId", nuevoId)

                "userId$nuevoId"
            }.await()

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
     * Inicia sesion verificando las credenciales del usuario.
     *
     * @param email Correo electrónico del usuario.
     * @param password Contraseña del usuario.
     * @return 'true' si las credenciales son correctas, 'false' si son incorrectas o hay un error.
     */
    suspend fun loginUser(email: String, password: String): Boolean {
        return try {
            val querySnapshot = db.collection("usuarios")
                .whereEqualTo("email", email)
                .get()
                .await()

            val usuario = querySnapshot.documents.firstOrNull()?.toObject(Usuario::class.java)
            usuario?.password == password
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }

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
    fun getCurrentUser(): com.example.chefchomps.model.Usuario? {
        val firebaseUser = auth.currentUser
        return if (firebaseUser != null) {
            com.example.chefchomps.model.Usuario(
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
    suspend fun getUserProfile(): com.example.chefchomps.model.Usuario? {
        val userId = auth.currentUser?.uid ?: return null
        return try {
            val doc = db.collection("usuarios").document(userId).get().await()
            doc.toObject(com.example.chefchomps.model.Usuario::class.java)
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