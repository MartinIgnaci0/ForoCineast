package com.example.forocineast.ui.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Chat // <--- Import actualizado
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import com.example.forocineast.model.Pelicula
import com.example.forocineast.viewmodel.CarteleraViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CarteleraScreen(
    onNavigateToForo: () -> Unit,
    onNavigateToDetalle: (Int) -> Unit,
    onNavigateToPerfil: () -> Unit,
    onNavigateToFavoritos: () -> Unit,
    viewModel: CarteleraViewModel,
    userId: Int
) {
    // Estados
    val estrenos by viewModel.estrenos.collectAsState()
    val populares by viewModel.populares.collectAsState()
    val peliculasMejorValoradas by viewModel.peliculasMejorValoradas.collectAsState()
    val seriesMejorValoradas by viewModel.seriesMejorValoradas.collectAsState()
    
    val busquedaActiva by viewModel.busquedaActiva.collectAsState()
    val resultadosBusqueda by viewModel.resultadosBusqueda.collectAsState()
    
    val isLoading by viewModel.isLoading.collectAsState()

    var textoBusqueda by remember { mutableStateOf("") }

    LaunchedEffect(userId) {
        viewModel.cargarFavoritos(userId)
    }

    Scaffold(
        topBar = {
            Column(modifier = Modifier.background(MaterialTheme.colorScheme.background)) {
                CenterAlignedTopAppBar(
                    title = { Text("Cartelera Cineast") },
                    navigationIcon = {
                        IconButton(onClick = onNavigateToPerfil) {
                            Icon(
                                imageVector = Icons.Default.AccountCircle,
                                contentDescription = "Mi Perfil",
                                tint = MaterialTheme.colorScheme.primary
                            )
                        }
                    },
                    actions = {
                        IconButton(onClick = onNavigateToFavoritos) {
                            Icon(
                                imageVector = Icons.Default.Favorite,
                                contentDescription = "Favoritos",
                                tint = Color.Red
                            )
                        }
                    },
                    colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                        containerColor = MaterialTheme.colorScheme.background,
                        titleContentColor = MaterialTheme.colorScheme.primary
                    )
                )

                OutlinedTextField(
                    value = textoBusqueda,
                    onValueChange = { 
                        textoBusqueda = it
                        viewModel.buscar(it) 
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 8.dp),
                    placeholder = { Text("Buscar series...", color = Color.Gray) },
                    leadingIcon = { 
                        Icon(Icons.Default.Search, contentDescription = null, tint = Color.Gray) 
                    },
                    trailingIcon = {
                        if (textoBusqueda.isNotEmpty()) {
                            IconButton(onClick = { 
                                textoBusqueda = ""
                                viewModel.cerrarBusqueda()
                            }) {
                                Icon(Icons.Default.Close, contentDescription = "Borrar", tint = Color.Gray)
                            }
                        }
                    },
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = MaterialTheme.colorScheme.primary,
                        unfocusedBorderColor = Color.Gray,
                        focusedTextColor = Color.White,
                        unfocusedTextColor = Color.White,
                        cursorColor = MaterialTheme.colorScheme.primary
                    ),
                    singleLine = true
                )
            }
        },
        floatingActionButton = {
            ExtendedFloatingActionButton(
                onClick = onNavigateToForo,
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = Color.White,
                // Usamos el icono AutoMirrored para evitar el warning
                icon = { Icon(Icons.AutoMirrored.Filled.Chat, contentDescription = null) },
                text = { Text("Ir al Foro") }
            )
        }
    ) { padding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .background(MaterialTheme.colorScheme.background)
        ) {
            if (isLoading) {
                CircularProgressIndicator(
                    modifier = Modifier.align(Alignment.Center),
                    color = MaterialTheme.colorScheme.primary
                )
            } else {
                if (busquedaActiva && textoBusqueda.isNotBlank()) {
                    LazyVerticalGrid(
                        columns = GridCells.Adaptive(minSize = 150.dp),
                        contentPadding = PaddingValues(16.dp),
                        verticalArrangement = Arrangement.spacedBy(16.dp),
                        horizontalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        items(resultadosBusqueda) { pelicula ->
                            ItemPeliculaMini(
                                pelicula = pelicula,
                                esFavorito = viewModel.esFavorito(pelicula, userId),
                                onToggleFavorito = { viewModel.toggleFavorito(pelicula, userId) },
                                onClick = { onNavigateToDetalle(pelicula.idRemote) }
                            )
                        }
                    }
                } else {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .verticalScroll(rememberScrollState())
                            .padding(vertical = 16.dp),
                        verticalArrangement = Arrangement.spacedBy(24.dp)
                    ) {
                        SeccionTitulo("🔥 En Cartelera (Estrenos)")
                        LazyRow(
                            contentPadding = PaddingValues(horizontal = 16.dp),
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            items(estrenos) { pelicula ->
                                ItemPeliculaMini(
                                    pelicula = pelicula,
                                    esFavorito = viewModel.esFavorito(pelicula, userId),
                                    onToggleFavorito = { viewModel.toggleFavorito(pelicula, userId) },
                                    onClick = { onNavigateToDetalle(pelicula.idRemote) }
                                )
                            }
                        }

                        SeccionTitulo("🍿 Más Populares")
                        LazyRow(
                            contentPadding = PaddingValues(horizontal = 16.dp),
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            items(populares) { pelicula ->
                                ItemPeliculaGrande(
                                    pelicula = pelicula,
                                    esFavorito = viewModel.esFavorito(pelicula, userId),
                                    onToggleFavorito = { viewModel.toggleFavorito(pelicula, userId) },
                                    onClick = { onNavigateToDetalle(pelicula.idRemote) }
                                )
                            }
                        }

                        SeccionTitulo("⭐ Cine: Top Crítica")
                        LazyRow(
                            contentPadding = PaddingValues(horizontal = 16.dp),
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            items(peliculasMejorValoradas) { pelicula ->
                                ItemPeliculaGrande(
                                    pelicula = pelicula,
                                    esFavorito = viewModel.esFavorito(pelicula, userId),
                                    onToggleFavorito = { viewModel.toggleFavorito(pelicula, userId) },
                                    onClick = { onNavigateToDetalle(pelicula.idRemote) }
                                )
                            }
                        }

                        SeccionTitulo("📺 Series: Top Crítica")
                        LazyRow(
                            contentPadding = PaddingValues(horizontal = 16.dp),
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            items(seriesMejorValoradas) { pelicula ->
                                ItemPeliculaGrande(
                                    pelicula = pelicula,
                                    esFavorito = viewModel.esFavorito(pelicula, userId),
                                    onToggleFavorito = { viewModel.toggleFavorito(pelicula, userId) },
                                    onClick = { onNavigateToDetalle(pelicula.idRemote) }
                                )
                            }
                        }
                        
                        Spacer(modifier = Modifier.height(80.dp))
                    }
                }
            }
        }
    }
}

