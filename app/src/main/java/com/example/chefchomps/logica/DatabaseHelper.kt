package com.example.chefchomps.logica

import android.net.Uri
import android.util.Log
import com.example.chefchomps.model.Ingredient
import com.example.chefchomps.model.Recipe
import com.example.chefchomps.model.Usuario
import com.google.android.gms.tasks.Task
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthUserCollisionException
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.storage
import kotlinx.coroutines.tasks.await
import java.util.UUID

///**
// * Representa un usuario con sus datos básicos.
// *
// * @param email Correo electrónico del usuario.
// * @param nombre Nombre del usuario.
// * @param apellidos Apellidos del usuario.
// * @param password Contraseña del usuario.
// */
//data class Usuario(
//    val email: String = "",
//    val nombre: String = "",
//    val apellidos: String = ""
//)

/**
 * Clase para gestionar el registro e inicio de sesión de usuarios en Firebase.
 */
class DatabaseHelper {

    val db = FirebaseFirestore.getInstance();
    internal val auth = FirebaseAuth.getInstance()

    companion object {
        private const val RESET_CODE_VALIDITY_MINUTES = 15
    }

    /**
     * Registra un nuevo usuario en la base de datos.
     *
     * @param email Correo electrónico del usuario.
     * @param password Contraseña del usuario.
     * @param nombre Nombre del usuario.
     * @param apellidos Apellidos del usuario.
     * @param username Nombre de usuario (opcional, se genera automáticamente si no se proporciona).
     * @return 'true' si el registro fue exitoso, 'false' en caso de error.
     */
    suspend fun registerUser(email: String, password: String, nombre: String, apellidos: String, username: String = ""): RegistroResultado {
        return try {
            val authResult = auth.createUserWithEmailAndPassword(email, password).await()
            val userId = authResult.user?.uid ?: return RegistroResultado.ERROR

            // Generar nombre de usuario a partir del email si no se proporciona
            val finalUsername = if (username.isBlank()) {
                email.substringBefore("@").replace(".", "_")
            } else {
                username
            }

            val usuario = Usuario(email, nombre, apellidos, password, finalUsername)
            db.collection("usuarios").document(userId).set(usuario).await()

            RegistroResultado.EXITO
        } catch (e: FirebaseAuthUserCollisionException) {
            Log.e("DatabaseHelper", "Correo ya registrado: ${e.message}")
            RegistroResultado.EMAIL_YA_REGISTRADO
        } catch (e: Exception) {
            Log.e("DatabaseHelper", "Error en registro: ${e.message}")
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
            val authResult = auth.signInWithEmailAndPassword(email, password).await()
            authResult.user != null
        } catch (e: Exception) {
            Log.e("DatabaseHelper", "Error en login: ${e.message}")
            e.printStackTrace()
            false
        }
    }

