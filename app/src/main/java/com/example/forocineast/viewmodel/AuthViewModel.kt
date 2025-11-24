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

class AuthViewModel(
    private val repository: UsuarioRepository = UsuarioRepository()
) : ViewModel() {

    // Estado del formulario (textos, errores, carga)
    private val _uiState = MutableStateFlow(AuthUiState())
    val uiState: StateFlow<AuthUiState> = _uiState

    // Guardamos el usuario logueado en memoria
    var usuarioActual: Usuario? = null
        private set

    // --- Actualizadores de campos (se llaman desde la UI) ---
    fun onNombreChange(texto: String) = _uiState.update { it.copy(nombre = texto, errorNombre = null) }
    fun onAliasChange(texto: String) = _uiState.update { it.copy(alias = texto, errorAlias = null) }
    fun onCorreoChange(texto: String) = _uiState.update { it.copy(correo = texto, errorCorreo = null) }
    fun onClaveChange(texto: String) = _uiState.update { it.copy(clave = texto, errorClave = null) }
    fun onConfirmarClaveChange(texto: String) = _uiState.update { it.copy(confirmarClave = texto) }

    // --- Lógica de Login ---
    fun login(onSuccess: () -> Unit) {
        viewModelScope.launch {
            _uiState.update { it.copy(estaCargando = true, errorGeneral = null) }
            try {
                val correo = _uiState.value.correo
                val clave = _uiState.value.clave

                // Llamada al repositorio
                val usuarioRecibido = repository.login(correo, clave)

                usuarioActual = usuarioRecibido
                _uiState.update { it.copy(estaCargando = false) }
                onSuccess()

            } catch (e: Exception) {
                _uiState.update {
                    it.copy(
                        estaCargando = false,
                        errorGeneral = "Credenciales incorrectas o error de conexión"
                    )
                }
            }
        }
    }

    // --- Lógica de Registro ---
    fun registrar(onSuccess: () -> Unit) {
        if (!validarRegistro()) return

        viewModelScope.launch {
            _uiState.update { it.copy(estaCargando = true, errorGeneral = null) }
            try {
                val s = _uiState.value
                val nuevoUsuario = repository.registro(s.nombre, s.alias, s.correo, s.clave)

                usuarioActual = nuevoUsuario
                _uiState.update { it.copy(estaCargando = false, registroExitoso = true) }
                onSuccess()

            } catch (e: Exception) {
                _uiState.update {
                    it.copy(estaCargando = false, errorGeneral = "Error al registrar. Revisa tu conexión.")
                }
            }
        }
    }

    // Validaciones locales antes de enviar
    private fun validarRegistro(): Boolean {
        val s = _uiState.value
        var esValido = true

        if (s.nombre.isBlank()) {
            _uiState.update { it.copy(errorNombre = "El nombre es obligatorio") }
            esValido = false
        }
        if (s.alias.isBlank()) {
            _uiState.update { it.copy(errorAlias = "El alias es obligatorio") }
            esValido = false
        }
        if (s.correo.isBlank()) {
            _uiState.update { it.copy(errorCorreo = "El correo es obligatorio") }
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