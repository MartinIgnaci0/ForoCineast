package com.example.forocineast.viewmodel

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.example.forocineast.model.Post
import com.example.forocineast.repository.PostRepository
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Rule
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class ForoViewModelTest {

    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    private val testDispatcher = StandardTestDispatcher()
    private lateinit var repository: PostRepository
    private lateinit var viewModel: ForoViewModel

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        repository = mockk()
        // Simulamos que al iniciar, el repositorio devuelve una lista vacía
        coEvery { repository.obtenerResenas() } returns emptyList()
        
        viewModel = ForoViewModel(repository)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `Al publicar resena, se llama al repositorio y se actualiza la lista`() = runTest {
        // DADOS
        val postNuevo = Post(
            titulo = "Test Post",
            cuerpo = "Contenido",
            peliculaRef = "Peli",
            valoracion = 5,
            tieneSpoilers = 0,
            fechaCreacion = "2024-01-01",
            autorId = 1,
            autorAlias = "User"
        )
        // Simulamos que publicar devuelve el post creado
        coEvery { repository.publicarResena(any()) } returns postNuevo

        // CUANDO
        viewModel.publicarResena("Test Post", "Contenido", "Peli", 5, false, 1, "User")
        testDispatcher.scheduler.advanceUntilIdle() // Avanzamos el tiempo

        // ENTONCES
        // 1. Verificamos que se llamó al repositorio
        coVerify { repository.publicarResena(any()) }
        
        // 2. Verificamos que la lista del ViewModel ahora tiene 1 elemento (Optimistic update)
        assertEquals(1, viewModel.posts.value.size)
        assertEquals("Test Post", viewModel.posts.value[0].titulo)
    }
    
    @Test
    fun `Al eliminar resena, se quita de la lista`() = runTest {
        // DADOS: Una lista inicial con 1 post
        val postExistente = Post(id=1, titulo="A borrar", cuerpo="", peliculaRef="", valoracion=0, fechaCreacion="", autorId=1)
        
        // Simulamos que cargarResenas devuelve este post inicial (aunque en setup era empty, 
        // podemos inyectar estado inicial o simular el flujo)
        // Para este test, vamos a "trucar" el estado inicial o llamar a una función que cargue
        coEvery { repository.obtenerResenas() } returns listOf(postExistente)
        viewModel.cargarResenas()
        testDispatcher.scheduler.advanceUntilIdle()
        
        // Verificamos que empieza con 1
        assertEquals(1, viewModel.posts.value.size)

        // Simulamos eliminación exitosa
        coEvery { repository.eliminarResena(1, 1) } returns Unit

        // CUANDO
        viewModel.eliminarResena(postExistente, 1)
        testDispatcher.scheduler.advanceUntilIdle()

        // ENTONCES
        assertEquals(0, viewModel.posts.value.size)
    }
}