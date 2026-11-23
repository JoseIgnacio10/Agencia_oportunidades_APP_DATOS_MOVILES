package com.example.agenciasoportunidades.ui.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.AddComment
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Event
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.AssistChip
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.RadioButton
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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.agenciasoportunidades.model.data.local.ActivacionEntity
import com.example.agenciasoportunidades.model.data.local.ContactoEntity
import com.example.agenciasoportunidades.model.data.local.GestionEntity
import com.example.agenciasoportunidades.model.data.local.ResultadoEntity
import com.example.agenciasoportunidades.viewmodel.LoginViewModel
import com.example.agenciasoportunidades.viewmodel.OportunidadViewModel

private val ESTADOS = listOf("Pendiente", "En gestión", "Confirmada", "Descartada")
private val TIPOS_GESTION = listOf("Llamada", "Reunión", "Correo", "Visita")

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DetalleScreen(
    navController: NavController,
    viewModel: OportunidadViewModel,
    loginViewModel: LoginViewModel
) {
    val oportunidad by viewModel.seleccionada.collectAsState()
    val sesion by loginViewModel.estado.collectAsState()
    val contactos by viewModel.contactos.collectAsState()
    val gestiones by viewModel.gestiones.collectAsState()
    val activacion by viewModel.activacion.collectAsState()
    val resultado by viewModel.resultado.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Detalle de oportunidad") },
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
        val op = oportunidad
        if (op == null) {
            Text(
                text = "No hay una oportunidad seleccionada",
                modifier = Modifier
                    .padding(padding)
                    .padding(16.dp)
            )
        } else {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .imePadding()
                    .verticalScroll(rememberScrollState())
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                op.fotoPath?.let { FotoEvidencia(ruta = it) }
                Text(op.titulo, style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold)
                AssistChip(onClick = {}, label = { Text(op.tipoEvento) })
                Text("Organización: ${op.organizacion}")
                Text("Público objetivo: ${op.publicoObjetivo}")
                Text("Ubicación: ${op.ubicacion}")
                Text("Fecha: ${op.fecha}")

                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.secondaryContainer)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text("Estado actual", style = MaterialTheme.typography.labelLarge)
                        Text(op.estado, style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
                    }
                }

                if (sesion.esSupervisor) {
                    HorizontalDivider()
                    Text("Gestionar estado", style = MaterialTheme.typography.titleMedium)
                    ESTADOS.forEach { estado ->
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { viewModel.cambiarEstado(estado) }
                        ) {
                            RadioButton(
                                selected = op.estado == estado,
                                onClick = { viewModel.cambiarEstado(estado) }
                            )
                            Text(estado)
                        }
                    }
                }

                HorizontalDivider()
                SeccionContactoDetalle(contactos = contactos)

                HorizontalDivider()
                SeccionGestionesDetalle(
                    gestiones = gestiones,
                    puedeRegistrar = sesion.esEjecutivo || sesion.esSupervisor,
                    usuarioNombre = sesion.usuarioNombre.orEmpty(),
                    onRegistrarGestion = viewModel::registrarGestion
                )

                val mostrarActivacion = (sesion.esActivaciones && op.estado == "Confirmada") ||
                        (!sesion.esActivaciones && activacion != null)

                if (mostrarActivacion) {
                    HorizontalDivider()
                    SeccionActivacionDetalle(
                        activacion = activacion,
                        resultado = resultado,
                        esActivaciones = sesion.esActivaciones,
                        responsablePorDefecto = sesion.usuarioNombre.orEmpty(),
                        onProgramarActivacion = viewModel::programarActivacion,
                        onMarcarRealizada = viewModel::marcarActivacionRealizada,
                        onRegistrarResultado = viewModel::registrarResultado
                    )
                }
            }
        }
    }
}

