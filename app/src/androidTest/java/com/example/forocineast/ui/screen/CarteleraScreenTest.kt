package com.example.forocineast.ui.screen

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.forocineast.model.Pelicula
import com.example.forocineast.viewmodel.CarteleraViewModel
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.flow.MutableStateFlow
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class CarteleraScreenTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun carteleraMuestraSeccionesYEstrenos() {
        // 1. Mock del ViewModel y sus Estados
        val viewModel = mockk<CarteleraViewModel>(relaxed = true)
        
        // Simulamos datos
        val peliEstreno = Pelicula(1, "Estreno Test")
        val peliPopular = Pelicula(2, "Popular Test")
        
        every { viewModel.estrenos } returns MutableStateFlow(listOf(peliEstreno))
        every { viewModel.populares } returns MutableStateFlow(listOf(peliPopular))
        every { viewModel.peliculasMejorValoradas } returns MutableStateFlow(emptyList())
        every { viewModel.seriesMejorValoradas } returns MutableStateFlow(emptyList())
        every { viewModel.busquedaActiva } returns MutableStateFlow(false)
        every { viewModel.resultadosBusqueda } returns MutableStateFlow(emptyList())
        every { viewModel.favoritos } returns MutableStateFlow(emptyList())
        every { viewModel.isLoading } returns MutableStateFlow(false)
        
        // Simulamos funciones
        every { viewModel.esFavorito(any(), any()) } returns false

        // 2. Cargamos la Pantalla
        composeTestRule.setContent {
            CarteleraScreen(
                onNavigateToForo = {},
                onNavigateToDetalle = {},
                onNavigateToPerfil = {},
                onNavigateToFavoritos = {},
                viewModel = viewModel,
                userId = 1
            )
        }

        // 3. Verificamos que aparezcan los textos clave
        composeTestRule.onNodeWithText("Cartelera Cineast").assertIsDisplayed()
        composeTestRule.onNodeWithText("🔥 En Cartelera (Estrenos)").assertIsDisplayed()
        
        // Verificamos que aparezca nuestra película fake
        composeTestRule.onNodeWithText("Estreno Test").assertIsDisplayed()
        composeTestRule.onNodeWithText("Popular Test").assertIsDisplayed()
    }
}