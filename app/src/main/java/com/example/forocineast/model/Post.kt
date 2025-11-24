package com.example.forocineast.model

data class Post(
    val id: Int? = null,
    val titulo: String,      // Ej: "Final explicado de Interestelar"
    val cuerpo: String,      // El análisis del usuario
    val peliculaRef: String, // Título de la película asociada
    val valoracion: Int,     // Calificación personal (1 a 5)
    val tieneSpoilers: Boolean = false, // Bandera para ocultar texto
    val fechaCreacion: String,
    val autorId: Int,
    val autorAlias: String? = null
)