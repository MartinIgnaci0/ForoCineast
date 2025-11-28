package com.example.forocineast.repository

import com.example.forocineast.data.remote.ApiService
import com.example.forocineast.data.remote.RetrofitInstance
import com.example.forocineast.model.Post
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import io.mockk.mockkObject
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import retrofit2.Response

class PostRepositoryTest {

    private lateinit var apiService: ApiService
    private lateinit var repository: PostRepository

    @Before
    fun setup() {
        apiService = mockk()
        mockkObject(RetrofitInstance)
        coEvery { RetrofitInstance.api } returns apiService
        repository = PostRepository()
    }

    @Test
    fun `obtenerResenas devuelve lista de posts`() = runBlocking {
        val postsEsperados = listOf(Post(1, "Titulo", "Cuerpo", "Peli", 5, 0, "2024", 1))
        coEvery { apiService.obtenerPosts() } returns postsEsperados

        val resultado = repository.obtenerResenas()

        assertEquals(postsEsperados, resultado)
    }

    @Test
    fun `publicarResena llama a la api`() = runBlocking {
        val nuevoPost = Post(titulo = "Nuevo", cuerpo = "Hola", peliculaRef = "A", valoracion = 5, tieneSpoilers = 0, fechaCreacion = "Hoy", autorId = 1)
        coEvery { apiService.crearPost(any()) } returns nuevoPost.copy(id = 100)

        val resultado = repository.publicarResena(nuevoPost)

        assertEquals(100, resultado.id)
        coVerify { apiService.crearPost(nuevoPost) }
    }

    @Test
    fun `eliminarResena llama a la api con userId`() = runBlocking {
        coEvery { apiService.eliminarPost(10, 5) } returns Response.success(Unit)

        repository.eliminarResena(10, 5)

        coVerify { apiService.eliminarPost(10, 5) }
    }
}