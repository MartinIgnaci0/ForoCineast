package com.example.forocineast.repository

import com.example.forocineast.data.remote.RetrofitInstance
import com.example.forocineast.model.Usuario

class UsuarioRepository {

    private val api = RetrofitInstance.api

    /**
     * Envía las credenciales para iniciar sesión.
     * Retorna el objeto Usuario completo (con ID y Alias) si es exitoso.
     */
    suspend fun login(correo: String, clave: String): Usuario {
        // Creamos un objeto parcial solo con lo necesario
        val request = Usuario(
            nombreCompleto = "",
            alias = "",
            correo = correo,
            clave = clave
        )
        return api.login(request)
    }

    /**
     * Registra un nuevo usuario con los nuevos campos de Cineast.
     */
    suspend fun registro(nombre: String, alias: String, correo: String, clave: String): Usuario {
        val nuevoUsuario = Usuario(
            nombreCompleto = nombre,
            alias = alias,
            correo = correo,
            clave = clave
        )
        return api.registro(nuevoUsuario)
    }
}