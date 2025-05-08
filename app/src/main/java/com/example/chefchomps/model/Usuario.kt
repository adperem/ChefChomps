package com.example.chefchomps.model

/**
 * Representa un usuario con sus datos básicos.
 *
 * @param email Correo electrónico del usuario.
 * @param nombre Nombre del usuario.
 * @param apellidos Apellidos del usuario.
 * @param password Contraseña del usuario.
 * @param username Nombre de usuario para identificación.
 */
data class Usuario(
    val email: String = "",
    val nombre: String = "",
    val apellidos: String = "",
    val password: String = "",
    val username: String = ""
)