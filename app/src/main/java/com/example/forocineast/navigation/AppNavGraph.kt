package com.example.forocineast.navigation

import androidx.compose.runtime.Composable
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.example.forocineast.ui.screen.CarteleraScreen
import com.example.forocineast.ui.screen.DetallePeliculaScreen
import com.example.forocineast.ui.screen.FavoritosScreen
import com.example.forocineast.ui.screen.ForoScreen
import com.example.forocineast.ui.screen.LoginScreen
import com.example.forocineast.ui.screen.PerfilUsuarioScreen
import com.example.forocineast.ui.screen.RegistroScreen
import com.example.forocineast.ui.screen.SplashScreen
import com.example.forocineast.viewmodel.AuthViewModel
import com.example.forocineast.viewmodel.CarteleraViewModel

@Composable
fun AppNavGraph(navController: NavHostController) {
    val authViewModel: AuthViewModel = viewModel()
    val carteleraViewModel: CarteleraViewModel = viewModel()

    NavHost(
        navController = navController,
        // AHORA EMPEZAMOS EN LA INTRO (SPLASH)
        startDestination = AppNavigation.Splash.route
    ) {
        // 0. INTRO (SPLASH SCREEN)
        composable(AppNavigation.Splash.route) {
            SplashScreen(
                onAnimacionTerminada = {
                    // Al terminar la intro, vamos al Login y borramos la Splash del historial
                    navController.navigate(AppNavigation.Login.route) {
                        popUpTo(AppNavigation.Splash.route) { inclusive = true }
                    }
                }
            )
        }

        // 1. LOGIN
        composable(AppNavigation.Login.route) {
            LoginScreen(
                onNavigateToRegistro = { navController.navigate(AppNavigation.Registro.route) },
                onNavigateToCartelera = {
                    navController.navigate(AppNavigation.Cartelera.route) {
                        popUpTo(AppNavigation.Login.route) { inclusive = true }
                    }
                },
                viewModel = authViewModel
            )
        }

        // 2. REGISTRO
        composable(AppNavigation.Registro.route) {
            RegistroScreen(
                onBack = { navController.popBackStack() },
                onRegistroExitoso = {
                    navController.navigate(AppNavigation.Cartelera.route) {
                        popUpTo(AppNavigation.Login.route) { inclusive = true }
                    }
                },
                viewModel = authViewModel
            )
        }

        // 3. CARTELERA
        composable(AppNavigation.Cartelera.route) {
            // Obtenemos el ID del usuario logueado (o 0 si no hay)
            val userId = authViewModel.usuarioActual?.id ?: 0
            
            CarteleraScreen(
                onNavigateToForo = { navController.navigate(AppNavigation.Foro.route) },
                onNavigateToDetalle = { peliculaId ->
                    navController.navigate(AppNavigation.DetallePelicula.createRoute(peliculaId))
                },
                onNavigateToPerfil = { navController.navigate(AppNavigation.Perfil.route) },
                onNavigateToFavoritos = { navController.navigate(AppNavigation.Favoritos.route) },
                viewModel = carteleraViewModel,
                userId = userId
            )
        }

        // 4. FORO
        composable(AppNavigation.Foro.route) {
            ForoScreen(
                authViewModel = authViewModel
            )
        }

        // 5. PERFIL
        composable(AppNavigation.Perfil.route) {
            PerfilUsuarioScreen(
                authViewModel = authViewModel,
                onBack = { navController.popBackStack() },
                onCerrarSesion = {
                    navController.navigate(AppNavigation.Login.route) {
                        popUpTo(0) { inclusive = true }
                    }
                }
            )
        }

        // 6. FAVORITOS
        composable(AppNavigation.Favoritos.route) {
            val userId = authViewModel.usuarioActual?.id ?: 0
            
            FavoritosScreen(
                viewModel = carteleraViewModel,
                userId = userId,
                onBack = { navController.popBackStack() },
                onNavigateToDetalle = { peliculaId ->
                    navController.navigate(AppNavigation.DetallePelicula.createRoute(peliculaId))
                }
            )
        }

        // 7. DETALLE PELICULA
        composable(
            route = AppNavigation.DetallePelicula.route,
            arguments = listOf(navArgument("peliculaId") { type = NavType.IntType })
        ) { backStackEntry ->
            val peliculaId = backStackEntry.arguments?.getInt("peliculaId") ?: 0
            val pelicula = carteleraViewModel.obtenerPeliculaPorId(peliculaId)
            
            DetallePeliculaScreen(
                pelicula = pelicula,
                onBack = { navController.popBackStack() }
            )
        }
    }
}