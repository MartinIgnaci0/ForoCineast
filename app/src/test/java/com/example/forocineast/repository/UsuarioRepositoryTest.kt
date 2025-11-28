package com.example.forocineast.repository

import com.example.forocineast.data.remote.ApiService
import com.example.forocineast.data.remote.RetrofitInstance
import com.example.forocineast.model.Usuario
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import io.mockk.mockkObject
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test

class UsuarioRepositoryTest {

    private lateinit var apiService: ApiService
    private lateinit var repository: UsuarioRepository

    @Before
    fun setup() {
        apiService = mockk()
        
        // Mockeamos el objeto singleton RetrofitInstance para inyectar nuestra API mock
        mockkObject(RetrofitInstance)
        coEvery { RetrofitInstance.api } returns apiService

        repository = UsuarioRepository()
    }

    @Test
    fun `login llama a la api y devuelve el usuario`() = runBlocking {
        val usuarioEsperado = Usuario(1, "Test", "Alias", "test@a.com", "123456")
        
        // Simulamos la respuesta de la API
        coEvery { apiService.login(any()) } returns usuarioEsperado

        val resultado = repository.login("test@a.com", "123456")

        // Verificamos que se llamó a la API con los datos correctos
        coVerify { 
            apiService.login(match { 
                it.correo == "test@a.com" && it.clave == "123456" 
            }) 
        }
        
        assertEquals(usuarioEsperado, resultado)
    }
}