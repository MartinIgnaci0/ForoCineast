package com.example.forocineast.navigation

/**
 * Eventos que los ViewModels pueden disparar para solicitar navegación.
 */
sealed class NavEvent {
    object NavigateToLogin : NavEvent()
    object NavigateToRegistro : NavEvent()
    object NavigateToCartelera : NavEvent()
    object NavigateToForo : NavEvent()
}