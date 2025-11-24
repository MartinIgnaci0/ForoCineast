package com.example.forocineast.repository

import com.example.forocineast.data.remote.RetrofitInstance
import com.example.forocineast.model.Post

class PostRepository {

    private val api = RetrofitInstance.api

    suspend fun obtenerResenas(): List<Post> {
        return api.obtenerPosts()
    }

    suspend fun publicarResena(post: Post): Post {
        // El 'Post' ya viene armado desde el ViewModel con título, spoiler, rating, etc.
        return api.crearPost(post)
    }

    suspend fun eliminarResena(idPost: Int, idAutor: Int) {
        val response = api.eliminarPost(idPost, idAutor)
        if (!response.isSuccessful) {
            throw Exception("Error al eliminar: ${response.code()} (¿Eres el autor?)")
        }
    }

    suspend fun editarResena(post: Post) {
        post.id?.let { id ->
            val response = api.editarPost(id, post)
            if (!response.isSuccessful) throw Exception("Error al editar reseña")
        }
    }
}