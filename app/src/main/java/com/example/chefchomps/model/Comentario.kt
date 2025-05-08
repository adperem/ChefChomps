package com.example.chefchomps.model

import com.google.firebase.Timestamp

/**
 * Clase que representa un comentario y valoración para una receta.
 */
data class Comentario(
    val id: String = "",
    val usuarioId: String = "",
    val nombreUsuario: String = "",
    val recetaId: Int = 0,
    val texto: String = "",
    val valoracion: Int = 0,
    val fecha: Timestamp = Timestamp.now()
) {
    constructor() : this("", "", "", 0, "", 0, Timestamp.now())
} 