@Composable
private fun SeccionContactoDetalle(contactos: List<ContactoEntity>) {
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Text("Contacto", style = MaterialTheme.typography.titleMedium)
        if (contactos.isEmpty()) {
            Text(
                text = "Sin contacto registrado",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        } else {
            contactos.forEach { contacto ->
                Card(modifier = Modifier.fillMaxWidth()) {
                    Column(
                        modifier = Modifier.padding(12.dp),
                        verticalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Text(contacto.nombre, fontWeight = FontWeight.Bold)
                        if (contacto.cargo.isNotBlank()) {
                            Text("Cargo: ${contacto.cargo}", style = MaterialTheme.typography.bodyMedium)
                        }
                        if (contacto.telefono.isNotBlank()) {
                            Text("Teléfono: ${contacto.telefono}", style = MaterialTheme.typography.bodyMedium)
                        }
                        if (contacto.correo.isNotBlank()) {
                            Text("Correo: ${contacto.correo}", style = MaterialTheme.typography.bodyMedium)
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun SeccionGestionesDetalle(
    gestiones: List<GestionEntity>,
    puedeRegistrar: Boolean,
    usuarioNombre: String,
    onRegistrarGestion: (String, String, String) -> Boolean
) {
    var tipoSeleccionado by remember { mutableStateOf(TIPOS_GESTION.first()) }
    var descripcion by remember { mutableStateOf("") }
    var errorDescripcion by remember { mutableStateOf<String?>(null) }

    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Text("Seguimiento y gestiones", style = MaterialTheme.typography.titleMedium)

        if (puedeRegistrar) {
            Card(modifier = Modifier.fillMaxWidth()) {
                Column(
                    modifier = Modifier.padding(12.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text("Registrar gestión", style = MaterialTheme.typography.titleSmall)
                    TIPOS_GESTION.forEach { tipo ->
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { tipoSeleccionado = tipo }
                        ) {
                            RadioButton(
                                selected = tipoSeleccionado == tipo,
                                onClick = { tipoSeleccionado = tipo }
                            )
                            Text(tipo)
                        }
                    }
                    OutlinedTextField(
                        value = descripcion,
                        onValueChange = {
                            descripcion = it
                            errorDescripcion = null
                        },
                        label = { Text("Descripción de la gestión *") },
                        isError = errorDescripcion != null,
                        supportingText = {
                            errorDescripcion?.let {
                                Text(it, color = MaterialTheme.colorScheme.error)
                            }
                        },
                        keyboardOptions = KeyboardOptions(capitalization = KeyboardCapitalization.Sentences),
                        modifier = Modifier.fillMaxWidth()
                    )
                    Button(
                        onClick = {
                            val ok = onRegistrarGestion(tipoSeleccionado, descripcion, usuarioNombre)
                            if (ok) {
                                descripcion = ""
                                errorDescripcion = null
                            } else {
                                errorDescripcion = "Ingresa una descripción"
                            }
                        },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Icon(Icons.Filled.AddComment, contentDescription = null)
                        Text("  Guardar gestión")
                    }
                }
            }
        }

        if (gestiones.isEmpty()) {
            Text(
                text = "Aún no hay gestiones registradas",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        } else {
            gestiones.forEach { gestion ->
                Card(modifier = Modifier.fillMaxWidth()) {
                    Column(
                        modifier = Modifier.padding(12.dp),
                        verticalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Text(
                            text = "${gestion.tipo} · ${gestion.fecha}",
                            style = MaterialTheme.typography.labelLarge,
                            color = MaterialTheme.colorScheme.primary
                        )
                        Text(gestion.descripcion, style = MaterialTheme.typography.bodyMedium)
                        if (gestion.usuario.isNotBlank()) {
                            Text(
                                text = "Registrado por: ${gestion.usuario}",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun SeccionActivacionDetalle(
    activacion: ActivacionEntity?,
    resultado: ResultadoEntity?,
    esActivaciones: Boolean,
    responsablePorDefecto: String,
    onProgramarActivacion: (String, String) -> Boolean,
    onMarcarRealizada: () -> Unit,
    onRegistrarResultado: (String, String, String) -> Boolean
) {
    var fecha by remember { mutableStateOf("") }
    var responsable by remember(responsablePorDefecto) { mutableStateOf(responsablePorDefecto) }
    var errorActivacion by remember { mutableStateOf<String?>(null) }

    var asistentes by remember { mutableStateOf("") }
    var leads by remember { mutableStateOf("") }
    var comentario by remember { mutableStateOf("") }
    var errorResultado by remember { mutableStateOf<String?>(null) }

    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Text("Activación", style = MaterialTheme.typography.titleMedium)

        if (activacion == null) {
            if (esActivaciones) {
                Card(modifier = Modifier.fillMaxWidth()) {
                    Column(
                        modifier = Modifier.padding(12.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Text("Programar activación", style = MaterialTheme.typography.titleSmall)
                        OutlinedTextField(
                            value = fecha,
                            onValueChange = {
                                fecha = it.filter { c -> c.isDigit() }.take(8)
                                errorActivacion = null
                            },
                            label = { Text("Fecha de activación *") },
                            placeholder = { Text("dd/mm/aaaa") },
                            leadingIcon = { Icon(Icons.Filled.Event, contentDescription = null) },
                            visualTransformation = FormatoFecha,
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            singleLine = true,
                            modifier = Modifier.fillMaxWidth()
                        )
                        OutlinedTextField(
                            value = responsable,
                            onValueChange = {
                                responsable = it
                                errorActivacion = null
                            },
                            label = { Text("Responsable *") },
                            leadingIcon = { Icon(Icons.Filled.Person, contentDescription = null) },
                            keyboardOptions = KeyboardOptions(capitalization = KeyboardCapitalization.Sentences),
                            singleLine = true,
                            modifier = Modifier.fillMaxWidth()
                        )
                        errorActivacion?.let {
                            Text(it, color = MaterialTheme.colorScheme.error, style = MaterialTheme.typography.bodySmall)
                        }
                        Button(
                            onClick = {
                                val ok = onProgramarActivacion(fecha, responsable)
                                if (ok) {
                                    fecha = ""
                                    errorActivacion = null
                                } else {
                                    errorActivacion = "Ingresa una fecha válida (dd/mm/aaaa) y un responsable"
                                }
                            },
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text("Programar activación")
                        }
                    }
                }
            }
        } else {
            Card(modifier = Modifier.fillMaxWidth()) {
                Column(
                    modifier = Modifier.padding(12.dp),
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Text(
                        text = "Estado de activación: ${activacion.estado}",
                        style = MaterialTheme.typography.labelLarge,
                        color = MaterialTheme.colorScheme.primary
                    )
                    Text("Fecha: ${activacion.fecha}")
                    Text("Responsable: ${activacion.responsable}")

                    if (esActivaciones && activacion.estado == "Programada") {
                        Spacer(Modifier.size(4.dp))
                        OutlinedButton(
                            onClick = onMarcarRealizada,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Icon(Icons.Filled.CheckCircle, contentDescription = null)
                            Text("  Marcar como realizada")
                        }
                    }
                }
            }

            if (activacion.estado == "Realizada") {
                if (resultado != null) {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.tertiaryContainer)
                    ) {
                        Column(
                            modifier = Modifier.padding(12.dp),
                            verticalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            Text("Resultado de la activación", style = MaterialTheme.typography.titleSmall)
                            Text("Asistentes reales: ${resultado.asistentesReales}")
                            Text("Leads obtenidos: ${resultado.leads}")
                            if (resultado.comentario.isNotBlank()) {
                                Text("Comentario: ${resultado.comentario}")
                            }
                        }
                    }
                } else if (esActivaciones) {
                    Card(modifier = Modifier.fillMaxWidth()) {
                        Column(
                            modifier = Modifier.padding(12.dp),
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Text("Registrar resultado", style = MaterialTheme.typography.titleSmall)
                            OutlinedTextField(
                                value = asistentes,
                                onValueChange = {
                                    asistentes = it.filter { c -> c.isDigit() }
                                    errorResultado = null
                                },
                                label = { Text("Asistentes reales *") },
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                singleLine = true,
                                modifier = Modifier.fillMaxWidth()
                            )
                            OutlinedTextField(
                                value = leads,
                                onValueChange = {
                                    leads = it.filter { c -> c.isDigit() }
                                    errorResultado = null
                                },
                                label = { Text("Leads obtenidos *") },
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                singleLine = true,
                                modifier = Modifier.fillMaxWidth()
                            )
                            OutlinedTextField(
                                value = comentario,
                                onValueChange = { comentario = it },
                                label = { Text("Comentario") },
                                keyboardOptions = KeyboardOptions(capitalization = KeyboardCapitalization.Sentences),
                                modifier = Modifier.fillMaxWidth()
                            )
                            errorResultado?.let {
                                Text(it, color = MaterialTheme.colorScheme.error, style = MaterialTheme.typography.bodySmall)
                            }
                            Button(
                                onClick = {
                                    val ok = onRegistrarResultado(asistentes, leads, comentario)
                                    if (ok) {
                                        asistentes = ""
                                        leads = ""
                                        comentario = ""
                                        errorResultado = null
                                    } else {
                                        errorResultado = "Ingresa números válidos para asistentes y leads"
                                    }
                                },
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Text("Guardar resultado")
                            }
                        }
                    }
                }
            }
        }
    }
}