package com.example.forocineast.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.forocineast.model.Post
import com.example.forocineast.repository.PostRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class ForoViewModel(
    private val repository: PostRepository = PostRepository()
) : ViewModel() {

    private val _posts = MutableStateFlow<List<Post>>(emptyList())
    val posts: StateFlow<List<Post>> = _posts

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    // Estado para mensajes de error (ej: fallo al eliminar)
    private val _mensajeError = MutableStateFlow<String?>(null)
    val mensajeError: StateFlow<String?> = _mensajeError

    init {
        cargarResenas()
    }

    fun limpiarError() {
        _mensajeError.value = null
    }

    fun cargarResenas() {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                val lista = repository.obtenerResenas()
                _posts.value = lista
            } catch (e: Exception) {
                e.printStackTrace()
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun publicarResena(
        titulo: String,
        cuerpo: String,
        peliculaRef: String,
        valoracion: Int,
        tieneSpoilers: Boolean,
        usuarioId: Int,
        usuarioAlias: String?
    ) {
        viewModelScope.launch {
            _isLoading.value = true
            val fechaHoy = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())

            val nuevoPost = Post(
                titulo = titulo,
                cuerpo = cuerpo,
                peliculaRef = peliculaRef,
                valoracion = valoracion,
                tieneSpoilers = if (tieneSpoilers) 1 else 0,
                fechaCreacion = fechaHoy,
                autorId = usuarioId,
                autorAlias = usuarioAlias
            )

            try {
                val postCreado = repository.publicarResena(nuevoPost)
                val listaActual = _posts.value.toMutableList()
                listaActual.add(0, postCreado)
                _posts.value = listaActual
            } catch (e: Exception) {
                e.printStackTrace()
                _mensajeError.value = "Error al publicar: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun eliminarResena(post: Post, usuarioIdActual: Int) {
        viewModelScope.launch {
            try {
                post.id?.let { idPost ->
                    // 1. Intentamos eliminar en el servidor
                    repository.eliminarResena(idPost, usuarioIdActual)
                    
                    // 2. Si no hubo error, actualizamos la lista local (Optimistic remove)
                    val listaActual = _posts.value.toMutableList()
                    listaActual.remove(post)
                    _posts.value = listaActual
                }
            } catch (e: Exception) {
                // Si falla (ej: el servidor dice 403 Forbidden), mostramos el error y recargamos
                e.printStackTrace()
                _mensajeError.value = e.message ?: "Error desconocido al eliminar"
                cargarResenas() // Restauramos la lista real
            }
        }
    }
}