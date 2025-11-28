package com.example.forocineast.navigation

import org.junit.Assert.assertEquals
import org.junit.Test

class AppNavigationTest {

    @Test
    fun `rutas son correctas`() {
        assertEquals("login_screen", AppNavigation.Login.route)
        assertEquals("registro_screen", AppNavigation.Registro.route)
        assertEquals("cartelera_screen", AppNavigation.Cartelera.route)
        assertEquals("foro_screen", AppNavigation.Foro.route)
        assertEquals("favoritos_screen", AppNavigation.Favoritos.route)
        assertEquals("splash_screen", AppNavigation.Splash.route)
    }

    @Test
    fun `ruta detalle pelicula se genera correctamente`() {
        val id = 123
        val rutaGenerada = AppNavigation.DetallePelicula.createRoute(id)
        assertEquals("detalle_pelicula/123", rutaGenerada)
    }
}