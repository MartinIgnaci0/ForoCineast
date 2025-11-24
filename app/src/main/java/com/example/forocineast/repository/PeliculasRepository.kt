package com.example.forocineast.repository

import com.example.forocineast.data.remote.ExternalRetrofitInstance
import com.example.forocineast.model.Pelicula

class PeliculasRepository {

    // Instancia de la API externa (TMDB)
    private val api = ExternalRetrofitInstance.api

    suspend fun obtenerCartelera(): List<Pelicula> {
        return try {
            val response = api.getPeliculasPopulares()
            response.resultados ?: emptyList()
        } catch (e: Exception) {
            e.printStackTrace()
            emptyList()
        }
    }
}