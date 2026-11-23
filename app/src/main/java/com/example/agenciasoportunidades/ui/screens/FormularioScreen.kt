package com.example.agenciasoportunidades.ui.screens

import android.Manifest
import android.content.pm.PackageManager
import android.graphics.BitmapFactory
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.Image
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.Badge
import androidx.compose.material.icons.filled.Business
import androidx.compose.material.icons.filled.Category
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Error
import androidx.compose.material.icons.filled.Event
import androidx.compose.material.icons.filled.Groups
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.PhotoCamera
import androidx.compose.material.icons.filled.Place
import androidx.compose.material.icons.filled.QrCodeScanner
import androidx.compose.material.icons.filled.Save
import androidx.compose.material.icons.filled.Title
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MenuAnchorType
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.produceState
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.OffsetMapping
import androidx.compose.ui.text.input.TransformedText
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.navigation.NavController
import com.example.agenciasoportunidades.model.data.local.OrganizacionEntity
import com.example.agenciasoportunidades.viewmodel.OportunidadViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

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

object FormatoFecha : VisualTransformation {
    override fun filter(text: AnnotatedString): TransformedText {
        val digitos = text.text
        val conBarras = buildString {
            digitos.forEachIndexed { i, c ->
                append(c)
                if ((i == 1 || i == 3) && i < digitos.length - 1) append('/')
            }
        }
        val mapeo = object : OffsetMapping {
            override fun originalToTransformed(offset: Int): Int = when {
                offset <= 2 -> offset
                offset <= 4 -> offset + 1
                else -> offset + 2
            }

            override fun transformedToOriginal(offset: Int): Int = when {
                offset <= 2 -> offset
                offset <= 5 -> offset - 1
                else -> offset - 2
            }
        }
        return TransformedText(AnnotatedString(conBarras), mapeo)
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FormularioScreen(
    navController: NavController,
    viewModel: OportunidadViewModel
) {
    val estado by viewModel.estado.collectAsState()
    val organizaciones by viewModel.organizaciones.collectAsState()
    val capitalizar = KeyboardOptions(capitalization = KeyboardCapitalization.Sentences)

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(if (estado.id == null) "Nueva oportunidad" else "Editar oportunidad") },
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
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .imePadding()
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            OutlinedButton(
                onClick = { navController.navigate("escanear_qr") },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 8.dp)
            ) {
                Icon(Icons.Filled.QrCodeScanner, contentDescription = null)
                Text("  Escanear QR del evento")
            }

            OutlinedTextField(
                value = estado.titulo,
                onValueChange = viewModel::onTituloChange,
                label = { Text("Título de la oportunidad *") },
                leadingIcon = { Icon(Icons.Filled.Title, contentDescription = null) },
                trailingIcon = {
                    AnimatedVisibility(
                        visible = estado.errores.titulo != null,
                        enter = scaleIn() + fadeIn(),
                        exit = scaleOut() + fadeOut()
                    ) {
                        Icon(Icons.Filled.Error, contentDescription = "Error", tint = MaterialTheme.colorScheme.error)
                    }
                },
                isError = estado.errores.titulo != null,
                supportingText = {
                    AnimatedVisibility(
                        visible = estado.errores.titulo != null,
                        enter = expandVertically() + fadeIn(),
                        exit = shrinkVertically() + fadeOut()
                    ) {
                        Text(text = estado.errores.titulo.orEmpty(), color = MaterialTheme.colorScheme.error)
                    }
                },
                keyboardOptions = capitalizar,
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )

            SelectorOrganizacion(
                valor = estado.organizacion,
                organizaciones = organizaciones,
                error = estado.errores.organizacion,
                onValorChange = viewModel::onOrganizacionChange
            )

            SelectorTipoEvento(
                seleccionado = estado.tipoEvento,
                onSeleccionar = viewModel::onTipoEventoChange
            )

