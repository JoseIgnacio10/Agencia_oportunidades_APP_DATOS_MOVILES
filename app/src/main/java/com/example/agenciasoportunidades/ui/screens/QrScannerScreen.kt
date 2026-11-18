package com.example.agenciasoportunidades.ui.screens

import android.Manifest
import android.content.pm.PackageManager
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.camera.core.CameraSelector
import androidx.camera.core.ExperimentalGetImage
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
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
import androidx.core.content.ContextCompat
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.navigation.NavController
import com.example.agenciasoportunidades.viewmodel.OportunidadViewModel
import com.google.mlkit.vision.barcode.BarcodeScannerOptions
import com.google.mlkit.vision.barcode.BarcodeScanning
import com.google.mlkit.vision.barcode.common.Barcode
import com.google.mlkit.vision.common.InputImage
import java.util.concurrent.Executors
import java.util.concurrent.atomic.AtomicBoolean

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun QrScannerScreen(
    navController: NavController,
    viewModel: OportunidadViewModel
) {
    val context = LocalContext.current
    var tienePermiso by remember {
        mutableStateOf(
            ContextCompat.checkSelfPermission(context, Manifest.permission.CAMERA) ==
                    PackageManager.PERMISSION_GRANTED
        )
    }
    var permisoDenegado by rememberSaveable { mutableStateOf(false) }

    val pedirPermiso = rememberLauncherForActivityResult(ActivityResultContracts.RequestPermission()) { concedido ->
        tienePermiso = concedido
        permisoDenegado = !concedido
    }

    LaunchedEffect(Unit) {
        if (!tienePermiso) pedirPermiso.launch(Manifest.permission.CAMERA)
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Escanear QR del evento") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
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
            if (tienePermiso) {
                VistaCamaraQr(
                    onQrLeido = { texto ->
                        val cargado = viewModel.cargarDesdeQr(texto)
                        val mensaje = if (cargado) "Datos del evento cargados desde el QR"
                        else "Este QR no corresponde a un evento"
                        Toast.makeText(context, mensaje, Toast.LENGTH_SHORT).show()
                        navController.popBackStack()
                    }
                )
            } else if (permisoDenegado) {
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
                    Button(onClick = { pedirPermiso.launch(Manifest.permission.CAMERA) }) {
                        Text("Permitir cámara")
                    }
                }
            }
        }
    }
}

@androidx.annotation.OptIn(ExperimentalGetImage::class)
@Composable
private fun VistaCamaraQr(onQrLeido: (String) -> Unit) {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current
    val avisoActual by rememberUpdatedState(onQrLeido)

    val previewView = remember(context) {
        PreviewView(context).apply {
            scaleType = PreviewView.ScaleType.FILL_CENTER
            implementationMode = PreviewView.ImplementationMode.COMPATIBLE
        }
    }

    DisposableEffect(lifecycleOwner, previewView) {
        val ejecutor = Executors.newSingleThreadExecutor()
        val lector = BarcodeScanning.getClient(
            BarcodeScannerOptions.Builder()
                .setBarcodeFormats(Barcode.FORMAT_QR_CODE)
                .build()
        )
        val qrDetectado = AtomicBoolean(false)
        var proveedorCamara: ProcessCameraProvider? = null

        val futuro = ProcessCameraProvider.getInstance(context)
        futuro.addListener({
            val proveedor = futuro.get()
            proveedorCamara = proveedor

            val vistaPrevia = Preview.Builder().build().also {
                it.setSurfaceProvider(previewView.surfaceProvider)
            }

            val analisis = ImageAnalysis.Builder()
                .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
                .build()

            analisis.setAnalyzer(ejecutor) { imagenCamara ->
                val imagen = imagenCamara.image
                if (imagen == null || qrDetectado.get()) {
                    imagenCamara.close()
                } else {
                    val entrada = InputImage.fromMediaImage(imagen, imagenCamara.imageInfo.rotationDegrees)
                    lector.process(entrada)
                        .addOnSuccessListener { codigos ->
                            val texto = codigos.firstOrNull { it.rawValue != null }?.rawValue
                            if (texto != null && qrDetectado.compareAndSet(false, true)) {
                                avisoActual(texto)
                            }
                        }
                        .addOnCompleteListener { imagenCamara.close() }
                }
            }

            val camara = if (proveedor.hasCamera(CameraSelector.DEFAULT_BACK_CAMERA)) {
                CameraSelector.DEFAULT_BACK_CAMERA
            } else {
                CameraSelector.DEFAULT_FRONT_CAMERA
            }

            proveedor.unbindAll()
            proveedor.bindToLifecycle(lifecycleOwner, camara, vistaPrevia, analisis)
        }, ContextCompat.getMainExecutor(context))

        onDispose {
            proveedorCamara?.unbindAll()
            ejecutor.shutdown()
            lector.close()
        }
    }

    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        AndroidView(factory = { previewView }, modifier = Modifier.fillMaxSize())
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
