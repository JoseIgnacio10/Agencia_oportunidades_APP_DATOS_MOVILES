package com.example.agenciasoportunidades.ui.screens

import android.Manifest
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.material.icons.filled.PhotoCamera
import androidx.compose.material.icons.filled.QrCodeScanner
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.OutlinedButton
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.platform.LocalContext
import com.example.agenciasoportunidades.device.CameraManager
import com.example.agenciasoportunidades.ui.components.FotoEvidencia
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.Business
import androidx.compose.material.icons.filled.Category
import androidx.compose.material.icons.filled.Event
import androidx.compose.material.icons.filled.Groups
import androidx.compose.material.icons.filled.Place
import androidx.compose.material.icons.filled.Save
import androidx.compose.material.icons.filled.Title
import androidx.compose.material3.Button
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.ui.unit.dp
import com.example.agenciasoportunidades.ui.components.CampoTexto
import com.example.agenciasoportunidades.viewmodel.OportunidadViewModel

// Tipos de oportunidad definidos en el caso (sección 3.2)
private val TIPOS_EVENTO = listOf(
    "Evento corporativo",
    "Feria empresarial",
    "Actividad municipal",
    "Jornada de beneficios",
    "Feria institucional",
    "Actividad de trabajadores",
    "Activación comercial",
    "Otra instancia relevante"
)

