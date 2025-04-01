package com.example.chefchomps.logica

import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await

data class Usuario(
    val email: String = "",
    val nombre: String = "",
    val apellidos: String = "",
    val password: String = ""
)

class DatabaseHelper {

    private val db = FirebaseFirestore.getInstance()


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
}