package com.example.forocineast.ui.screen

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.outlined.Star
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import com.example.forocineast.model.Post

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CrearResenaDialog(
    onDismiss: () -> Unit,
    onPublicar: (titulo: String, cuerpo: String, pelicula: String, rating: Int, spoiler: Boolean) -> Unit,
    postAEditar: Post? = null
) {
    var titulo by remember { mutableStateOf(postAEditar?.titulo ?: "") }
    var cuerpo by remember { mutableStateOf(postAEditar?.cuerpo ?: "") }
    var pelicula by remember { mutableStateOf(postAEditar?.peliculaRef ?: "") }
    var rating by remember { mutableStateOf(postAEditar?.valoracion ?: 3) }
    var tieneSpoilers by remember { mutableStateOf(postAEditar?.esSpoiler() ?: false) }

    Dialog(onDismissRequest = onDismiss) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            shape = MaterialTheme.shapes.large,
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
        ) {
            Column(
                modifier = Modifier
                    .padding(16.dp)
                    .fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Text(
                    text = if (postAEditar == null) "Nueva Reseña" else "Editar Reseña",
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )

                // Campos de Texto
                OutlinedTextField(
                    value = pelicula,
                    onValueChange = { pelicula = it },
                    label = { Text("Película o Serie") },
                    placeholder = { Text("Ej: El Padrino") },
                    modifier = Modifier.fillMaxWidth()
                )

                OutlinedTextField(
                    value = titulo,
                    onValueChange = { titulo = it },
                    label = { Text("Título de tu crítica") },
                    modifier = Modifier.fillMaxWidth()
                )

                OutlinedTextField(
                    value = cuerpo,
                    onValueChange = { cuerpo = it },
                    label = { Text("Tu opinión...") },
                    modifier = Modifier.fillMaxWidth().height(120.dp),
                    maxLines = 5
                )

                // Selector de Estrellas
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text("Calificación:", style = MaterialTheme.typography.bodyMedium)
                    Spacer(modifier = Modifier.width(8.dp))
                    (1..5).forEach { index ->
                        Icon(
                            imageVector = if (index <= rating) Icons.Filled.Star else Icons.Outlined.Star,
                            contentDescription = null,
                            tint = Color(0xFFFFC107), // Dorado
                            modifier = Modifier
                                .size(32.dp)
                                .clickable { rating = index }
                        )
                    }
                }

                // Switch de Spoilers
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("¿Contiene Spoilers?", modifier = Modifier.weight(1f))
                    Switch(
                        checked = tieneSpoilers,
                        onCheckedChange = { tieneSpoilers = it }
                    )
                }

                // Botones
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End
                ) {
                    TextButton(onClick = onDismiss) { Text("Cancelar") }
                    Spacer(modifier = Modifier.width(8.dp))
                    Button(
                        onClick = {
                            if (titulo.isNotBlank() && cuerpo.isNotBlank()) {
                                onPublicar(titulo, cuerpo, pelicula, rating, tieneSpoilers)
                                onDismiss()
                            }
                        }
                    ) {
                        Text("Publicar")
                    }
                }
            }
        }
    }
}