@Composable
fun SeccionTitulo(titulo: String) {
    Text(
        text = titulo,
        style = MaterialTheme.typography.headlineSmall,
        fontWeight = FontWeight.Bold,
        color = Color.White,
        modifier = Modifier.padding(horizontal = 16.dp)
    )
}

@Composable
fun ItemPeliculaMini(
    pelicula: Pelicula,
    esFavorito: Boolean = false,
    onToggleFavorito: () -> Unit = {},
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .width(140.dp)
            .clickable { onClick() },
        elevation = CardDefaults.cardElevation(4.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
    ) {
        Box {
            Column {
                AsyncImage(
                    model = pelicula.getPosterUrl(),
                    contentDescription = pelicula.titulo,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(200.dp),
                    contentScale = ContentScale.Crop
                )
                Text(
                    text = pelicula.titulo,
                    style = MaterialTheme.typography.bodyMedium,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.padding(8.dp)
                )
            }
            IconButton(
                onClick = onToggleFavorito,
                modifier = Modifier.align(Alignment.TopEnd)
            ) {
                Icon(
                    imageVector = if (esFavorito) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                    contentDescription = "Favorito",
                    tint = if (esFavorito) Color.Red else Color.White
                )
            }
        }
    }
}

@Composable
fun ItemPeliculaGrande(
    pelicula: Pelicula,
    esFavorito: Boolean = false,
    onToggleFavorito: () -> Unit = {},
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .width(200.dp)
            .clickable { onClick() },
        elevation = CardDefaults.cardElevation(6.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
    ) {
        Box {
            Column {
                AsyncImage(
                    model = pelicula.getPosterUrl(),
                    contentDescription = pelicula.titulo,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(280.dp),
                    contentScale = ContentScale.Crop
                )
                Column(modifier = Modifier.padding(8.dp)) {
                    Text(
                        text = pelicula.titulo,
                        style = MaterialTheme.typography.titleMedium,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.Star, null, tint = Color(0xFFFFC107), modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = String.format("%.1f", pelicula.calificacionPromedio), 
                            style = MaterialTheme.typography.bodySmall
                        )
                    }
                }
            }
            IconButton(
                onClick = onToggleFavorito,
                modifier = Modifier.align(Alignment.TopEnd)
            ) {
                Icon(
                    imageVector = if (esFavorito) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                    contentDescription = "Favorito",
                    tint = if (esFavorito) Color.Red else Color.White
                )
            }
        }
    }
}