            OutlinedTextField(
                value = estado.publicoObjetivo,
                onValueChange = viewModel::onPublicoObjetivoChange,
                label = { Text("Público objetivo *") },
                placeholder = { Text("Ej: trabajadores y sus familias") },
                leadingIcon = { Icon(Icons.Filled.Groups, contentDescription = null) },
                trailingIcon = {
                    AnimatedVisibility(
                        visible = estado.errores.publicoObjetivo != null,
                        enter = scaleIn() + fadeIn(),
                        exit = scaleOut() + fadeOut()
                    ) {
                        Icon(Icons.Filled.Error, contentDescription = "Error", tint = MaterialTheme.colorScheme.error)
                    }
                },
                isError = estado.errores.publicoObjetivo != null,
                supportingText = {
                    AnimatedVisibility(
                        visible = estado.errores.publicoObjetivo != null,
                        enter = expandVertically() + fadeIn(),
                        exit = shrinkVertically() + fadeOut()
                    ) {
                        Text(text = estado.errores.publicoObjetivo.orEmpty(), color = MaterialTheme.colorScheme.error)
                    }
                },
                keyboardOptions = capitalizar,
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )

            OutlinedTextField(
                value = estado.ubicacion,
                onValueChange = viewModel::onUbicacionChange,
                label = { Text("Ubicación (comuna, región) *") },
                placeholder = { Text("Ej: Puente Alto, RM") },
                leadingIcon = { Icon(Icons.Filled.Place, contentDescription = null) },
                trailingIcon = {
                    AnimatedVisibility(
                        visible = estado.errores.ubicacion != null,
                        enter = scaleIn() + fadeIn(),
                        exit = scaleOut() + fadeOut()
                    ) {
                        Icon(Icons.Filled.Error, contentDescription = "Error", tint = MaterialTheme.colorScheme.error)
                    }
                },
                isError = estado.errores.ubicacion != null,
                supportingText = {
                    AnimatedVisibility(
                        visible = estado.errores.ubicacion != null,
                        enter = expandVertically() + fadeIn(),
                        exit = shrinkVertically() + fadeOut()
                    ) {
                        Text(text = estado.errores.ubicacion.orEmpty(), color = MaterialTheme.colorScheme.error)
                    }
                },
                keyboardOptions = capitalizar,
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )

            OutlinedTextField(
                value = estado.fecha,
                onValueChange = viewModel::onFechaChange,
                label = { Text("Fecha del evento *") },
                placeholder = { Text("dd/mm/aaaa") },
                leadingIcon = { Icon(Icons.Filled.Event, contentDescription = null) },
                trailingIcon = {
                    AnimatedVisibility(
                        visible = estado.errores.fecha != null,
                        enter = scaleIn() + fadeIn(),
                        exit = scaleOut() + fadeOut()
                    ) {
                        Icon(Icons.Filled.Error, contentDescription = "Error", tint = MaterialTheme.colorScheme.error)
                    }
                },
                isError = estado.errores.fecha != null,
                supportingText = {
                    AnimatedVisibility(
                        visible = estado.errores.fecha != null,
                        enter = expandVertically() + fadeIn(),
                        exit = shrinkVertically() + fadeOut()
                    ) {
                        Text(text = estado.errores.fecha.orEmpty(), color = MaterialTheme.colorScheme.error)
                    }
                },
                visualTransformation = FormatoFecha,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )

            HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))

            Text(
                text = "Contacto principal",
                style = MaterialTheme.typography.titleMedium,
                modifier = Modifier.padding(bottom = 4.dp)
            )

            OutlinedTextField(
                value = estado.contactoNombre,
                onValueChange = viewModel::onContactoNombreChange,
                label = { Text("Nombre del contacto *") },
                leadingIcon = { Icon(Icons.Filled.Person, contentDescription = null) },
                trailingIcon = {
                    AnimatedVisibility(
                        visible = estado.errores.contactoNombre != null,
                        enter = scaleIn() + fadeIn(),
                        exit = scaleOut() + fadeOut()
                    ) {
                        Icon(Icons.Filled.Error, contentDescription = "Error", tint = MaterialTheme.colorScheme.error)
                    }
                },
                isError = estado.errores.contactoNombre != null,
                supportingText = {
                    AnimatedVisibility(
                        visible = estado.errores.contactoNombre != null,
                        enter = expandVertically() + fadeIn(),
                        exit = shrinkVertically() + fadeOut()
                    ) {
                        Text(text = estado.errores.contactoNombre.orEmpty(), color = MaterialTheme.colorScheme.error)
                    }
                },
                keyboardOptions = capitalizar,
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )

            OutlinedTextField(
                value = estado.contactoCargo,
                onValueChange = viewModel::onContactoCargoChange,
                label = { Text("Cargo") },
                leadingIcon = { Icon(Icons.Filled.Badge, contentDescription = null) },
                keyboardOptions = capitalizar,
                singleLine = true,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 8.dp)
            )

            OutlinedTextField(
                value = estado.contactoTelefono,
                onValueChange = viewModel::onContactoTelefonoChange,
                label = { Text("Teléfono") },
                leadingIcon = { Icon(Icons.Filled.Phone, contentDescription = null) },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                singleLine = true,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 8.dp)
            )

            OutlinedTextField(
                value = estado.contactoCorreo,
                onValueChange = viewModel::onContactoCorreoChange,
                label = { Text("Correo electrónico") },
                leadingIcon = { Icon(Icons.Filled.Email, contentDescription = null) },
                trailingIcon = {
                    AnimatedVisibility(
                        visible = estado.errores.contactoCorreo != null,
                        enter = scaleIn() + fadeIn(),
                        exit = scaleOut() + fadeOut()
                    ) {
                        Icon(Icons.Filled.Error, contentDescription = "Error", tint = MaterialTheme.colorScheme.error)
                    }
                },
                isError = estado.errores.contactoCorreo != null,
                supportingText = {
                    AnimatedVisibility(
                        visible = estado.errores.contactoCorreo != null,
                        enter = expandVertically() + fadeIn(),
                        exit = shrinkVertically() + fadeOut()
                    ) {
                        Text(text = estado.errores.contactoCorreo.orEmpty(), color = MaterialTheme.colorScheme.error)
                    }
                },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )

            SeccionFoto(
                fotoPath = estado.fotoPath,
                onFotoTomada = viewModel::onFotoChange
            )

            Button(
                onClick = {
                    if (viewModel.validarFormulario()) {
                        viewModel.guardar {
                            navController.popBackStack()
                        }
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 12.dp)
                    .height(52.dp)
            ) {
                Icon(Icons.Filled.Save, contentDescription = null)
                Text("  Guardar oportunidad")
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun SelectorOrganizacion(
    valor: String,
    organizaciones: List<OrganizacionEntity>,
    error: String?,
    onValorChange: (String) -> Unit
) {
    var expandido by remember { mutableStateOf(false) }

    ExposedDropdownMenuBox(
        expanded = expandido,
        onExpandedChange = { expandido = !expandido },
        modifier = Modifier.fillMaxWidth()
    ) {
        OutlinedTextField(
            value = valor,
            onValueChange = {
                onValorChange(it)
                expandido = true
            },
            label = { Text("Organización responsable *") },
            leadingIcon = { Icon(Icons.Filled.Business, contentDescription = null) },
            trailingIcon = {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    AnimatedVisibility(
                        visible = error != null,
                        enter = scaleIn() + fadeIn(),
                        exit = scaleOut() + fadeOut()
                    ) {
                        Icon(Icons.Filled.Error, contentDescription = "Error", tint = MaterialTheme.colorScheme.error)
                    }
                    ExposedDropdownMenuDefaults.TrailingIcon(expanded = expandido)
                }
            },
            isError = error != null,
            supportingText = {
                AnimatedVisibility(
                    visible = error != null,
                    enter = expandVertically() + fadeIn(),
                    exit = shrinkVertically() + fadeOut()
                ) {
                    Text(text = error.orEmpty(), color = MaterialTheme.colorScheme.error)
                }
            },
            keyboardOptions = KeyboardOptions(capitalization = KeyboardCapitalization.Sentences),
            singleLine = true,
            modifier = Modifier
                .menuAnchor(MenuAnchorType.PrimaryEditable)
                .fillMaxWidth()
        )

        ExposedDropdownMenu(
            expanded = expandido && organizaciones.isNotEmpty(),
            onDismissRequest = { expandido = false }
        ) {
            organizaciones.forEach { org ->
                DropdownMenuItem(
                    text = {
                        Column {
                            Text(org.nombre)
                            if (org.rubro.isNotBlank() || org.comuna.isNotBlank()) {
                                Text(
                                    text = listOf(org.rubro, org.comuna).filter { it.isNotBlank() }.joinToString(" - "),
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                    },
                    onClick = {
                        onValorChange(org.nombre)
                        expandido = false
                    }
                )
            }
        }
    }
}

@Composable
private fun SeccionFoto(
    fotoPath: String?,
    onFotoTomada: (String?) -> Unit
) {
    val context = LocalContext.current
    var rutaPendiente by rememberSaveable { mutableStateOf<String?>(null) }
    var permisoDenegado by rememberSaveable { mutableStateOf(false) }

    val tomarFoto = rememberLauncherForActivityResult(ActivityResultContracts.TakePicture()) { guardada ->
        if (guardada) {
            onFotoTomada(rutaPendiente)
        } else {
            rutaPendiente?.let { File(it).delete() }
        }
        rutaPendiente = null
    }

    fun abrirCamara() {
        val carpeta = File(context.filesDir, "fotos").apply { mkdirs() }
        val fecha = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())
        val archivo = File(carpeta, "EVENTO_$fecha.jpg")
        rutaPendiente = archivo.absolutePath
        tomarFoto.launch(FileProvider.getUriForFile(context, "${context.packageName}.fileprovider", archivo))
    }

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
                val tienePermiso = ContextCompat.checkSelfPermission(context, Manifest.permission.CAMERA) ==
                        PackageManager.PERMISSION_GRANTED
                if (tienePermiso) abrirCamara() else pedirPermiso.launch(Manifest.permission.CAMERA)
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Icon(Icons.Filled.PhotoCamera, contentDescription = null)
            Text(if (fotoPath == null) "  Tomar foto" else "  Tomar otra foto")
        }

        if (permisoDenegado) {
            Row {
                Icon(Icons.Filled.Warning, contentDescription = null, tint = MaterialTheme.colorScheme.error)
                Text(
                    text = "  Sin permiso de cámara no se puede tomar la foto. Puedes guardar la oportunidad sin foto o volver a intentarlo.",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.error
                )
            }
        }
    }
}

@Composable
fun FotoEvidencia(
    ruta: String,
    modifier: Modifier = Modifier,
    alto: Dp = 180.dp
) {
    val imagen by produceState<ImageBitmap?>(initialValue = null, ruta) {
        value = withContext(Dispatchers.IO) {
            val archivo = File(ruta)
            if (!archivo.exists() || archivo.length() == 0L) {
                null
            } else {
                val dimensiones = BitmapFactory.Options().apply { inJustDecodeBounds = true }
                BitmapFactory.decodeFile(ruta, dimensiones)
                var escala = 1
                while (dimensiones.outWidth / escala > 600 || dimensiones.outHeight / escala > 600) {
                    escala *= 2
                }
                BitmapFactory.decodeFile(ruta, BitmapFactory.Options().apply { inSampleSize = escala })
                    ?.asImageBitmap()
            }
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
                modifier = Modifier
                    .fillMaxWidth()
                    .height(alto)
            )
        }
    }
}

@Composable
fun SelectorTipoEvento(
    seleccionado: String,
    onSeleccionar: (String) -> Unit
) {
    var expandido by remember { mutableStateOf(false) }

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 8.dp)
    ) {
        OutlinedTextField(
            value = seleccionado,
            onValueChange = {},
            readOnly = true,
            label = { Text("Tipo de evento") },
            leadingIcon = { Icon(Icons.Filled.Category, contentDescription = null) },
            trailingIcon = { Icon(Icons.Filled.ArrowDropDown, contentDescription = "Elegir tipo") },
            modifier = Modifier.fillMaxWidth()
        )
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