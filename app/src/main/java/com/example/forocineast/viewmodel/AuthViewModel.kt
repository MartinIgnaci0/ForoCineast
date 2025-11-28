package com.example.forocineast.viewmodel

import android.app.Application
import android.content.Context
import android.net.Uri
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.forocineast.model.AuthUiState
import com.example.forocineast.model.Usuario
import com.example.forocineast.repository.UsuarioRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

// Refactorizado para aceptar repositorio en el constructor (Testable)
class AuthViewModel(
    application: Application,
    private val repository: UsuarioRepository
) : AndroidViewModel(application) {

    // Constructor secundario para uso normal en la App (Inyección de dependencias simple)
    constructor(application: Application) : this(application, UsuarioRepository())

    private val context = application.applicationContext
    private val prefs = context.getSharedPreferences("app_prefs", Context.MODE_PRIVATE)

    private val _uiState = MutableStateFlow(AuthUiState())
    val uiState: StateFlow<AuthUiState> = _uiState

    var usuarioActual: Usuario? = null
        private set

    fun login(onSuccess: () -> Unit) {
        viewModelScope.launch {
            _uiState.update { it.copy(estaCargando = true, errorGeneral = null) }
            try {
                val correo = _uiState.value.correo
                val clave = _uiState.value.clave

                val usuarioRecibido = repository.login(correo, clave)

                val fotoGuardada = prefs.getString("foto_perfil_${usuarioRecibido.id}", null)
                val usuarioConFoto = if (fotoGuardada != null) {
                    usuarioRecibido.copy(fotoPerfilUrl = fotoGuardada)
                } else {
                    usuarioRecibido
                }

                usuarioActual = usuarioConFoto
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

    fun actualizarFotoPerfil(uri: Uri) {
        val idUsuario = usuarioActual?.id ?: return
        val rutaFoto = uri.toString()
        usuarioActual = usuarioActual?.copy(fotoPerfilUrl = rutaFoto)
        prefs.edit().putString("foto_perfil_$idUsuario", rutaFoto).apply()
    }

    fun onNombreChange(texto: String) = _uiState.update { it.copy(nombre = texto, errorNombre = null) }
    fun onAliasChange(texto: String) = _uiState.update { it.copy(alias = texto, errorAlias = null) }
    fun onCorreoChange(texto: String) = _uiState.update { it.copy(correo = texto, errorCorreo = null) }
    fun onClaveChange(texto: String) = _uiState.update { it.copy(clave = texto, errorClave = null) }
    fun onConfirmarClaveChange(texto: String) = _uiState.update { it.copy(confirmarClave = texto) }

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

    // Hacemos pública la función para testearla
    fun validarRegistro(): Boolean {
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
        
        // Validación de correo usando Regex estándar en lugar de Android Patterns para facilitar tests
        val emailRegex = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$".toRegex()
        
        if (s.correo.isBlank()) {
            _uiState.update { it.copy(errorCorreo = "El correo es obligatorio") }
            esValido = false
        } else if (!emailRegex.matches(s.correo)) {
            _uiState.update { it.copy(errorCorreo = "Formato de correo inválido") }
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