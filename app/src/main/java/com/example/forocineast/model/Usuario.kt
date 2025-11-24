package com.example.forocineast.model

/**
 * Modelo de Usuario para la lógica de negocio y autenticación.
 * Incluye 'alias' para interacciones sociales en el foro.
 */
data class Usuario(
    val id: Int = 0,
    val nombreCompleto: String,
    val alias: String, // Nickname (ej. "Cinefilo10")
    val correo: String,
    val clave: String,
    val fotoPerfilUrl: String? = null
)