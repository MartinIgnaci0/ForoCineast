package com.example.forocineast.navigation

/**
 * Define las rutas de navegación de la aplicación.
 */
sealed class AppNavigation(val route: String) {
    // Intro
    object Splash : AppNavigation("splash_screen")       // <--- NUEVA RUTA

    // Autenticación
    object Login : AppNavigation("login_screen")
    object Registro : AppNavigation("registro_screen")

    // Pantallas Principales
    object Cartelera : AppNavigation("cartelera_screen") // Home
    object Foro : AppNavigation("foro_screen")           // Comunidad
    object Perfil : AppNavigation("perfil_screen")       // Usuario
    object Favoritos : AppNavigation("favoritos_screen") // Favoritos

    // Detalles
    object DetallePelicula : AppNavigation("detalle_pelicula/{peliculaId}") {
        fun createRoute(peliculaId: Int) = "detalle_pelicula/$peliculaId"
    }
}