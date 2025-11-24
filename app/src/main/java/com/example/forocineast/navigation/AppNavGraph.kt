package com.example.forocineast.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.forocineast.ui.screen.CarteleraScreen
import com.example.forocineast.ui.screen.ForoScreen
import com.example.forocineast.ui.screen.LoginScreen
import com.example.forocineast.ui.screen.RegistroScreen

@Composable
fun AppNavGraph(navController: NavHostController) {
    NavHost(
        navController = navController,
        startDestination = AppNavigation.Login.route
    ) {
        // 1. Pantalla de Login
        composable(AppNavigation.Login.route) {
            LoginScreen(
                onNavigateToRegistro = { navController.navigate(AppNavigation.Registro.route) },
                onNavigateToCartelera = {
                    // Al entrar, vamos a la Cartelera. Borramos Login del historial.
                    navController.navigate(AppNavigation.Cartelera.route) {
                        popUpTo(AppNavigation.Login.route) { inclusive = true }
                    }
                }
            )
        }

        // 2. Pantalla de Registro
        composable(AppNavigation.Registro.route) {
            RegistroScreen(
                onBack = { navController.popBackStack() },
                onRegistroExitoso = {
                    navController.navigate(AppNavigation.Cartelera.route) {
                        popUpTo(AppNavigation.Login.route) { inclusive = true }
                    }
                }
            )
        }

        // 3. Pantalla Principal (Cartelera)
        composable(AppNavigation.Cartelera.route) {
            CarteleraScreen(
                onNavigateToForo = { navController.navigate(AppNavigation.Foro.route) }
            )
        }

        // 4. Pantalla del Foro (Comunidad)
        composable(AppNavigation.Foro.route) {
            ForoScreen()
        }
    }
}