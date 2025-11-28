package com.example.forocineast.data.local

import android.content.Context
import com.example.forocineast.model.Pelicula
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class FavoritosManager(context: Context) {
    private val prefs = context.getSharedPreferences("mis_favoritos", Context.MODE_PRIVATE)
    private val gson = Gson()

    // Ahora pedimos el userId para operar sobre la lista correcta
    fun guardarFavorito(userId: Int, pelicula: Pelicula) {
        val lista = obtenerFavoritos(userId).toMutableList()
        if (lista.none { it.idRemote == pelicula.idRemote }) {
            lista.add(pelicula)
            guardarLista(userId, lista)
        }
    }

    fun eliminarFavorito(userId: Int, idRemote: Int) {
        val lista = obtenerFavoritos(userId).toMutableList()
        lista.removeAll { it.idRemote == idRemote }
        guardarLista(userId, lista)
    }

    fun obtenerFavoritos(userId: Int): List<Pelicula> {
        // La clave ahora incluye el ID del usuario (ej: "lista_favoritos_5")
        val json = prefs.getString("lista_favoritos_$userId", null) ?: return emptyList()
        val type = object : TypeToken<List<Pelicula>>() {}.type
        return try {
            gson.fromJson(json, type)
        } catch (e: Exception) {
            emptyList()
        }
    }

    fun esFavorito(userId: Int, idRemote: Int): Boolean {
        return obtenerFavoritos(userId).any { it.idRemote == idRemote }
    }

    private fun guardarLista(userId: Int, lista: List<Pelicula>) {
        val json = gson.toJson(lista)
        prefs.edit().putString("lista_favoritos_$userId", json).apply()
    }
}