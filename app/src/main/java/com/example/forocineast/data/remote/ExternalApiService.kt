package com.example.forocineast.data.remote

import retrofit2.http.GET
import retrofit2.http.Query
import com.example.forocineast.model.PeliculaResponse

/**
 * Interfaz para las llamadas a The Movie Database (TMDB).
 * Aquí definimos los endpoints para traer información de cine.
 */
interface ExternalApiService {

    /**
     * Obtiene la lista de películas populares actuales.
     * @param apiKey Tu llave de acceso a TMDB (Regístrate en themoviedb.org).
     * @param language Idioma de los resultados (Español por defecto).
     * @param page Número de página para paginación.
     */
    @GET("movie/popular")
    suspend fun getPeliculasPopulares(
        @Query("api_key") apiKey: String = "TU_API_KEY_AQUI",
        @Query("language") language: String = "es-ES",
        @Query("page") page: Int = 1
    ): PeliculaResponse
}