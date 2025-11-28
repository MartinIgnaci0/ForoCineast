package com.example.forocineast.data.remote

import com.example.forocineast.model.PeliculaResponse
import retrofit2.http.GET
import retrofit2.http.Query

interface ExternalApiService {
    // ==========================================
    //  TMDB API (The Movie Database)
    // ==========================================

    // 1. POPULARES (Películas)
    @GET("movie/popular")
    suspend fun getPopulares(
        @Query("language") idioma: String = "es-ES",
        @Query("page") pagina: Int = 1
    ): PeliculaResponse

    // 2. ESTRENOS (Películas)
    @GET("movie/now_playing")
    suspend fun getEstrenos(
        @Query("language") idioma: String = "es-ES",
        @Query("page") pagina: Int = 1
    ): PeliculaResponse

    // 3. MEJOR VALORADAS - PELICULAS
    @GET("movie/top_rated")
    suspend fun getPeliculasMejorValoradas(
        @Query("language") idioma: String = "es-ES",
        @Query("page") pagina: Int = 1
    ): PeliculaResponse

    // 4. MEJOR VALORADAS - SERIES (TV)
    @GET("tv/top_rated")
    suspend fun getSeriesMejorValoradas(
        @Query("language") idioma: String = "es-ES",
        @Query("page") pagina: Int = 1
    ): PeliculaResponse

    // 5. BUSCAR SERIES
    @GET("search/tv")
    suspend fun buscarSeries(
        @Query("query") query: String,
        @Query("language") idioma: String = "es-ES",
        @Query("page") pagina: Int = 1
    ): PeliculaResponse
}