package com.example.agenciasoportunidades.ui.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.produceState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.example.agenciasoportunidades.device.CameraManager
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

// Muestra la foto de evidencia guardada en el teléfono.
// La imagen se lee en segundo plano (Dispatchers.IO) para no congelar la pantalla.
@Composable
fun FotoEvidencia(
    ruta: String,
    modifier: Modifier = Modifier,
    alto: Dp = 180.dp
) {
    val imagen by produceState<ImageBitmap?>(initialValue = null, ruta) {
        value = withContext(Dispatchers.IO) {
            CameraManager.cargarMiniatura(ruta)?.asImageBitmap()
        }
    }

    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(alto)
            .clip(RoundedCornerShape(12.dp)),
        contentAlignment = Alignment.Center
    ) {
        val foto = imagen
        if (foto == null) {
            CircularProgressIndicator()
        } else {
            Image(
                bitmap = foto,
                contentDescription = "Foto de evidencia del evento",
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxWidth().height(alto)
            )
        }
    }
}
