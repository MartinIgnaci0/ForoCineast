package com.example.forocineast.ui.components

import android.net.Uri
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage

/**
 * Muestra la foto de perfil del usuario o un icono por defecto.
 */
@Composable
fun AvatarUsuario(
    imagenUri: Uri?,
    modifier: Modifier = Modifier,
    size: Int = 100
) {
    if (imagenUri != null) {
        AsyncImage(
            model = imagenUri,
            contentDescription = "Avatar",
            modifier = modifier
                .size(size.dp)
                .clip(CircleShape),
            contentScale = ContentScale.Crop
        )
    } else {
        Icon(
            imageVector = Icons.Default.AccountCircle,
            contentDescription = "Sin avatar",
            tint = MaterialTheme.colorScheme.primary, // Usamos el rojo cine
            modifier = modifier.size(size.dp)
        )
    }
}