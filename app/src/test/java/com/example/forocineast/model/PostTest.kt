package com.example.forocineast.model

import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class PostTest {

    @Test
    fun `esSpoiler devuelve true cuando tieneSpoilers es 1`() {
        val post = Post(
            titulo = "Test",
            cuerpo = "Cuerpo",
            peliculaRef = "Matrix",
            valoracion = 5,
            tieneSpoilers = 1, // 1 significa Sí
            fechaCreacion = "2024-01-01",
            autorId = 1
        )
        assertTrue(post.esSpoiler())
    }

    @Test
    fun `esSpoiler devuelve false cuando tieneSpoilers es 0`() {
        val post = Post(
            titulo = "Test",
            cuerpo = "Cuerpo",
            peliculaRef = "Matrix",
            valoracion = 5,
            tieneSpoilers = 0, // 0 significa No
            fechaCreacion = "2024-01-01",
            autorId = 1
        )
        assertFalse(post.esSpoiler())
    }
}