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

    init {
        cargarResenas()
    }

    fun cargarResenas() {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                _posts.value = repository.obtenerResenas()
            } catch (e: Exception) {
                e.printStackTrace()
            } finally {
                _isLoading.value = false
            }
        }
    }

    /**
     * Crea un post con los nuevos campos de Cine (Valoración, Spoilers, etc)
     */
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
            val fechaHoy = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())

            val nuevoPost = Post(
                titulo = titulo,
                cuerpo = cuerpo,
                peliculaRef = peliculaRef,
                valoracion = valoracion,
                tieneSpoilers = tieneSpoilers,
                fechaCreacion = fechaHoy,
                autorId = usuarioId,
                autorAlias = usuarioAlias
            )

            try {
                repository.publicarResena(nuevoPost)
                cargarResenas() // Recargamos la lista para ver el cambio
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    fun eliminarResena(post: Post, usuarioIdActual: Int) {
        viewModelScope.launch {
            try {
                post.id?.let { idPost ->
                    repository.eliminarResena(idPost, usuarioIdActual)
                    cargarResenas()
                }
            } catch (e: Exception) {
                println("Error al eliminar: ${e.message}")
            }
        }
    }
}