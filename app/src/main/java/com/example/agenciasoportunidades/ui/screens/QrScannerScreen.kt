package com.example.agenciasoportunidades.ui.screens

import android.Manifest
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.camera.view.PreviewView
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.NoPhotography
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.lifecycle.compose.LocalLifecycleOwner
import com.example.agenciasoportunidades.device.CameraManager
import com.example.agenciasoportunidades.device.QrScannerManager

// Pantalla de escaneo: pide el permiso, muestra la cámara en vivo y entrega el texto del QR.
// No interpreta el QR: eso lo hace el ViewModel (cargarDesdeQr).
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun QrScannerScreen(
    onQrLeido: (String) -> Unit,
    onVolver: () -> Unit
) {
    val context = LocalContext.current
    val cameraManager = remember(context) { CameraManager(context) }
    var tienePermiso by remember { mutableStateOf(cameraManager.tienePermisoCamara()) }
    var permisoDenegado by rememberSaveable { mutableStateOf(false) }

    val pedirPermiso = rememberLauncherForActivityResult(ActivityResultContracts.RequestPermission()) { concedido ->
        tienePermiso = concedido
        permisoDenegado = !concedido
    }

    // Al entrar a la pantalla, si falta el permiso se pide de inmediato
    LaunchedEffect(Unit) {
        if (!tienePermiso) pedirPermiso.launch(Manifest.permission.CAMERA)
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Escanear QR del evento") },
                navigationIcon = {
                    IconButton(onClick = onVolver) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Volver")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer
                )
            )
        }
    ) { padding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            when {
                tienePermiso -> VistaCamaraQr(onQrLeido = onQrLeido)
                permisoDenegado -> PermisoDenegado(
                    onReintentar = { pedirPermiso.launch(Manifest.permission.CAMERA) }
                )
            }
        }
    }
}

@Composable
private fun VistaCamaraQr(onQrLeido: (String) -> Unit) {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current
    // rememberUpdatedState: si cambia la función de aviso, no hace falta reiniciar la cámara
    val avisoActual by rememberUpdatedState(onQrLeido)

    val previewView = remember(context) {
        PreviewView(context).apply {
            scaleType = PreviewView.ScaleType.FILL_CENTER
            // COMPATIBLE: funciona mejor en emuladores
            implementationMode = PreviewView.ImplementationMode.COMPATIBLE
        }
    }

    // Enciende la cámara al mostrar la vista y la apaga al salir (onDispose)
    DisposableEffect(lifecycleOwner, previewView) {
        val lectorQr = QrScannerManager(context)
        lectorQr.iniciar(lifecycleOwner, previewView) { texto -> avisoActual(texto) }
        onDispose { lectorQr.detener() }
    }

    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        // PreviewView es una vista clásica de Android: AndroidView la inserta dentro de Compose
        AndroidView(factory = { previewView }, modifier = Modifier.fillMaxSize())

        // Marco guía para que el usuario centre el QR
        Box(
            modifier = Modifier
                .size(240.dp)
                .border(4.dp, Color.White, RoundedCornerShape(16.dp))
        )
        Surface(
            color = Color.Black.copy(alpha = 0.6f),
            shape = RoundedCornerShape(12.dp),
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(24.dp)
        ) {
            Text(
                text = "Apunta al código QR del afiche o folleto del evento",
                color = Color.White,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(12.dp)
            )
        }
    }
}

// Retroalimentación cuando el usuario rechaza el permiso: explica y permite reintentar
@Composable
private fun PermisoDenegado(onReintentar: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Icon(
            Icons.Filled.NoPhotography,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.error,
            modifier = Modifier.size(64.dp)
        )
        Spacer(Modifier.size(12.dp))
        Text(
            text = "Para leer el QR la app necesita usar la cámara. Puedes volver y completar el formulario a mano.",
            textAlign = TextAlign.Center,
            style = MaterialTheme.typography.bodyLarge
        )
        Spacer(Modifier.size(16.dp))
        Button(onClick = onReintentar) { Text("Permitir cámara") }
    }
}
