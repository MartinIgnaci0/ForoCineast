package com.example.forocineast.model

import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class AuthUiStateTest {

    @Test
    fun `esFormularioValido es true cuando todos los campos estan correctos`() {
        val state = AuthUiState(
            nombre = "Martin",
            alias = "MP",
            correo = "a@a.com",
            clave = "123456",
            confirmarClave = "123456"
        )
        assertTrue(state.esFormularioValido)
    }

    @Test
    fun `esFormularioValido es false si las claves no coinciden`() {
        val state = AuthUiState(
            nombre = "Martin",
            alias = "MP",
            correo = "a@a.com",
            clave = "123456",
            confirmarClave = "000000"
        )
        assertFalse(state.esFormularioValido)
    }

    @Test
    fun `esFormularioValido es false si algun campo esta vacio`() {
        val state = AuthUiState(
            nombre = "", // Falta nombre
            alias = "MP",
            correo = "a@a.com",
            clave = "123456",
            confirmarClave = "123456"
        )
        assertFalse(state.esFormularioValido)
    }
    
    @Test
    fun `esFormularioValido es false si la clave es corta`() {
        val state = AuthUiState(
            nombre = "Martin",
            alias = "MP",
            correo = "a@a.com",
            clave = "123", // Muy corta
            confirmarClave = "123"
        )
        assertFalse(state.esFormularioValido)
    }
}