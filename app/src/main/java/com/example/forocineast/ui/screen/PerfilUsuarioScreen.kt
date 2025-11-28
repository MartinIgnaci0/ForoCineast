package com.example.forocineast.ui.screen

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.ExitToApp
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.PhotoLibrary
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import coil.compose.AsyncImage
import com.example.forocineast.viewmodel.AuthViewModel
import java.io.File
import java.io.FileOutputStream

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PerfilUsuarioScreen(
    authViewModel: AuthViewModel,
    onBack: () -> Unit,
    onCerrarSesion: () -> Unit
) {
    val usuario = authViewModel.usuarioActual
    val context = LocalContext.current

    // Estado local para mostrar la foto seleccionada inmediatamente
    var fotoUriLocal by remember { mutableStateOf<Uri?>(null) }
    
    // Si el usuario ya tiene foto remota (guardada), la usamos inicialmente si no hay local reciente
    val fotoParaMostrar = fotoUriLocal ?: (usuario?.fotoPerfilUrl?.let { Uri.parse(it) })

    // Variable temporal para guardar la URI de la foto tomada con la cámara
    var tempCameraUri by remember { mutableStateOf<Uri?>(null) }

    // 1. Lanzador para GALERÍA
    val galleryLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia()
    ) { uri ->
        if (uri != null) {
            // COPIAR A CACHÉ: Para que la foto persista y sea accesible siempre
            val archivoCopia = copiarUriAArchivo(context, uri)
            if (archivoCopia != null) {
                val uriCopia = Uri.fromFile(archivoCopia)
                fotoUriLocal = uriCopia
                authViewModel.actualizarFotoPerfil(uriCopia) // <--- GUARDAR EN MEMORIA Y DISCO
                Toast.makeText(context, "Foto de galería guardada", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(context, "Error al procesar la imagen", Toast.LENGTH_SHORT).show()
            }
        }
    }

    // 2. Lanzador para CÁMARA
    val cameraLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.TakePicture()
    ) { success ->
        if (success && tempCameraUri != null) {
            fotoUriLocal = tempCameraUri
            authViewModel.actualizarFotoPerfil(tempCameraUri!!) // <--- GUARDAR EN MEMORIA Y DISCO
            Toast.makeText(context, "Foto de cámara guardada", Toast.LENGTH_SHORT).show()
        }
    }

    // 3. Lanzador de PERMISO DE CÁMARA
    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            // Si dio permiso, lanzamos la cámara
            val file = File(context.cacheDir, "foto_perfil_${System.currentTimeMillis()}.jpg")
            val uri = FileProvider.getUriForFile(context, "${context.packageName}.fileprovider", file)
            tempCameraUri = uri
            cameraLauncher.launch(uri)
        } else {
            Toast.makeText(context, "Se requiere permiso de cámara", Toast.LENGTH_SHORT).show()
        }
    }

    // Diálogo para elegir entre Cámara o Galería
    var mostrarDialogoFoto by remember { mutableStateOf(false) }

    if (mostrarDialogoFoto) {
        AlertDialog(
            onDismissRequest = { mostrarDialogoFoto = false },
            title = { Text("Cambiar Foto de Perfil") },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                    // Opción GALERÍA
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable {
                                mostrarDialogoFoto = false
                                galleryLauncher.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
                            }
                            .padding(8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(Icons.Default.PhotoLibrary, null, tint = MaterialTheme.colorScheme.primary)
                        Spacer(modifier = Modifier.width(12.dp))
                        Text("Elegir de la Galería")
                    }

                    // Opción CÁMARA
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable {
                                mostrarDialogoFoto = false
                                
                                val permissionCheck = ContextCompat.checkSelfPermission(context, Manifest.permission.CAMERA)
                                if (permissionCheck == PackageManager.PERMISSION_GRANTED) {
                                    val file = File(context.cacheDir, "foto_perfil_${System.currentTimeMillis()}.jpg")
                                    val uri = FileProvider.getUriForFile(context, "${context.packageName}.fileprovider", file)
                                    tempCameraUri = uri
                                    cameraLauncher.launch(uri)
                                } else {
                                    permissionLauncher.launch(Manifest.permission.CAMERA)
                                }
                            }
                            .padding(8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(Icons.Default.CameraAlt, null, tint = MaterialTheme.colorScheme.primary)
                        Spacer(modifier = Modifier.width(12.dp))
                        Text("Tomar Foto")
                    }
                }
            },
            confirmButton = {
                TextButton(onClick = { mostrarDialogoFoto = false }) { Text("Cancelar") }
            }
        )
    }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("Mi Perfil") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Volver")
                    }
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background,
                    titleContentColor = MaterialTheme.colorScheme.primary
                )
            )
        }
    ) { padding ->
        if (usuario == null) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text("No hay sesión activa")
            }
        } else {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(24.dp)
            ) {
                // Avatar Grande
                Box {
                    Surface(
                        modifier = Modifier.size(120.dp),
                        shape = CircleShape,
                        color = MaterialTheme.colorScheme.primaryContainer
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            if (fotoParaMostrar != null) {
                                AsyncImage(
                                    model = fotoParaMostrar,
                                    contentDescription = null,
                                    modifier = Modifier.fillMaxSize(),
                                    contentScale = ContentScale.Crop
                                )
                            } else {
                                Icon(
                                    imageVector = Icons.Default.Person,
                                    contentDescription = null,
                                    modifier = Modifier.size(64.dp),
                                    tint = MaterialTheme.colorScheme.primary
                                )
                            }
                        }
                    }
                    IconButton(
                        onClick = { mostrarDialogoFoto = true },
                        modifier = Modifier
                            .align(Alignment.BottomEnd)
                            .background(MaterialTheme.colorScheme.secondary, CircleShape)
                    ) {
                        Icon(Icons.Default.Edit, contentDescription = "Editar Foto", tint = Color.Black)
                    }
                }

                // Información
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    elevation = CardDefaults.cardElevation(4.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        ItemInfo("Nombre", usuario.nombreCompleto)
                        HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))
                        ItemInfo("Alias", "@${usuario.alias}")
                        HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))
                        ItemInfo("Correo", usuario.correo)
                    }
                }

                Spacer(modifier = Modifier.weight(1f))

                Button(
                    onClick = onCerrarSesion,
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Icon(Icons.Default.ExitToApp, contentDescription = null)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Cerrar Sesión")
                }
            }
        }
    }
}

// Función auxiliar para copiar la imagen de galería a un archivo local
fun copiarUriAArchivo(context: Context, uri: Uri): File? {
    return try {
        val inputStream = context.contentResolver.openInputStream(uri) ?: return null
        val archivoSalida = File(context.cacheDir, "perfil_galeria_${System.currentTimeMillis()}.jpg")
        val outputStream = FileOutputStream(archivoSalida)
        inputStream.use { input ->
            outputStream.use { output ->
                input.copyTo(output)
            }
        }
        archivoSalida
    } catch (e: Exception) {
        e.printStackTrace()
        null
    }
}

@Composable
fun ItemInfo(titulo: String, valor: String) {
    Column {
        Text(
            text = titulo,
            style = MaterialTheme.typography.labelMedium,
            color = Color.Gray
        )
        Text(
            text = valor,
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.SemiBold
        )
    }
}