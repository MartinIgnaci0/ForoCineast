package com.example.forocineast.navigation

/**
 * Define las rutas de navegación de la aplicación.
 * Centraliza los strings para evitar errores de escritura al navegar.
 */
sealed class AppNavigation(val route: String) {
    // Autenticación
    object Login : AppNavigation("login_screen")
    object Registro : AppNavigation("registro_screen")

    // Pantallas Principales
    object Cartelera : AppNavigation("cartelera_screen") // Lista de películas (Home visual)
    object Foro : AppNavigation("foro_screen")           // Lista de reseñas (Chat/Comunidad)

    // Opcional: Para el futuro, si quieres ver el detalle de una peli
    object DetallePelicula : AppNavigation("detalle_pelicula/{peliculaId}") {
        fun createRoute(peliculaId: Int) = "detalle_pelicula/$peliculaId"
    }
}