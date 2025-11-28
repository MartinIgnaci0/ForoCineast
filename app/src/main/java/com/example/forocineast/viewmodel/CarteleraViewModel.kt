package com.example.forocineast.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.forocineast.data.local.FavoritosManager
import com.example.forocineast.model.Pelicula
import com.example.forocineast.repository.PeliculasRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class CarteleraViewModel(
    application: Application,
    private val repository: PeliculasRepository,
    private val favoritosManager: FavoritosManager
) : AndroidViewModel(application) {

    // --- CONSTRUCTOR NECESARIO PARA LA APP ---
    // Este es el que usa Android cuando llamas a viewModel() en la pantalla
    constructor(application: Application) : this(
        application, 
        PeliculasRepository(), 
        FavoritosManager(application.applicationContext)
    )

    private val _estrenos = MutableStateFlow<List<Pelicula>>(emptyList())
    val estrenos: StateFlow<List<Pelicula>> = _estrenos

    private val _populares = MutableStateFlow<List<Pelicula>>(emptyList())
    val populares: StateFlow<List<Pelicula>> = _populares

    private val _peliculasMejorValoradas = MutableStateFlow<List<Pelicula>>(emptyList())
    val peliculasMejorValoradas: StateFlow<List<Pelicula>> = _peliculasMejorValoradas

    private val _seriesMejorValoradas = MutableStateFlow<List<Pelicula>>(emptyList())
    val seriesMejorValoradas: StateFlow<List<Pelicula>> = _seriesMejorValoradas

    private val _favoritos = MutableStateFlow<List<Pelicula>>(emptyList())
    val favoritos: StateFlow<List<Pelicula>> = _favoritos

    private val _resultadosBusqueda = MutableStateFlow<List<Pelicula>>(emptyList())
    val resultadosBusqueda: StateFlow<List<Pelicula>> = _resultadosBusqueda

    private val _busquedaActiva = MutableStateFlow(false)
    val busquedaActiva: StateFlow<Boolean> = _busquedaActiva

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    init {
        cargarCartelera()
    }

    fun cargarCartelera() {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                val listaPopulares = repository.obtenerCartelera()
                _populares.value = listaPopulares

                val listaEstrenos = repository.obtenerEstrenos()
                _estrenos.value = if (listaEstrenos.isNotEmpty()) listaEstrenos else listaPopulares.take(5)

                _peliculasMejorValoradas.value = repository.obtenerPeliculasMejorValoradas()
                _seriesMejorValoradas.value = repository.obtenerSeriesMejorValoradas()

            } catch (e: Exception) {
                e.printStackTrace()
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun cargarFavoritos(userId: Int) {
        _favoritos.value = favoritosManager.obtenerFavoritos(userId)
    }

    fun toggleFavorito(pelicula: Pelicula, userId: Int) {
        if (favoritosManager.esFavorito(userId, pelicula.idRemote)) {
            favoritosManager.eliminarFavorito(userId, pelicula.idRemote)
        } else {
            favoritosManager.guardarFavorito(userId, pelicula)
        }
        cargarFavoritos(userId)
    }

    fun esFavorito(pelicula: Pelicula, userId: Int): Boolean {
        return favoritosManager.esFavorito(userId, pelicula.idRemote)
    }

    fun buscar(query: String) {
        if (query.isBlank()) {
            _busquedaActiva.value = false
            return
        }
        _busquedaActiva.value = true
        viewModelScope.launch {
            _isLoading.value = true
            try {
                // Usamos buscarContenido para obtener series Y películas
                val resultados = repository.buscarContenido(query) // <--- CORREGIDO AQUI
                _resultadosBusqueda.value = resultados
            } catch (e: Exception) {
                e.printStackTrace()
                _resultadosBusqueda.value = emptyList()
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun cerrarBusqueda() {
        _busquedaActiva.value = false
        _resultadosBusqueda.value = emptyList()
    }

    fun obtenerPeliculaPorId(id: Int): Pelicula? {
        return _estrenos.value.find { it.idRemote == id }
            ?: _populares.value.find { it.idRemote == id }
            ?: _peliculasMejorValoradas.value.find { it.idRemote == id }
            ?: _seriesMejorValoradas.value.find { it.idRemote == id }
            ?: _resultadosBusqueda.value.find { it.idRemote == id }
            ?: _favoritos.value.find { it.idRemote == id }
    }
}