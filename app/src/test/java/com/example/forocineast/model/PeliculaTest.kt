package com.example.forocineast.model

import org.junit.Assert.assertEquals
import org.junit.Test

class PeliculaTest {

    @Test
    fun `titulo devuelve title si existe`() {
        val peli = Pelicula(title = "Cine", name = null)
        assertEquals("Cine", peli.titulo)
    }

    @Test
    fun `titulo devuelve name si title es nulo`() {
        val serie = Pelicula(title = null, name = "Serie")
        assertEquals("Serie", serie.titulo)
    }

    @Test
    fun `titulo devuelve default si ambos son nulos`() {
        val desconocido = Pelicula(title = null, name = null)
        assertEquals("Título Desconocido", desconocido.titulo)
    }

    @Test
    fun `getPosterUrl devuelve URL completa si hay path`() {
        val peli = Pelicula(posterPath = "/imagen.jpg")
        assertEquals("https://image.tmdb.org/t/p/w500/imagen.jpg", peli.getPosterUrl())
    }

    @Test
    fun `getPosterUrl devuelve placeholder si path es nulo`() {
        val peli = Pelicula(posterPath = null)
        assertEquals("https://via.placeholder.com/300x450.png?text=No+Poster", peli.getPosterUrl())
    }
    
    @Test
    fun `anio devuelve releaseDate si existe`() {
        val peli = Pelicula(releaseDate = "2024-01-01")
        assertEquals("2024-01-01", peli.anio)
    }
    
    @Test
    fun `anio devuelve firstAirDate si es serie`() {
        val serie = Pelicula(firstAirDate = "2023-05-05")
        assertEquals("2023-05-05", serie.anio)
    }
}