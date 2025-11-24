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

class UsuarioViewModel(
    private val repository: UsuarioRepository = UsuarioRepository()
) : ViewModel() {

    // Aquí guardaremos al usuario una vez inicie sesión para usarlo en toda la app
    var usuarioActual: Usuario? = null
        private set

    // Estado de la Interfaz (Campos de texto, Errores, Carga)
    private val _uiState = MutableStateFlow(AuthUiState())
    val uiState: StateFlow<AuthUiState> = _uiState

    // ============================================================
    //  ACTUALIZADORES DE CAMPOS (Se llaman desde la UI al escribir)
    // ============================================================

    // Cuando el usuario escribe, actualizamos el estado y limpiamos el error de ese campo
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

    // ============================================================
    //  LÓGICA DE REGISTRO
    // ============================================================

    fun registrar(onSuccess: () -> Unit, onValidationFailed: (String) -> Unit) {
        // 1. Validamos los datos localmente antes de molestar al servidor
        if (!validarRegistro()) {
            // Si la validación falla, buscamos cuál fue el primer error para mostrarlo en el Toast
            val estado = _uiState.value
            val mensajeError = when {
                estado.errorNombre != null -> estado.errorNombre
                estado.errorAlias != null -> estado.errorAlias
                estado.errorCorreo != null -> estado.errorCorreo
                estado.errorClave != null -> estado.errorClave
                else -> "Por favor completa todos los campos correctamente."
            }
            // Enviamos el mensaje a la UI
            onValidationFailed(mensajeError ?: "Error desconocido")
            return
        }

        // 2. Si todo es válido, llamamos al Backend
        viewModelScope.launch {
            // Activamos el circulito de carga
            _uiState.update { it.copy(estaCargando = true, errorGeneral = null) }

            try {
                val s = _uiState.value

                // Llamada al repositorio (que conecta con tu Node.js)
                val nuevoUsuario = repository.registro(
                    nombre = s.nombre,
                    alias = s.alias,
                    correo = s.correo,
                    clave = s.clave
                )

                // Éxito: Guardamos el usuario y avisamos
                usuarioActual = nuevoUsuario
                println("✅ REGISTRO ÉXITOSO: ${nuevoUsuario.alias} (ID: ${nuevoUsuario.id})")

                _uiState.update { it.copy(estaCargando = false, registroExitoso = true) }
                onSuccess()

            } catch (e: Exception) {
                // Error: Mostramos mensaje y apagamos carga
                e.printStackTrace()
                val errorMsg = e.message ?: "Error de conexión"
                println("❌ ERROR REGISTRO: $errorMsg")

                _uiState.update { it.copy(estaCargando = false, errorGeneral = errorMsg) }
                onValidationFailed("Error del servidor: $errorMsg")
            }
        }
    }

    // ============================================================
    //  LÓGICA DE LOGIN
    // ============================================================

    fun login(onSuccess: () -> Unit) {
        viewModelScope.launch {
            _uiState.update { it.copy(estaCargando = true, errorGeneral = null) }

            try {
                val correo = _uiState.value.correo
                val clave = _uiState.value.clave

                val usuarioRecibido = repository.login(correo, clave)

                usuarioActual = usuarioRecibido
                println("✅ LOGIN EXITOSO: ${usuarioRecibido.alias}")

                _uiState.update { it.copy(estaCargando = false) }
                onSuccess()

            } catch (e: Exception) {
                val errorMsg = if (e.message?.contains("401") == true) "Credenciales incorrectas" else "Error de conexión"
                println("❌ ERROR LOGIN: $errorMsg")

                _uiState.update {
                    it.copy(estaCargando = false, errorGeneral = errorMsg)
                }
            }
        }
    }

    // ============================================================
    //  VALIDACIONES INTERNAS
    // ============================================================
    private fun validarRegistro(): Boolean {
        val s = _uiState.value
        var esValido = true

        // Nombre
        if (s.nombre.isBlank()) {
            _uiState.update { it.copy(errorNombre = "Falta ingresar tu nombre") }
            esValido = false
        }

        // Alias (Reemplaza a Edad)
        if (s.alias.isBlank()) {
            _uiState.update { it.copy(errorAlias = "Falta ingresar un alias") }
            esValido = false
        }

        // Correo (Vacío y Formato)
        if (s.correo.isBlank()) {
            _uiState.update { it.copy(errorCorreo = "Falta ingresar el correo") }
            esValido = false
        } else if (!android.util.Patterns.EMAIL_ADDRESS.matcher(s.correo).matches()) {
            _uiState.update { it.copy(errorCorreo = "El formato del correo es inválido") }
            esValido = false
        }

        // Contraseña (Longitud)
        if (s.clave.length < 6) {
            _uiState.update { it.copy(errorClave = "Mínimo 6 caracteres") }
            esValido = false
        }
        // Contraseña (Coincidencia)
        if (s.clave != s.confirmarClave) {
            _uiState.update { it.copy(errorClave = "Las contraseñas no coinciden") }
            esValido = false
        }

        return esValido
    }
}