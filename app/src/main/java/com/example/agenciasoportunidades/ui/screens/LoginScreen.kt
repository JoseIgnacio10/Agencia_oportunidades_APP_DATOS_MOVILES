package com.example.agenciasoportunidades.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Login
import androidx.compose.material.icons.filled.BusinessCenter
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.agenciasoportunidades.ui.components.CampoTexto
import com.example.agenciasoportunidades.viewmodel.LoginViewModel

// Pantalla de ingreso con cuenta ficticia.
// Igual que el formulario de oportunidades: muestra el estado del ViewModel y le avisa cada cambio.
@Composable
fun LoginScreen(
    viewModel: LoginViewModel,
    onLoginExitoso: (nombreUsuario: String) -> Unit
) {
    val estado by viewModel.uiState.collectAsState()

    // Cuando el ViewModel confirma la sesión, se avisa una sola vez para navegar
    LaunchedEffect(estado.usuarioNombre) {
        estado.usuarioNombre?.let(onLoginExitoso)
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .systemBarsPadding()
            .imePadding() // el teclado no tapa los campos
            .verticalScroll(rememberScrollState())
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        // Encabezado: identidad de la app
        Icon(
            Icons.Filled.BusinessCenter,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.primary,
            modifier = Modifier.size(72.dp)
        )
        Spacer(Modifier.height(12.dp))
        Text(
            text = "Agencia Oportunidades",
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold
        )
        Text(
            text = "Gestión de oportunidades y activaciones corporativas",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center
        )
        Spacer(Modifier.height(32.dp))

        CampoTexto(
            valor = estado.correo,
            onValorChange = viewModel::onCorreoChanged,
            etiqueta = "Correo corporativo",
            icono = Icons.Filled.Email,
            error = estado.correoError,
            placeholder = "nombre@empresa.cl",
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email, imeAction = ImeAction.Next)
        )
        CampoTexto(
            valor = estado.clave,
            onValorChange = viewModel::onClaveChanged,
            etiqueta = "Clave",
            icono = Icons.Filled.Lock,
            error = estado.claveError,
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password, imeAction = ImeAction.Done),
            esClave = true
        )

        // Error general: aparece solo después de consultar la base de datos
        estado.mensajeError?.let { mensaje ->
            Card(
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.errorContainer),
                modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp)
            ) {
                Row(modifier = Modifier.padding(12.dp), verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Filled.Warning, contentDescription = null, tint = MaterialTheme.colorScheme.error)
                    Text(
                        text = "  $mensaje",
                        color = MaterialTheme.colorScheme.onErrorContainer,
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
            }
        }

        // Habilitado solo si el ViewModel dice que el formulario es válido
        Button(
            onClick = viewModel::iniciarSesion,
            enabled = estado.esFormularioValido && !estado.estaCargando,
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 16.dp)
                .height(52.dp)
        ) {
            if (estado.estaCargando) {
                CircularProgressIndicator(modifier = Modifier.size(24.dp), strokeWidth = 2.dp)
            } else {
                Icon(Icons.AutoMirrored.Filled.Login, contentDescription = null)
                Text("  Ingresar")
            }
        }
    }
}
