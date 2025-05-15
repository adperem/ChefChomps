package com.example.chefchomps.ui.profile

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.chefchomps.logica.DatabaseHelper
import com.example.chefchomps.model.Usuario
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

/**
 * ViewModel que gestiona la lógica de negocio para la pantalla de perfil de usuario.
 */
class ProfileViewModel : ViewModel() {
    private val databaseHelper = DatabaseHelper()
    
    private val _uiState = MutableStateFlow<ProfileUiState>(ProfileUiState.Loading)
    val uiState: StateFlow<ProfileUiState> = _uiState

    init {
        // Observar cambios en el estado de autenticación
        databaseHelper.auth.addAuthStateListener { firebaseAuth ->
            val user = firebaseAuth.currentUser
            if (user != null) {
                // Usuario está autenticado, cargar sus datos
                loadUserProfile()
            } else {
                // Usuario no está autenticado
                _uiState.value = ProfileUiState.NotAuthenticated
            }
        }
    }

    /**
     * Carga el perfil del usuario autenticado desde Firestore de manera asíncrona.
     */
    private fun loadUserProfile() {
        viewModelScope.launch {
            try {
                _uiState.value = ProfileUiState.Loading
                val usuario = databaseHelper.getUserProfile() ?: throw Exception("Usuario no encontrado")
                _uiState.value = ProfileUiState.Success(usuario)
            } catch (e: Exception) {
                _uiState.value = ProfileUiState.Error(e.message ?: "Error desconocido")
            }
        }
    }

    /**
     * Actualiza el perfil del usuario en Firestore y, si es necesario, el email en Firebase Authentication.
     *
     * @param usuario Usuario con los datos actualizados del perfil.
     */
    fun updateUserProfile(usuario: Usuario) {
        viewModelScope.launch {
            try {
                _uiState.value = ProfileUiState.Loading
                val userId = databaseHelper.auth.currentUser?.uid ?: throw Exception("Usuario no autenticado")
                
                // Actualizar en Firestore
                databaseHelper.db.collection("usuarios").document(userId)
                    .set(usuario)
                    .await()
                
                // Actualizar email en Authentication si ha cambiado
                if (usuario.email != databaseHelper.auth.currentUser?.email) {
                    databaseHelper.auth.currentUser?.updateEmail(usuario.email)?.await()
                }
                
                _uiState.value = ProfileUiState.Success(usuario)
            } catch (e: Exception) {
                _uiState.value = ProfileUiState.Error(e.message ?: "Error al actualizar el perfil")
            }
        }
    }

    /**
     * Cierra la sesión del usuario actual utilizando DatabaseHelper.
     */
    fun signOut() {
        databaseHelper.signOut()
    }

    /**
     * Elimina la cuenta del usuario actual, incluyendo sus datos en Firestore y su autenticación
     * en Firebase Authentication.
     */
    fun deleteUserAccount() {
        viewModelScope.launch {
            try {
                _uiState.value = ProfileUiState.Loading
                // Eliminar la cuenta en Firebase
                val success = databaseHelper.borrarCuentaActual()
                if (success) {
                    // Eliminar también la autenticación
                    databaseHelper.auth.currentUser?.delete()?.await()
                    // Cerrar sesión
                    databaseHelper.signOut()
                    _uiState.value = ProfileUiState.NotAuthenticated
                } else {
                    _uiState.value = ProfileUiState.Error("No se pudo eliminar la cuenta")
                }
            } catch (e: Exception) {
                _uiState.value = ProfileUiState.Error("Error al eliminar la cuenta: ${e.message}")
                // Intentar cerrar sesión de todos modos en caso de error
                databaseHelper.signOut()
            }
        }
    }
}

/**
 * Clase sellada que representa los posibles estados de la interfaz de usuario
 * para la pantalla de perfil.
 */
sealed class ProfileUiState {
    object Loading : ProfileUiState()
    object NotAuthenticated : ProfileUiState()
    data class Success(val usuario: Usuario) : ProfileUiState()
    data class Error(val message: String) : ProfileUiState()
} 