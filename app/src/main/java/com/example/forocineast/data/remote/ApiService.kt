package com.example.forocineast.data.remote

import com.example.forocineast.model.Post
import com.example.forocineast.model.Usuario
import retrofit2.Response
import retrofit2.http.*

/**
 * Interfaz de comunicación con el Backend (Node.js).
 * Define los endpoints para Usuarios y Reseñas de Cine.
 */
interface ApiService {

    // ==========================================
    //  USUARIOS (Autenticación)
    // ==========================================

    /**
     * Iniciar Sesión.
     * Envía {correo, clave} y recibe el Usuario completo (id, alias, token, etc).
     */
    @POST("usuarios/login")
    suspend fun login(@Body usuario: Usuario): Usuario

    /**
     * Crear nueva cuenta.
     * Envía los datos del registro y recibe el usuario creado.
     */
    @POST("usuarios/registro")
    suspend fun registro(@Body usuario: Usuario): Usuario


    // ==========================================
    //  FORO (Reseñas de Películas)
    // ==========================================

    /**
     * Obtener todas las reseñas.
     * Devuelve una lista de posts creados por la comunidad.
     */
    @GET("posts")
    suspend fun obtenerPosts(): List<Post>

    /**
     * Publicar una nueva reseña.
     * Se envía el objeto Post con título, cuerpo, valoración, spoilers, etc.
     */
    @POST("posts")
    suspend fun crearPost(@Body post: Post): Post

    /**
     * Eliminar una reseña.
     * Se pasa el ID del post en la ruta y el ID del usuario como query param
     * para verificar en el backend que el usuario es el dueño del post.
     */
    @DELETE("posts/{id}")
    suspend fun eliminarPost(
        @Path("id") idPost: Int,
        @Query("userId") idUsuario: Int
    ): Response<Unit>

    /**
     * Editar una reseña existente.
     */
    @PUT("posts/{id}")
    suspend fun editarPost(
        @Path("id") idPost: Int,
        @Body post: Post
    ): Response<Unit>
}