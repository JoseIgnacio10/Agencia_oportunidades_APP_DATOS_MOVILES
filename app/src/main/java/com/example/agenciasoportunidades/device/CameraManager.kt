package com.example.agenciasoportunidades.device

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import java.io.File
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

// Recurso nativo 1: cámara.
// Concentra todo lo que depende del hardware y de los archivos, para que las pantallas
// solo pidan "dame un archivo para la foto" sin saber cómo se guarda.
class CameraManager(context: Context) {

    private val appContext = context.applicationContext

    // La app ya declaró CAMERA en el Manifest, así que Android exige pedirlo en tiempo de ejecución
    fun tienePermisoCamara(): Boolean =
        ContextCompat.checkSelfPermission(appContext, Manifest.permission.CAMERA) ==
                PackageManager.PERMISSION_GRANTED

    // Archivo vacío donde la app de cámara guardará la foto.
    // Va en la carpeta privada de la app: otras apps no la ven y se borra al desinstalar.
    fun crearArchivoFoto(): File {
        val carpeta = File(appContext.filesDir, "fotos").apply { mkdirs() }
        val fecha = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())
        return File(carpeta, "EVENTO_$fecha.jpg")
    }

    // URI content:// que se entrega a la app de cámara (debe coincidir con el provider del Manifest)
    fun obtenerUri(archivo: File): Uri =
        FileProvider.getUriForFile(appContext, "${appContext.packageName}.fileprovider", archivo)

    // Si el usuario cancela la cámara, el archivo vacío no sirve
    fun eliminarFoto(ruta: String?) {
        if (ruta != null) File(ruta).delete()
    }

    companion object {
        // Carga la foto reducida para mostrarla en pantalla sin gastar mucha memoria
        fun cargarMiniatura(ruta: String, ladoMaximo: Int = 600): Bitmap? {
            val archivo = File(ruta)
            if (!archivo.exists() || archivo.length() == 0L) return null
            // 1° lectura: solo las dimensiones, sin cargar la imagen
            val dimensiones = BitmapFactory.Options().apply { inJustDecodeBounds = true }
            BitmapFactory.decodeFile(ruta, dimensiones)
            var escala = 1
            while (dimensiones.outWidth / escala > ladoMaximo || dimensiones.outHeight / escala > ladoMaximo) {
                escala *= 2
            }
            // 2° lectura: la imagen ya reducida
            return BitmapFactory.decodeFile(ruta, BitmapFactory.Options().apply { inSampleSize = escala })
        }
    }
}
