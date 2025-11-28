package com.example.forocineast.viewmodel

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.example.forocineast.model.Usuario
import com.example.forocineast.repository.UsuarioRepository
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
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Rule
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class UsuarioViewModelTest {

    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    private val testDispatcher = StandardTestDispatcher()
    private lateinit var repository: UsuarioRepository
    private lateinit var viewModel: UsuarioViewModel

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        repository = mockk(relaxed = true)
        viewModel = UsuarioViewModel(repository)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `validarRegistro retorna false si campos vacios`() {
        viewModel.onNombreChange("")
        assertFalse(viewModel.validarRegistro())
        assertEquals("Falta ingresar tu nombre", viewModel.uiState.value.errorNombre)
    }

    @Test
    fun `validarRegistro retorna true si campos validos`() {
        viewModel.onNombreChange("User")
        viewModel.onAliasChange("Alias")
        viewModel.onCorreoChange("a@a.com")
        viewModel.onClaveChange("123456")
        viewModel.onConfirmarClaveChange("123456")
        
        assertTrue(viewModel.validarRegistro())
    }

    @Test
    fun `registrar llama al repositorio cuando datos validos`() = runTest {
        // Arrange
        viewModel.onNombreChange("User")
        viewModel.onAliasChange("Alias")
        viewModel.onCorreoChange("a@a.com")
        viewModel.onClaveChange("123456")
        viewModel.onConfirmarClaveChange("123456")
        
        val user = Usuario(1, "User", "Alias", "a@a.com", "123456")
        coEvery { repository.registro(any(), any(), any(), any()) } returns user

        // Act
        viewModel.registrar(onSuccess = {}, onValidationFailed = {})
        testDispatcher.scheduler.advanceUntilIdle()

        // Assert
        coVerify { repository.registro("User", "Alias", "a@a.com", "123456") }
        assertEquals(user, viewModel.usuarioActual)
    }
}