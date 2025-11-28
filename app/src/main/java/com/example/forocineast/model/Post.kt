package com.example.forocineast.model

data class Post(
    val id: Int? = null,
    val titulo: String,
    val cuerpo: String,
    val peliculaRef: String,
    val valoracion: Int,
    val tieneSpoilers: Int = 0, // <--- CAMBIADO A INT (0 = No, 1 = Sí) para compatibilidad con MySQL
    val fechaCreacion: String,
    val autorId: Int,
    val autorAlias: String? = null
) {
    // Helper para usarlo como booleano en la UI
    fun esSpoiler(): Boolean {
        return tieneSpoilers == 1
    }
}