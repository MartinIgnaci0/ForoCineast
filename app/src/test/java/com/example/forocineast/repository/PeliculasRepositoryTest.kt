package com.example.forocineast.repository

import com.example.forocineast.data.remote.ExternalApiService
import com.example.forocineast.data.remote.ExternalRetrofitInstance
import com.example.forocineast.model.Pelicula
import com.example.forocineast.model.PeliculaResponse
import io.mockk.coEvery
import io.mockk.mockk
import io.mockk.mockkObject
import io.mockk.unmockkAll
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test

class PeliculasRepositoryTest {

    private lateinit var api: ExternalApiService
    private lateinit var repository: PeliculasRepository

    @Before
    fun setup() {
        api = mockk()
        mockkObject(ExternalRetrofitInstance)
        coEvery { ExternalRetrofitInstance.api } returns api

        repository = PeliculasRepository()
    }

    @After
    fun tearDown() {
        unmockkAll()
    }

    @Test
    fun `obtenerCartelera devuelve lista de populares si api responde`() = runTest {
        val lista = listOf(Pelicula(1, "Popular"))
        val response = PeliculaResponse(lista)
        
        coEvery { api.getPopulares() } returns response

        val resultado = repository.obtenerCartelera()
        
        assertEquals(lista, resultado)
    }

    @Test
    fun `obtenerCartelera devuelve vacio si api falla`() = runTest {
        coEvery { api.getPopulares() } throws RuntimeException("Error de red")

        val resultado = repository.obtenerCartelera()
        
        assertEquals(emptyList<Pelicula>(), resultado)
    }

    @Test
    fun `obtenerEstrenos devuelve lista correcta`() = runTest {
        val lista = listOf(Pelicula(2, "Estreno"))
        val response = PeliculaResponse(lista)
        coEvery { api.getEstrenos() } returns response

        val resultado = repository.obtenerEstrenos()
        assertEquals(lista, resultado)
    }

    @Test
    fun `buscarContenido llama a endpoints y combina resultados`() = runTest {
        // Simulamos datos de series y películas usando argumentos nombrados para mayor claridad
        // CORREGIDO: Usamos solo argumentos nombrados para evitar pasar 'title' dos veces
        val serie = Pelicula(idRemote = 3, name = "Serie Test")
        val peli = Pelicula(idRemote = 4, title = "Peli Test")
        
        val responseSeries = PeliculaResponse(listOf(serie))
        val responsePelis = PeliculaResponse(listOf(peli))
        
        val query = "Test"
        
        // Mockeamos AMBAS llamadas
        coEvery { api.buscarSeries(query = query) } returns responseSeries
        coEvery { api.buscarPeliculas(query = query) } returns responsePelis

        // Ejecutamos la búsqueda mixta
        val resultado = repository.buscarContenido(query)
        
        // Verificamos que estén ambos en la lista resultante
        assertEquals(2, resultado.size)
    }

    @Test
    fun `obtenerPeliculasMejorValoradas devuelve lista`() = runTest {
        val lista = listOf(Pelicula(4, "Top Movie"))
        val response = PeliculaResponse(lista)
        coEvery { api.getPeliculasMejorValoradas() } returns response

        val resultado = repository.obtenerPeliculasMejorValoradas()
        assertEquals(lista, resultado)
    }

    @Test
    fun `obtenerSeriesMejorValoradas devuelve lista`() = runTest {
        val lista = listOf(Pelicula(5, "Top Series"))
        val response = PeliculaResponse(lista)
        coEvery { api.getSeriesMejorValoradas() } returns response

        val resultado = repository.obtenerSeriesMejorValoradas()
        assertEquals(lista, resultado)
    }
}