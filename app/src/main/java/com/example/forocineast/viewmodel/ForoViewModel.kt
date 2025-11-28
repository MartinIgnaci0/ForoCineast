package com.example.forocineast.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.forocineast.model.Post
import com.example.forocineast.repository.PostRepository
import kotlinx.coroutines.delay
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
                // Pequeña pausa artificial para asegurar que la BD remota actualizó (opcional pero útil)
                // delay(300) 
                val lista = repository.obtenerResenas()
                _posts.value = lista
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
            // 1. Activamos la carga
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
                // 2. Enviamos al servidor
                val postCreado = repository.publicarResena(nuevoPost)
                
                // 3. ACTUALIZACIÓN OPTIMISTA:
                // En lugar de esperar a recargar todo, agregamos el post nuevo a la lista actual manualmente
                // Esto hace que aparezca INSTANTANEAMENTE.
                val listaActual = _posts.value.toMutableList()
                
                // Lo agregamos al principio de la lista (índice 0) para que salga arriba
                listaActual.add(0, postCreado)
                
                _posts.value = listaActual

            } catch (e: Exception) {
                e.printStackTrace()
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun eliminarResena(post: Post, usuarioIdActual: Int) {
        viewModelScope.launch {
            try {
                post.id?.let { idPost ->
                    repository.eliminarResena(idPost, usuarioIdActual)
                    
                    // Actualización optimista para eliminar: Lo quitamos de la lista local
                    val listaActual = _posts.value.toMutableList()
                    listaActual.remove(post)
                    _posts.value = listaActual
                }
            } catch (e: Exception) {
                println("Error al eliminar: ${e.message}")
                // Si falla, recargamos por si acaso
                cargarResenas()
            }
        }
    }
}