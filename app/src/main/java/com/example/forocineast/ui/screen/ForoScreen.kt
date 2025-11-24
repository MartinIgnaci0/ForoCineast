package com.example.forocineast.ui.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.forocineast.model.Post
import com.example.forocineast.viewmodel.AuthViewModel
import com.example.forocineast.viewmodel.ForoViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ForoScreen() {
    val foroViewModel: ForoViewModel = viewModel()
    val authViewModel: AuthViewModel = viewModel()

    val posts by foroViewModel.posts.collectAsState()
    val isLoading by foroViewModel.isLoading.collectAsState()

    var mostrarDialogo by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(title = { Text("Opiniones de la Comunidad") })
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { mostrarDialogo = true },
                containerColor = MaterialTheme.colorScheme.primary
            ) {
                Icon(Icons.Default.Add, contentDescription = "Nueva Reseña")
            }
        }
    ) { padding ->
        Box(modifier = Modifier.padding(padding).fillMaxSize()) {
            if (isLoading) {
                CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
            } else {
                LazyColumn(
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    items(posts) { post ->
                        PostItem(
                            post = post,
                            usuarioActualId = authViewModel.usuarioActual?.id ?: -1,
                            onDelete = { foroViewModel.eliminarResena(post, authViewModel.usuarioActual?.id ?: -1) }
                        )
                    }
                }
            }
        }

        if (mostrarDialogo) {
            // 👇 AQUÍ LLAMAMOS AL NUEVO NOMBRE
            CrearResenaDialog(
                onDismiss = { mostrarDialogo = false },
                onPublicar = { titulo, cuerpo, peli, rating, spoiler ->
                    foroViewModel.publicarResena(
                        titulo = titulo,
                        cuerpo = cuerpo,
                        peliculaRef = peli,
                        valoracion = rating,
                        tieneSpoilers = spoiler,
                        usuarioId = authViewModel.usuarioActual?.id ?: 0,
                        usuarioAlias = authViewModel.usuarioActual?.alias ?: "Anónimo"
                    )
                }
            )
        }
    }
}

@Composable
fun PostItem(post: Post, usuarioActualId: Int, onDelete: () -> Unit) {
    var mostrarSpoiler by remember { mutableStateOf(!post.tieneSpoilers) }

    Card(elevation = CardDefaults.cardElevation(4.dp)) {
        Column(modifier = Modifier.padding(16.dp)) {
            // Cabecera: Película y Estrellas
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = post.peliculaRef.uppercase(),
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.primary,
                    fontWeight = FontWeight.Bold
                )
                Text(text = "★".repeat(post.valoracion), color = Color(0xFFFFC107))
            }

            Spacer(modifier = Modifier.height(4.dp))

            // Título
            Text(
                text = post.titulo,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )

            // Autor
            Text(
                text = "por ${post.autorAlias ?: "Usuario ${post.autorId}"}",
                style = MaterialTheme.typography.bodySmall,
                color = Color.Gray,
                fontStyle = FontStyle.Italic
            )

            Spacer(modifier = Modifier.height(12.dp))

            // Contenido (Manejo de Spoilers)
            if (mostrarSpoiler) {
                Text(text = post.cuerpo, style = MaterialTheme.typography.bodyMedium)
            } else {
                Button(
                    onClick = { mostrarSpoiler = true },
                    colors = ButtonDefaults.buttonColors(containerColor = Color.Black)
                ) {
                    Icon(Icons.Default.Warning, contentDescription = null, tint = Color.Yellow)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Ver Spoiler", color = Color.Yellow)
                }
            }

            // Botón de eliminar (Solo si soy el dueño)
            if (post.autorId == usuarioActualId) {
                Spacer(modifier = Modifier.height(8.dp))
                HorizontalDivider()
                TextButton(
                    onClick = onDelete,
                    modifier = Modifier.align(Alignment.End)
                ) {
                    Text("Eliminar", color = MaterialTheme.colorScheme.error)
                    Icon(Icons.Default.Delete, contentDescription = null, tint = MaterialTheme.colorScheme.error)
                }
            }
        }
    }
}