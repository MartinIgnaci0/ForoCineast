package com.example.forocineast.data.local

import android.content.Context
import android.content.SharedPreferences
import com.example.forocineast.model.Pelicula
import com.google.gson.Gson
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

class FavoritosManagerTest {

    private lateinit var context: Context
    private lateinit var sharedPrefs: SharedPreferences
    private lateinit var editor: SharedPreferences.Editor
    private lateinit var manager: FavoritosManager
    private val gson = Gson()

    @Before
    fun setup() {
        context = mockk()
        sharedPrefs = mockk(relaxed = true)
        editor = mockk(relaxed = true)

        every { context.getSharedPreferences(any(), any()) } returns sharedPrefs
        every { sharedPrefs.edit() } returns editor
        every { editor.putString(any(), any()) } returns editor
        every { editor.apply() } returns Unit

        manager = FavoritosManager(context)
    }

    @Test
    fun `guardarFavorito guarda la pelicula en la lista del usuario`() {
        // Simulamos que la lista inicial está vacía (getString devuelve null o "[]")
        every { sharedPrefs.getString("lista_favoritos_1", any()) } returns null

        val peli = Pelicula(1, "Test Peli")
        
        manager.guardarFavorito(1, peli)

        // Verificamos que se guardó la lista con la peli
        val listaEsperada = listOf(peli)
        val jsonEsperado = gson.toJson(listaEsperada)
        
        verify { editor.putString("lista_favoritos_1", jsonEsperado) }
    }

    @Test
    fun `esFavorito devuelve true si esta en la lista`() {
        val peli = Pelicula(1, "Test Peli")
        val lista = listOf(peli)
        val json = gson.toJson(lista)

        // Simulamos que ya hay favoritos guardados
        every { sharedPrefs.getString("lista_favoritos_1", any()) } returns json

        assertTrue(manager.esFavorito(1, 1))
    }

    @Test
    fun `esFavorito devuelve false si no esta en la lista`() {
        // Lista vacía
        every { sharedPrefs.getString("lista_favoritos_1", any()) } returns "[]"

        assertFalse(manager.esFavorito(1, 99))
    }
}