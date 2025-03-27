package com.example.chefchomps.logica

import com.example.chefchomps.persistencia.DatabaseConnection
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await
import java.sql.ResultSet
import java.sql.PreparedStatement
import java.sql.SQLException

data class Usuario(
    val email: String = "",
    val nombre: String = "",
    val apellidos: String = ""
)

class DatabaseHelper {

    private val db = FirebaseFirestore.getInstance()
    private val auth = FirebaseAuth.getInstance()

    suspend fun registerUser(email: String, password: String, nombre: String, apellidos: String): Boolean {
        return try {
            val authResult = auth.createUserWithEmailAndPassword(email, password).await()
            val uid = authResult.user?.uid ?: return false

            val usuario = Usuario(email, nombre, apellidos)
            db.collection("usuarios").document(uid).set(usuario).await()

            true
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }

    suspend fun loginUser(email: String, password: String): Boolean {
        return try {
            auth.signInWithEmailAndPassword(email, password).await()
            true
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }
}