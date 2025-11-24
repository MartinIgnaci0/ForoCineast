package com.example.forocineast.model

import android.net.Uri

/**
 * Estado de la interfaz para las pantallas de Login y Registro.
 * Centraliza los campos de texto, errores y estados de carga.
 */
data class AuthUiState(
    // Campos del formulario
    val nombre: String = "",
    val alias: String = "",
    val correo: String = "",
    val clave: String = "",
    val confirmarClave: String = "",
    val fotoUri: Uri? = null,

    // Mensajes de error por campo
    val errorNombre: String? = null,
    val errorAlias: String? = null,
    val errorCorreo: String? = null,
    val errorClave: String? = null,
    val errorGeneral: String? = null,

    // Estados de proceso
    val estaCargando: Boolean = false,
    val registroExitoso: Boolean = false
) {
    // Helper para saber si el botón de registro debe habilitarse
    val esFormularioValido: Boolean
        get() = nombre.isNotBlank() &&
                alias.isNotBlank() &&
                correo.isNotBlank() &&
                clave.length >= 6 &&
                clave == confirmarClave
}