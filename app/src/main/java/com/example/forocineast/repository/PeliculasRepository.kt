package com.example.forocineast.repository

import android.util.Log
import com.example.forocineast.data.remote.ExternalRetrofitInstance
import com.example.forocineast.model.Pelicula

class PeliculasRepository {

    private val api = ExternalRetrofitInstance.api

    suspend fun obtenerCartelera(): List<Pelicula> {
        return try {
            val respuesta = api.getPopulares()
            val lista = respuesta.resultados ?: emptyList()
            Log.d("TMDB_REPO", "Populares encontradas: ${lista.size}")
            lista
        } catch (e: Exception) {
            Log.e("TMDB_REPO", "Error al obtener populares: ${e.message}")
            e.printStackTrace()
            emptyList()
        }
    }

    suspend fun obtenerEstrenos(): List<Pelicula> {
        return try {
            val respuesta = api.getEstrenos()
            val lista = respuesta.resultados ?: emptyList()
            Log.d("TMDB_REPO", "Estrenos encontrados: ${lista.size}")
            lista
        } catch (e: Exception) {
            Log.e("TMDB_REPO", "Error al obtener estrenos: ${e.message}")
            e.printStackTrace()
            emptyList()
        }
    }

    suspend fun obtenerPeliculasMejorValoradas(): List<Pelicula> {
        return try {
            val respuesta = api.getPeliculasMejorValoradas()
            val lista = respuesta.resultados ?: emptyList()
            Log.d("TMDB_REPO", "Pelis mejor valoradas: ${lista.size}")
            lista
        } catch (e: Exception) {
            Log.e("TMDB_REPO", "Error al obtener pelis top: ${e.message}")
            emptyList()
        }
    }

    suspend fun obtenerSeriesMejorValoradas(): List<Pelicula> {
        return try {
            val respuesta = api.getSeriesMejorValoradas()
            val lista = respuesta.resultados ?: emptyList()
            Log.d("TMDB_REPO", "Series mejor valoradas: ${lista.size}")
            lista
        } catch (e: Exception) {
            Log.e("TMDB_REPO", "Error al obtener series top: ${e.message}")
            emptyList()
        }
    }

    // --- NUEVA FUNCIÓN: BUSCAR SERIES ---
    suspend fun buscarSeries(query: String): List<Pelicula> {
        return try {
            val respuesta = api.buscarSeries(query = query)
            val lista = respuesta.resultados ?: emptyList()
            Log.d("TMDB_REPO", "Resultados búsqueda '$query': ${lista.size}")
            lista
        } catch (e: Exception) {
            Log.e("TMDB_REPO", "Error buscando series: ${e.message}")
            emptyList()
        }
    }
}