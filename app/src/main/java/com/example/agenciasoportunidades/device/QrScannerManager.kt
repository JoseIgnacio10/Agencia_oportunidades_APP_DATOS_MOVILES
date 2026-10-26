package com.example.agenciasoportunidades.device

import android.content.Context
import androidx.annotation.OptIn
import androidx.camera.core.CameraSelector
import androidx.camera.core.ExperimentalGetImage
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.ImageProxy
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.core.content.ContextCompat
import androidx.lifecycle.LifecycleOwner
import com.google.mlkit.vision.barcode.BarcodeScannerOptions
import com.google.mlkit.vision.barcode.BarcodeScanning
import com.google.mlkit.vision.barcode.common.Barcode
import com.google.mlkit.vision.common.InputImage
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors
import java.util.concurrent.atomic.AtomicBoolean

// Recurso nativo 2: lector de códigos QR.
// CameraX muestra la cámara en vivo y entrega cada imagen; ML Kit busca un QR en ella.
// La pantalla solo entrega dónde dibujar la cámara y recibe el texto leído.
class QrScannerManager(context: Context) {

    private val appContext = context.applicationContext

    // Analizar imágenes es trabajo pesado: se hace en un hilo aparte para no congelar la pantalla
    private val ejecutor: ExecutorService = Executors.newSingleThreadExecutor()

    // ML Kit configurado para buscar SOLO códigos QR (más rápido que buscar todos los formatos)
    private val lector = BarcodeScanning.getClient(
        BarcodeScannerOptions.Builder()
            .setBarcodeFormats(Barcode.FORMAT_QR_CODE)
            .build()
    )

    // Evita avisar dos veces el mismo QR (la cámara analiza varias imágenes por segundo)
    private val qrDetectado = AtomicBoolean(false)

    private var proveedorCamara: ProcessCameraProvider? = null

    // Enciende la cámara ligada al ciclo de vida de la pantalla:
    // si la pantalla se cierra o pasa a segundo plano, CameraX apaga la cámara solo.
    fun iniciar(
        lifecycleOwner: LifecycleOwner,
        previewView: PreviewView,
        onQrLeido: (String) -> Unit
    ) {
        qrDetectado.set(false)
        val futuro = ProcessCameraProvider.getInstance(appContext)
        futuro.addListener({
            val proveedor = futuro.get()
            proveedorCamara = proveedor

            // Vista previa: lo que ve la cámara se dibuja en el PreviewView
            val vistaPrevia = Preview.Builder().build().also {
                it.setSurfaceProvider(previewView.surfaceProvider)
            }

            // Análisis: KEEP_ONLY_LATEST descarta imágenes viejas si ML Kit va más lento que la cámara
            val analisis = ImageAnalysis.Builder()
                .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
                .build()
            analisis.setAnalyzer(ejecutor) { imagen -> analizar(imagen, onQrLeido) }

            // Cámara trasera; si el equipo no tiene (algunos emuladores), usa la frontal
            val camara = if (proveedor.hasCamera(CameraSelector.DEFAULT_BACK_CAMERA)) {
                CameraSelector.DEFAULT_BACK_CAMERA
            } else {
                CameraSelector.DEFAULT_FRONT_CAMERA
            }

            proveedor.unbindAll()
            proveedor.bindToLifecycle(lifecycleOwner, camara, vistaPrevia, analisis)
        }, ContextCompat.getMainExecutor(appContext))
    }

    @OptIn(ExperimentalGetImage::class)
    private fun analizar(imagenCamara: ImageProxy, onQrLeido: (String) -> Unit) {
        val imagen = imagenCamara.image
        if (imagen == null || qrDetectado.get()) {
            imagenCamara.close()
            return
        }
        // La rotación importa: el QR puede verse girado según cómo se sostenga el teléfono
        val entrada = InputImage.fromMediaImage(imagen, imagenCamara.imageInfo.rotationDegrees)
        lector.process(entrada)
            .addOnSuccessListener { codigos ->
                val texto = codigos.firstOrNull { it.rawValue != null }?.rawValue
                if (texto != null && qrDetectado.compareAndSet(false, true)) {
                    onQrLeido(texto)
                }
            }
            // Siempre liberar la imagen; si no, CameraX deja de entregar nuevas
            .addOnCompleteListener { imagenCamara.close() }
    }

    // Apaga la cámara y libera ML Kit al salir de la pantalla de escaneo
    fun detener() {
        proveedorCamara?.unbindAll()
        proveedorCamara = null
        ejecutor.shutdown()
        lector.close()
    }
}
