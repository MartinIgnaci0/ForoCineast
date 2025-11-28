package com.example.forocineast.viewmodel

import android.app.Application
import android.content.Context
import android.net.Uri
import android.util.Patterns
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.forocineast.model.AuthUiState
import com.example.forocineast.model.Usuario
import com.example.forocineast.repository.UsuarioRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

// Cambiamos a AndroidViewModel para tener acceso al Contexto (necesario para SharedPreferences)
class AuthViewModel(
    application: Application
) : AndroidViewModel(application) {

    private val repository = UsuarioRepository()
    private val context = application.applicationContext
    
    // Preferencias para guardar datos locales simples
    private val prefs = context.getSharedPreferences("app_prefs", Context.MODE_PRIVATE)

    // Estado del formulario
    private val _uiState = MutableStateFlow(AuthUiState())
    val uiState: StateFlow<AuthUiState> = _uiState

    // Usuario actual
    var usuarioActual: Usuario? = null
        private set

    // --- Lógica de Login ---
    fun login(onSuccess: () -> Unit) {
        viewModelScope.launch {
            _uiState.update { it.copy(estaCargando = true, errorGeneral = null) }
            try {
                val correo = _uiState.value.correo
                val clave = _uiState.value.clave

                // 1. Login en el servidor
                val usuarioRecibido = repository.login(correo, clave)

                // 2. Recuperar foto guardada localmente si existe
                val fotoGuardada = prefs.getString("foto_perfil_${usuarioRecibido.id}", null)
                
                // Si hay foto local, la usamos. Si no, usamos la que venga del servidor (si hubiera)
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

    // --- Actualizar foto de perfil (Persistente) ---
    fun actualizarFotoPerfil(uri: Uri) {
        val idUsuario = usuarioActual?.id ?: return
        val rutaFoto = uri.toString()

        // 1. Actualizar en memoria (para que se vea ya)
        usuarioActual = usuarioActual?.copy(fotoPerfilUrl = rutaFoto)

        // 2. Guardar en disco (SharedPreferences) para el futuro
        prefs.edit().putString("foto_perfil_$idUsuario", rutaFoto).apply()
        
        // Forzamos recomposición si es necesario (aunque usuarioActual no es flow, la pantalla lo relee)
    }

    // --- Actualizadores de campos ---
    fun onNombreChange(texto: String) = _uiState.update { it.copy(nombre = texto, errorNombre = null) }
    fun onAliasChange(texto: String) = _uiState.update { it.copy(alias = texto, errorAlias = null) }
    fun onCorreoChange(texto: String) = _uiState.update { it.copy(correo = texto, errorCorreo = null) }
    fun onClaveChange(texto: String) = _uiState.update { it.copy(clave = texto, errorClave = null) }
    fun onConfirmarClaveChange(texto: String) = _uiState.update { it.copy(confirmarClave = texto) }

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
        } else if (!Patterns.EMAIL_ADDRESS.matcher(s.correo).matches()) {
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