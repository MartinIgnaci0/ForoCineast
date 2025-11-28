package com.example.forocineast.viewmodel

import android.app.Application
import android.content.SharedPreferences
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.example.forocineast.data.local.FavoritosManager
import com.example.forocineast.model.Pelicula
import com.example.forocineast.repository.PeliculasRepository
import io.mockk.coEvery
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Rule
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class CarteleraViewModelTest {

    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    private val testDispatcher = StandardTestDispatcher()
    private lateinit var repository: PeliculasRepository
    private lateinit var favoritosManager: FavoritosManager
    private lateinit var application: Application
    private lateinit var viewModel: CarteleraViewModel
    private lateinit var sharedPrefs: SharedPreferences
    private lateinit var editor: SharedPreferences.Editor

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        repository = mockk()
        favoritosManager = mockk(relaxed = true)
        application = mockk()
        sharedPrefs = mockk(relaxed = true)
        editor = mockk(relaxed = true)

        every { application.applicationContext } returns application
        every { application.getSharedPreferences(any(), any()) } returns sharedPrefs
        every { sharedPrefs.edit() } returns editor
        every { editor.putString(any(), any()) } returns editor

        coEvery { repository.obtenerCartelera() } returns emptyList()
        coEvery { repository.obtenerEstrenos() } returns emptyList()
        coEvery { repository.obtenerPeliculasMejorValoradas() } returns emptyList()
        coEvery { repository.obtenerSeriesMejorValoradas() } returns emptyList()

        // Usamos argumentos nombrados para evitar errores de "Argument already passed"
        viewModel = CarteleraViewModel(
            application = application,
            repository = repository,
            favoritosManager = favoritosManager
        )
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `cargarCartelera actualiza las listas correctamente`() = runTest {
        val peli1 = Pelicula(1, "Peli 1")
        val peli2 = Pelicula(2, "Peli 2")
        
        coEvery { repository.obtenerCartelera() } returns listOf(peli1, peli2)
        coEvery { repository.obtenerEstrenos() } returns listOf(peli1)

        viewModel.cargarCartelera()
        testDispatcher.scheduler.advanceUntilIdle()

        assertEquals(2, viewModel.populares.value.size)
        assertEquals(1, viewModel.estrenos.value.size)
        assertFalse(viewModel.isLoading.value)
    }

    @Test
    fun `buscar actualiza resultadosBusqueda`() = runTest {
        val resultado = Pelicula(3, "Búsqueda")
        coEvery { repository.buscarContenido("query") } returns listOf(resultado)

        viewModel.buscar("query")
        assertTrue(viewModel.busquedaActiva.value)
        
        testDispatcher.scheduler.advanceUntilIdle()
        
        assertEquals(1, viewModel.resultadosBusqueda.value.size)
        assertEquals("Búsqueda", viewModel.resultadosBusqueda.value[0].titulo)
    }
    
    @Test
    fun `toggleFavorito guarda si no existe y elimina si existe`() = runTest {
        val peli = Pelicula(1, "Fav")
        val userId = 10
        
        every { favoritosManager.esFavorito(userId, 1) } returns false
        
        viewModel.toggleFavorito(peli, userId)
        
        verify { favoritosManager.guardarFavorito(userId, peli) }
        
        every { favoritosManager.esFavorito(userId, 1) } returns true
        
        viewModel.toggleFavorito(peli, userId)
        
        verify { favoritosManager.eliminarFavorito(userId, 1) }
    }

    @Test
    fun `obtenerPeliculaPorId encuentra la pelicula en cualquier lista`() = runTest {
        val peliEstreno = Pelicula(10, "Estreno")
        val peliPopular = Pelicula(20, "Popular")
        val peliBusqueda = Pelicula(30, "Buscada")

        coEvery { repository.obtenerEstrenos() } returns listOf(peliEstreno)
        coEvery { repository.obtenerCartelera() } returns listOf(peliPopular)
        coEvery { repository.buscarContenido("query") } returns listOf(peliBusqueda)

        viewModel.cargarCartelera()
        viewModel.buscar("query")
        testDispatcher.scheduler.advanceUntilIdle()

        val encontrada1 = viewModel.obtenerPeliculaPorId(10)
        assertNotNull("Debería encontrar en estrenos", encontrada1)
        assertEquals("Estreno", encontrada1?.titulo)

        val encontrada2 = viewModel.obtenerPeliculaPorId(20)
        assertNotNull("Debería encontrar en populares", encontrada2)
        assertEquals("Popular", encontrada2?.titulo)

        val encontrada3 = viewModel.obtenerPeliculaPorId(30)
        assertNotNull("Debería encontrar en búsqueda", encontrada3)
        assertEquals("Buscada", encontrada3?.titulo)
        
        val noEncontrada = viewModel.obtenerPeliculaPorId(999)
        assertEquals(null, noEncontrada)
    }
}