package com.example.forocineast.repository

import android.util.Log
import com.example.forocineast.data.remote.ExternalRetrofitInstance
import com.example.forocineast.model.Pelicula
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope

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
            Log.d("TMDB_REPO", "Peliculas mejor valoradas: ${lista.size}")
            lista
        } catch (e: Exception) {
            Log.e("TMDB_REPO", "Error al obtener peliculas top: ${e.message}")
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

    // --- BÚSQUEDA MIXTA (CINE Y TV) ---
    suspend fun buscarContenido(query: String): List<Pelicula> = coroutineScope {
        // Lanzamos las dos peticiones en paralelo para que sea más rápido
        val deferredSeries = async {
            try {
                api.buscarSeries(query = query).resultados ?: emptyList()
            } catch (e: Exception) {
                emptyList<Pelicula>()
            }
        }
        val deferredPeliculas = async {
            try {
                api.buscarPeliculas(query = query).resultados ?: emptyList()
            } catch (e: Exception) {
                emptyList<Pelicula>()
            }
        }

        // Esperamos ambos resultados
        val series = deferredSeries.await()
        val peliculas = deferredPeliculas.await()

        // Combinamos las listas
        val combinados = (series + peliculas)
            // Opcional: Ordenar por popularidad o relevancia para que lo mejor salga primero
            .sortedByDescending { it.calificacionPromedio } 
        
        Log.d("TMDB_REPO", "Resultados búsqueda '$query': ${combinados.size} (S:${series.size} + P:${peliculas.size})")
        combinados
    }
}