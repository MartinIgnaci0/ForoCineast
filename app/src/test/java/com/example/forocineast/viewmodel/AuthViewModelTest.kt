package com.example.forocineast.viewmodel

import android.app.Application
import android.content.SharedPreferences
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.example.forocineast.model.Usuario
import com.example.forocineast.repository.UsuarioRepository
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
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
class AuthViewModelTest {

    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    private val testDispatcher = StandardTestDispatcher()
    private lateinit var repository: UsuarioRepository
    private lateinit var application: Application
    private lateinit var viewModel: AuthViewModel
    private lateinit var sharedPrefs: SharedPreferences
    private lateinit var editor: SharedPreferences.Editor

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        repository = mockk()
        application = mockk()
        sharedPrefs = mockk(relaxed = true)
        editor = mockk(relaxed = true)

        every { application.applicationContext } returns application
        every { application.getSharedPreferences(any(), any()) } returns sharedPrefs
        every { sharedPrefs.edit() } returns editor
        every { editor.putString(any(), any()) } returns editor
        every { editor.apply() } returns Unit
        every { sharedPrefs.getString(any(), any()) } returns null

        viewModel = AuthViewModel(application, repository)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `validarRegistro devuelve falso si el correo es invalido`() {
        viewModel.onCorreoChange("correo_falso_sin_arroba")
        viewModel.onClaveChange("123456")
        viewModel.onConfirmarClaveChange("123456")
        viewModel.onNombreChange("Test")
        viewModel.onAliasChange("Alias")

        val esValido = viewModel.validarRegistro()
        
        assertFalse(esValido)
        assertEquals("Formato de correo inválido", viewModel.uiState.value.errorCorreo)
    }

    @Test
    fun `validarRegistro devuelve verdadero si todo es correcto`() {
        viewModel.onCorreoChange("usuario@test.com")
        viewModel.onClaveChange("123456")
        viewModel.onConfirmarClaveChange("123456")
        viewModel.onNombreChange("Test")
        viewModel.onAliasChange("Alias")

        assertTrue(viewModel.validarRegistro())
    }

    @Test
    fun `login exitoso actualiza el estado`() = runTest {
        val usuarioMock = Usuario(1, "Test", "Alias", "a@a.com", "123456")
        coEvery { repository.login(any(), any()) } returns usuarioMock

        viewModel.onCorreoChange("a@a.com")
        viewModel.onClaveChange("123456")

        viewModel.login {}
        testDispatcher.scheduler.advanceUntilIdle()

        assertEquals(usuarioMock, viewModel.usuarioActual)
        assertFalse(viewModel.uiState.value.estaCargando)
    }

    @Test
    fun `registrar llama al repositorio si la validacion pasa`() = runTest {
        // DADOS
        viewModel.onNombreChange("User")
        viewModel.onAliasChange("Alias")
        viewModel.onCorreoChange("test@test.com")
        viewModel.onClaveChange("123456")
        viewModel.onConfirmarClaveChange("123456")

        val nuevoUsuario = Usuario(2, "User", "Alias", "test@test.com", "123456")
        coEvery { repository.registro(any(), any(), any(), any()) } returns nuevoUsuario

        // CUANDO
        viewModel.registrar {}
        testDispatcher.scheduler.advanceUntilIdle()

        // ENTONCES
        coVerify { repository.registro("User", "Alias", "test@test.com", "123456") }
        assertEquals(nuevoUsuario, viewModel.usuarioActual)
        assertTrue(viewModel.uiState.value.registroExitoso)
    }

    @Test
    fun `registrar no llama al repositorio si la validacion falla`() = runTest {
        // DADOS (Falta nombre)
        viewModel.onNombreChange("") 
        
        // CUANDO
        viewModel.registrar {}
        testDispatcher.scheduler.advanceUntilIdle()

        // ENTONCES
        coVerify(exactly = 0) { repository.registro(any(), any(), any(), any()) }
    }
}