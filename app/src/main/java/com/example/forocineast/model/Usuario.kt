package com.example.forocineast.model

/**
 * Modelo de Usuario para la lógica de negocio y autenticación.
 * Incluye 'esAdmin' para privilegios de moderación.
 */
data class Usuario(
    val id: Int = 0,
    val nombreCompleto: String,
    val alias: String,
    val correo: String,
    val clave: String,
    val fotoPerfilUrl: String? = null,
    val esAdmin: Int = 0 // 0 = Usuario Normal, 1 = Administrador
) {
    fun isAdmin(): Boolean {
        return esAdmin == 1
    }
}