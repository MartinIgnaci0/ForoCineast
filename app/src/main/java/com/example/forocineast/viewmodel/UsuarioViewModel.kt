package com.example.forocineast.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.forocineast.model.AuthUiState
import com.example.forocineast.model.Usuario
import com.example.forocineast.repository.UsuarioRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

// Refactorizado para aceptar repositorio (Testable)
class UsuarioViewModel(
    private val repository: UsuarioRepository = UsuarioRepository()
) : ViewModel() {

    var usuarioActual: Usuario? = null
        private set

    private val _uiState = MutableStateFlow(AuthUiState())
    val uiState: StateFlow<AuthUiState> = _uiState

    fun onNombreChange(texto: String) {
        _uiState.update { it.copy(nombre = texto, errorNombre = null) }
    }

    fun onAliasChange(texto: String) {
        _uiState.update { it.copy(alias = texto, errorAlias = null) }
    }

    fun onCorreoChange(texto: String) {
        _uiState.update { it.copy(correo = texto, errorCorreo = null) }
    }

    fun onClaveChange(texto: String) {
        _uiState.update { it.copy(clave = texto, errorClave = null) }
    }

    fun onConfirmarClaveChange(texto: String) {
        _uiState.update { it.copy(confirmarClave = texto, errorClave = null) }
    }

    fun registrar(onSuccess: () -> Unit, onValidationFailed: (String) -> Unit) {
        if (!validarRegistro()) {
            val estado = _uiState.value
            val mensajeError = when {
                estado.errorNombre != null -> estado.errorNombre
                estado.errorAlias != null -> estado.errorAlias
                estado.errorCorreo != null -> estado.errorCorreo
                estado.errorClave != null -> estado.errorClave
                else -> "Por favor completa todos los campos correctamente."
            }
            onValidationFailed(mensajeError ?: "Error desconocido")
            return
        }

        viewModelScope.launch {
            _uiState.update { it.copy(estaCargando = true, errorGeneral = null) }

            try {
                val s = _uiState.value
                val nuevoUsuario = repository.registro(
                    nombre = s.nombre,
                    alias = s.alias,
                    correo = s.correo,
                    clave = s.clave
                )

                usuarioActual = nuevoUsuario
                _uiState.update { it.copy(estaCargando = false, registroExitoso = true) }
                onSuccess()

            } catch (e: Exception) {
                e.printStackTrace()
                val errorMsg = e.message ?: "Error de conexión"
                _uiState.update { it.copy(estaCargando = false, errorGeneral = errorMsg) }
                onValidationFailed("Error del servidor: $errorMsg")
            }
        }
    }

    fun login(onSuccess: () -> Unit) {
        viewModelScope.launch {
            _uiState.update { it.copy(estaCargando = true, errorGeneral = null) }

            try {
                val correo = _uiState.value.correo
                val clave = _uiState.value.clave

                val usuarioRecibido = repository.login(correo, clave)

                usuarioActual = usuarioRecibido
                _uiState.update { it.copy(estaCargando = false) }
                onSuccess()

            } catch (e: Exception) {
                val errorMsg = if (e.message?.contains("401") == true) "Credenciales incorrectas" else "Error de conexión"
                _uiState.update {
                    it.copy(estaCargando = false, errorGeneral = errorMsg)
                }
            }
        }
    }

    // Hacemos pública la función para testearla y usamos Regex
    fun validarRegistro(): Boolean {
        val s = _uiState.value
        var esValido = true

        if (s.nombre.isBlank()) {
            _uiState.update { it.copy(errorNombre = "Falta ingresar tu nombre") }
            esValido = false
        }

        if (s.alias.isBlank()) {
            _uiState.update { it.copy(errorAlias = "Falta ingresar un alias") }
            esValido = false
        }

        // Regex estándar en lugar de Patterns de Android
        val emailRegex = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$".toRegex()

        if (s.correo.isBlank()) {
            _uiState.update { it.copy(errorCorreo = "Falta ingresar el correo") }
            esValido = false
        } else if (!emailRegex.matches(s.correo)) {
            _uiState.update { it.copy(errorCorreo = "El formato del correo es inválido") }
            esValido = false
        }

        if (s.clave.length < 6) {
            _uiState.update { it.copy(errorClave = "Mínimo 6 caracteres") }
            esValido = false
        }
        if (s.clave != s.confirmarClave) {
            _uiState.update { it.copy(errorClave = "Las contraseñas no coinciden") }
            esValido = false
        }

        return esValido
    }
}