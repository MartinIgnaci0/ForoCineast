package com.example.forocineast.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.forocineast.model.Pelicula
import com.example.forocineast.repository.PeliculasRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class CarteleraViewModel(
    // Inyectamos el repositorio (o creamos uno por defecto)
    private val repository: PeliculasRepository = PeliculasRepository()
) : ViewModel() {

    // Estado de la lista de películas
    private val _peliculas = MutableStateFlow<List<Pelicula>>(emptyList())
    val peliculas: StateFlow<List<Pelicula>> = _peliculas

    // Estado de carga (para mostrar la ruedita girando)
    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    init {
        cargarCartelera()
    }

    fun cargarCartelera() {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                // Pedimos datos al repositorio
                val lista = repository.obtenerCartelera()
                _peliculas.value = lista
            } catch (e: Exception) {
                e.printStackTrace()
                _peliculas.value = emptyList()
            } finally {
                _isLoading.value = false
            }
        }
    }
}