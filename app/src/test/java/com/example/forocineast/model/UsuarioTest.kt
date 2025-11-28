package com.example.forocineast.model

import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class UsuarioTest {

    @Test
    fun `isAdmin devuelve true si esAdmin es 1`() {
        val admin = Usuario(nombreCompleto = "Admin", alias = "Boss", correo = "a@a.com", clave = "123", esAdmin = 1)
        assertTrue(admin.isAdmin())
    }

    @Test
    fun `isAdmin devuelve false si esAdmin es 0`() {
        val usuario = Usuario(nombreCompleto = "User", alias = "Fan", correo = "u@u.com", clave = "123", esAdmin = 0)
        assertFalse(usuario.isAdmin())
    }
}