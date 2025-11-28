package com.example.forocineast.ui.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.example.forocineast.model.Pelicula

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DetallePeliculaScreen(
    pelicula: Pelicula?,
    onBack: () -> Unit
) {
    // Si la película es nula (error raro), mostramos mensaje y botón volver
    if (pelicula == null) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Text("No se encontró la película")
            Button(onClick = onBack) { Text("Volver") }
        }
        return
    }

    Scaffold(
        floatingActionButton = {
            // Botón flotante para volver
            FloatingActionButton(onClick = onBack, containerColor = MaterialTheme.colorScheme.primary) {
                Icon(Icons.Default.ArrowBack, contentDescription = "Volver")
            }
        }
    ) { padding ->
        // Usamos un Box para superponer el gradiente sobre la imagen
        Box(modifier = Modifier.fillMaxSize().background(MaterialTheme.colorScheme.background)) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
            ) {
                // 1. BANNER GIGANTE
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(400.dp)
                ) {
                    AsyncImage(
                        model = pelicula.getBannerUrl(),
                        contentDescription = pelicula.titulo,
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Crop
                    )
                    // Gradiente negro abajo para que el texto se lea bien
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(
                                Brush.verticalGradient(
                                    colors = listOf(Color.Transparent, MaterialTheme.colorScheme.background),
                                    startY = 300f
                                )
                            )
                    )
                }

                // 2. INFO PRINCIPAL
                Column(
                    modifier = Modifier
                        .padding(16.dp)
                        .offset(y = (-60).dp) // Subimos el texto para que pise la imagen
                ) {
                    // Título
                    Text(
                        text = pelicula.titulo,
                        style = MaterialTheme.typography.displaySmall,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                    
                    Spacer(modifier = Modifier.height(8.dp))

                    // Fila de Metadatos (Año | Rating)
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        // Año
                        Surface(
                            color = MaterialTheme.colorScheme.primary,
                            shape = MaterialTheme.shapes.small
                        ) {
                            Text(
                                text = pelicula.anio?.take(4) ?: "????",
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                                color = Color.White,
                                fontWeight = FontWeight.Bold
                            )
                        }
                        
                        Spacer(modifier = Modifier.width(16.dp))

                        // Estrellas
                        Icon(Icons.Default.Star, null, tint = Color(0xFFFFC107))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "${String.format("%.1f", pelicula.calificacionPromedio)} / 10",
                            style = MaterialTheme.typography.titleMedium,
                            color = Color.White
                        )
                    }

                    Spacer(modifier = Modifier.height(24.dp))

                    // SINOPSIS
                    Text(
                        text = "Resumen",
                        style = MaterialTheme.typography.titleLarge,
                        color = MaterialTheme.colorScheme.primary,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = pelicula.sinopsis ?: "Sin descripción.",
                        style = MaterialTheme.typography.bodyLarge,
                        color = Color.LightGray,
                        lineHeight = MaterialTheme.typography.bodyLarge.fontSize * 1.5
                    )
                }
                
                // Espacio extra al final
                Spacer(modifier = Modifier.height(100.dp))
            }
        }
    }
}