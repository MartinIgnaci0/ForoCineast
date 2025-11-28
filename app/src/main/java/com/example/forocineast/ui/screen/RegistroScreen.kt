package com.example.forocineast.ui.screen

import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.forocineast.viewmodel.AuthViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RegistroScreen(
    onBack: () -> Unit,
    onRegistroExitoso: () -> Unit,
    viewModel: AuthViewModel // <--- PARAMETRO OBLIGATORIO
) {
    // val viewModel: AuthViewModel = viewModel() // <--- ELIMINADO
    val uiState by viewModel.uiState.collectAsState()
    val context = LocalContext.current

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Nueva Cuenta") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Volver")
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
                .padding(16.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Nombre Completo
            OutlinedTextField(
                value = uiState.nombre,
                onValueChange = { viewModel.onNombreChange(it) },
                label = { Text("Nombre Completo") },
                modifier = Modifier.fillMaxWidth(),
                isError = uiState.errorNombre != null,
                supportingText = { uiState.errorNombre?.let { Text(it) } }
            )

            // Alias (Nickname)
            OutlinedTextField(
                value = uiState.alias,
                onValueChange = { viewModel.onAliasChange(it) },
                label = { Text("Alias (Nickname)") },
                placeholder = { Text("Ej: Cinefilo10") },
                modifier = Modifier.fillMaxWidth(),
                isError = uiState.errorAlias != null,
                supportingText = { uiState.errorAlias?.let { Text(it) } }
            )

            // Correo
            OutlinedTextField(
                value = uiState.correo,
                onValueChange = { viewModel.onCorreoChange(it) },
                label = { Text("Correo Electrónico") },
                modifier = Modifier.fillMaxWidth(),
                isError = uiState.errorCorreo != null,
                supportingText = { uiState.errorCorreo?.let { Text(it) } }
            )

            // Contraseña
            OutlinedTextField(
                value = uiState.clave,
                onValueChange = { viewModel.onClaveChange(it) },
                label = { Text("Contraseña") },
                visualTransformation = PasswordVisualTransformation(),
                modifier = Modifier.fillMaxWidth(),
                isError = uiState.errorClave != null,
                supportingText = { uiState.errorClave?.let { Text(it) } }
            )

            // Confirmar Contraseña
            OutlinedTextField(
                value = uiState.confirmarClave,
                onValueChange = { viewModel.onConfirmarClaveChange(it) },
                label = { Text("Confirmar Contraseña") },
                visualTransformation = PasswordVisualTransformation(),
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(16.dp))

            Button(
                onClick = {
                    viewModel.registrar(
                        onSuccess = {
                            Toast.makeText(context, "Registro exitoso", Toast.LENGTH_LONG).show()
                            onRegistroExitoso()
                        }
                    )
                },
                modifier = Modifier.fillMaxWidth(),
                enabled = !uiState.estaCargando
            ) {
                if (uiState.estaCargando) {
                    CircularProgressIndicator(modifier = Modifier.size(24.dp))
                } else {
                    Text("REGISTRARSE")
                }
            }
        }
    }
}