    suspend fun generateAndSendResetCode(email: String): Boolean {
        return try {
            val user = db.collection("usuarios")
                .whereEqualTo("email", email)
                .get()
                .await()
                .documents
                .firstOrNull()

            if (user == null) return false

            val resetCode = (100000..999999).random().toString()

            db.collection("passwordResetCodes").document(email).set(
                mapOf(
                    "code" to resetCode,
                    "timestamp" to System.currentTimeMillis(),
                    "used" to false
                )
            ).await()

            sendResetCodeByEmail(email, resetCode)

            true
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }

    private fun sendResetCodeByEmail(email: String, code: String) {
        Log.d("ResetPassword", "Código para $email: $code")
    }

    suspend fun verifyResetCode(email: String, code: String): Boolean {
        return try {
            val snapshot = db.collection("passwordResetCodes")
                .document(email)
                .get()
                .await()

            if (!snapshot.exists()) return false

            val storedCode = snapshot.getString("code")
            val timestamp = snapshot.getLong("timestamp") ?: 0
            val isUsed = snapshot.getBoolean("used") ?: true

            storedCode == code &&
                    !isUsed &&
                    (System.currentTimeMillis() - timestamp) < RESET_CODE_VALIDITY_MINUTES * 60 * 1000
        } catch (e: Exception) {
            false
        }
    }

    suspend fun updatePasswordWithResetCode(email: String, code: String, newPassword: String): Boolean {
        return try {
            if (!verifyResetCode(email, code)) return false

            val query = db.collection("usuarios")
                .whereEqualTo("email", email)
                .get()
                .await()

            if (query.isEmpty) return false

            db.collection("passwordResetCodes")
                .document(email)
                .update("used", true)
                .await()

            val docRef = query.documents[0].reference
            docRef.update("password", newPassword).await()

            true
        } catch (e: Exception) {
            false
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
            Log.e("DatabaseHelper", "Error al obtener perfil: ${e.message}")
            null
        }
    }

    /**
     * Cierra la sesión del usuario actual.
     */
    fun signOut() {
        auth.signOut()
    }

    /**
     * Sube una nueva receta a la base de datos.
     *
     * @param titulo Título de la receta
     * @param imagenUri URI de la imagen de la receta (opcional)
     * @param ingredientes Lista de ingredientes de la receta
     * @param pasos Pasos para preparar la receta
     * @param tiempoPreparacion Tiempo de preparación en minutos
     * @param descripcion Descripción de la receta
     * @param porciones Número de porciones
     * @param esVegetariana Indica si la receta es vegetariana
     * @param esVegana Indica si la receta es vegana
     * @param tipoPlato Tipo de plato
     * @param glutenFree Indica si la receta es libre de gluten
     * @return La receta creada si tiene éxito, null en caso de error
     */
    suspend fun subirReceta(
        titulo: String,
        imagenUri: Uri?,
        ingredientes: List<Ingredient>,
        pasos: List<String>,
        tiempoPreparacion: Int,
        descripcion: String,
        porciones: Int,
        esVegetariana: Boolean,
        esVegana: Boolean,
        tipoPlato: String,
        glutenFree: Boolean
    ): Recipe? {
        return try {
            val userId = auth.currentUser?.uid

            val imagenUrl = if (imagenUri != null) {
                val storageRef = Firebase.storage.reference
                val imagenRef = storageRef.child("recetas/${UUID.randomUUID()}")
                val uploadTask = imagenRef.putFile(imagenUri).await()
                imagenRef.downloadUrl.await().toString()
            } else {
                null
            }

            val receta = Recipe(
                id = null, // Firebase generará el ID automáticamente
                title = titulo,
                image = imagenUrl,
                servings = porciones,
                readyInMinutes = tiempoPreparacion,
                instructions = pasos.joinToString("\n"),
                extendedIngredients = ingredientes,
                vegetarian = esVegetariana,
                vegan = esVegana,
                dishTypes = listOf(tipoPlato),
                summary = descripcion,
                userId = userId,
                glutenFree = glutenFree
            )

            // Crear un documento con ID generado automáticamente
            val docRef = db.collection("recetas").document()
            
            // Asignar el ID del documento a la receta
            val recetaConId = receta.copy(id = docRef.id.hashCode())
            
            // Guardar la receta
            docRef.set(recetaConId).await()

            recetaConId
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    /**
     * Busca recetas creadas por un usuario específico
     * 
     * @param nombreUsuario Nombre del usuario cuyas recetas se quieren buscar
     * @return Lista de recetas del usuario o null si ocurre un error
     */
    suspend fun buscarRecetasPorUsuario(nombreUsuario: String): List<Recipe>? {
        return try {
            // Aquí implementaríamos la lógica real para buscar en Firebase
            // Por ahora, retornamos una lista vacía como ejemplo
            emptyList()
        } catch (e: Exception) {
            null
        }
    }

    /**
     * Borrar de la base de datos el usuario actual
     * @return True si te promete borrar el usuario actual y false en el resto de casos
     */
    suspend fun borrarCuentaActual() :Boolean{
        if (auth.currentUser == null) {
                return false;
            }
        db.collection("usuarios").document(auth.currentUser!!.uid).delete();
        return true;
    }

}