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
        // YA NO MOCKEAMOS Log AQUÍ. 
        // 'unitTests.isReturnDefaultValues = true' en build.gradle se encarga de ello.

        // Mockeamos el Singleton de Retrofit
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
        // Simulamos error
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
    fun `buscarSeries llama al endpoint correcto`() = runTest {
        val lista = listOf(Pelicula(3, "Breaking Bad"))
        val response = PeliculaResponse(lista)
        val query = "Breaking"
        
        coEvery { api.buscarSeries(query = query) } returns response

        val resultado = repository.buscarSeries(query)
        assertEquals(lista, resultado)
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