// Formulario de nueva oportunidad.
// Cada campo avisa su cambio al ViewModel, y el ViewModel devuelve el valor y el error a mostrar.
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FormularioScreen(
    viewModel: OportunidadViewModel,
    onGuardado: () -> Unit,
    onVolver: () -> Unit,
    onEscanearQr: () -> Unit
) {
    val form by viewModel.formState.collectAsState()
    val capitalizar = KeyboardOptions(capitalization = KeyboardCapitalization.Sentences)

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Nueva oportunidad") },
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
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .imePadding() // el teclado no tapa los campos
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            // Atajo en terreno: el QR del afiche del evento llena los campos (recurso nativo 2)
            OutlinedButton(
                onClick = onEscanearQr,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 8.dp)
            ) {
                Icon(Icons.Filled.QrCodeScanner, contentDescription = null)
                Text("  Escanear QR del evento")
            }
            CampoTexto(
                valor = form.titulo,
                onValorChange = viewModel::onTituloChanged,
                etiqueta = "Título de la oportunidad *",
                icono = Icons.Filled.Title,
                error = form.tituloError,
                keyboardOptions = capitalizar
            )
            CampoTexto(
                valor = form.organizacion,
                onValorChange = viewModel::onOrganizacionChanged,
                etiqueta = "Organización responsable *",
                icono = Icons.Filled.Business,
                error = form.organizacionError,
                keyboardOptions = capitalizar
            )
            SelectorTipoEvento(
                seleccionado = form.tipoEvento,
                onSeleccionar = viewModel::onTipoEventoChanged
            )
            CampoTexto(
                valor = form.publicoObjetivo,
                onValorChange = viewModel::onPublicoObjetivoChanged,
                etiqueta = "Público objetivo *",
                icono = Icons.Filled.Groups,
                error = form.publicoObjetivoError,
                placeholder = "Ej: trabajadores y sus familias",
                keyboardOptions = capitalizar
            )
            CampoTexto(
                valor = form.ubicacion,
                onValorChange = viewModel::onUbicacionChanged,
                etiqueta = "Ubicación (comuna, región) *",
                icono = Icons.Filled.Place,
                error = form.ubicacionError,
                placeholder = "Ej: Puente Alto, RM",
                keyboardOptions = capitalizar
            )
            CampoTexto(
                valor = form.fecha,
                onValorChange = viewModel::onFechaChanged,
                etiqueta = "Fecha del evento *",
                icono = Icons.Filled.Event,
                error = form.fechaError,
                placeholder = "dd/mm/aaaa"
            )

            SeccionFoto(
                fotoPath = form.fotoPath,
                fotoError = form.fotoError,
                onFotoTomada = viewModel::onFotoPathChanged
            )

            // El botón se habilita solo cuando el ViewModel dice que el formulario es válido
            Button(
                onClick = { viewModel.guardarOportunidad(onExito = onGuardado) },
                enabled = form.esFormularioValido,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 12.dp)
                    .height(52.dp)
            ) {
                Icon(Icons.Filled.Save, contentDescription = null)
                Text("  Guardar oportunidad")
            }
            if (!form.esFormularioValido) {
                Text(
                    text = "Completa todos los campos marcados con * para guardar.",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

// Foto de evidencia con la cámara (recurso nativo 1).
// Flujo: revisar permiso → pedirlo si falta → abrir la cámara → avisar la ruta al ViewModel.
@Composable
private fun SeccionFoto(
    fotoPath: String?,
    fotoError: String?,
    onFotoTomada: (String?) -> Unit
) {
    val context = LocalContext.current
    val cameraManager = remember(context) { CameraManager(context) }
    // rememberSaveable: la ruta sobrevive si Android recrea la pantalla mientras la cámara está abierta
    var rutaPendiente by rememberSaveable { mutableStateOf<String?>(null) }
    var permisoDenegado by rememberSaveable { mutableStateOf(false) }

    // Abre la app de cámara y espera el resultado (true = foto guardada)
    val tomarFoto = rememberLauncherForActivityResult(ActivityResultContracts.TakePicture()) { guardada ->
        if (guardada) {
            onFotoTomada(rutaPendiente)
        } else {
            cameraManager.eliminarFoto(rutaPendiente) // canceló: el archivo vacío no sirve
        }
        rutaPendiente = null
    }

    fun abrirCamara() {
        val archivo = cameraManager.crearArchivoFoto()
        rutaPendiente = archivo.absolutePath
        tomarFoto.launch(cameraManager.obtenerUri(archivo))
    }

    // Muestra el diálogo de permiso de Android
    val pedirPermiso = rememberLauncherForActivityResult(ActivityResultContracts.RequestPermission()) { concedido ->
        permisoDenegado = !concedido
        if (concedido) abrirCamara()
    }

    Column(verticalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.padding(top = 8.dp)) {
        Text("Foto de evidencia (opcional)", style = MaterialTheme.typography.titleSmall)

        if (fotoPath != null) {
            FotoEvidencia(ruta = fotoPath)
        }

        OutlinedButton(
            onClick = {
                if (cameraManager.tienePermisoCamara()) abrirCamara()
                else pedirPermiso.launch(Manifest.permission.CAMERA)
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Icon(Icons.Filled.PhotoCamera, contentDescription = null)
            Text(if (fotoPath == null) "  Tomar foto" else "  Tomar otra foto")
        }

        // Retroalimentación si el usuario rechazó el permiso: la app sigue funcionando sin foto
        if (permisoDenegado) {
            MensajeAviso("Sin permiso de cámara no se puede tomar la foto. Puedes guardar la oportunidad sin foto o volver a intentarlo.")
        }
        if (fotoError != null) {
            MensajeAviso(fotoError)
        }
    }
}

@Composable
private fun MensajeAviso(texto: String) {
    Row {
        Icon(
            Icons.Filled.Warning,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.error
        )
        Text(
            text = "  $texto",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.error
        )
    }
}

// Lista desplegable con los tipos de evento del caso
@Composable
private fun SelectorTipoEvento(
    seleccionado: String,
    onSeleccionar: (String) -> Unit
) {
    var expandido by remember { mutableStateOf(false) }

    Box(modifier = Modifier.fillMaxWidth().padding(bottom = 8.dp)) {
        OutlinedTextField(
            value = seleccionado,
            onValueChange = {},
            readOnly = true,
            label = { Text("Tipo de evento") },
            leadingIcon = { Icon(Icons.Filled.Category, contentDescription = null) },
            trailingIcon = { Icon(Icons.Filled.ArrowDropDown, contentDescription = "Elegir tipo") },
            modifier = Modifier.fillMaxWidth()
        )
        // Capa transparente encima: el campo es de solo lectura y así todo el campo abre el menú
        Box(
            modifier = Modifier
                .matchParentSize()
                .clickable { expandido = true }
        )
        DropdownMenu(expanded = expandido, onDismissRequest = { expandido = false }) {
            TIPOS_EVENTO.forEach { tipo ->
                DropdownMenuItem(
                    text = { Text(tipo) },
                    onClick = {
                        onSeleccionar(tipo)
                        expandido = false
                    }
                )
            }
        }
    }
}
