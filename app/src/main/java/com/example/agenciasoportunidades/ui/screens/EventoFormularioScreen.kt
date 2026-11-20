package com.example.agenciasoportunidades.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Business
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.Error
import androidx.compose.material.icons.filled.Event
import androidx.compose.material.icons.filled.Groups
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Map
import androidx.compose.material.icons.filled.Place
import androidx.compose.material.icons.filled.Save
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material.icons.filled.Title
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Button
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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.agenciasoportunidades.viewmodel.EventoViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EventoFormularioScreen(
    navController: NavController,
    viewModel: EventoViewModel
) {
    val estado by viewModel.estado.collectAsState()
    val capitalizar = KeyboardOptions(capitalization = KeyboardCapitalization.Sentences)
    val numerico = KeyboardOptions(keyboardType = KeyboardType.Number)

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(if (estado.id == null) "Nuevo evento" else "Editar evento") },
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
            CampoEvento(estado.nombre, viewModel::onNombreChange, "Nombre del evento *", Icons.Filled.Title, estado.errores.nombre, keyboardOptions = capitalizar)
            SelectorTipoEvento(seleccionado = estado.tipo, onSeleccionar = viewModel::onTipoChange)
            CampoEvento(estado.descripcion, viewModel::onDescripcionChange, "Descripción", Icons.Filled.Description, keyboardOptions = capitalizar)
            CampoEvento(estado.fecha, viewModel::onFechaChange, "Fecha *", Icons.Filled.Event, estado.errores.fecha, "dd/mm/aaaa", numerico, FormatoFecha)
            CampoEvento(estado.horario, viewModel::onHorarioChange, "Horario", Icons.Filled.Schedule, placeholder = "Ej: 10:00 - 18:00")
            CampoEvento(estado.comuna, viewModel::onComunaChange, "Comuna *", Icons.Filled.Place, estado.errores.comuna, keyboardOptions = capitalizar)
            CampoEvento(estado.region, viewModel::onRegionChange, "Región *", Icons.Filled.Map, estado.errores.region, keyboardOptions = capitalizar)
            CampoEvento(estado.organizacion, viewModel::onOrganizacionChange, "Organización responsable *", Icons.Filled.Business, estado.errores.organizacion, keyboardOptions = capitalizar)
            CampoEvento(estado.asistentesEstimados, viewModel::onAsistentesChange, "Asistentes estimados", Icons.Filled.Groups, keyboardOptions = numerico)
            CampoEvento(estado.condiciones, viewModel::onCondicionesChange, "Condiciones de participación", Icons.Filled.Info, keyboardOptions = capitalizar)

            AnimatedVisibility(
                visible = estado.mensajeError != null,
                enter = expandVertically() + fadeIn(),
                exit = shrinkVertically() + fadeOut()
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Filled.Warning, contentDescription = null, tint = MaterialTheme.colorScheme.error)
                    Text(text = "  ${estado.mensajeError.orEmpty()}", color = MaterialTheme.colorScheme.error)
                }
            }

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
                Text("  Guardar evento")
            }
        }
    }
}

@Composable
private fun CampoEvento(
    valor: String,
    onValorChange: (String) -> Unit,
    etiqueta: String,
    icono: ImageVector,
    error: String? = null,
    placeholder: String? = null,
    keyboardOptions: KeyboardOptions = KeyboardOptions.Default,
    visualTransformation: VisualTransformation = VisualTransformation.None
) {
    OutlinedTextField(
        value = valor,
        onValueChange = onValorChange,
        label = { Text(etiqueta) },
        placeholder = placeholder?.let { { Text(it) } },
        leadingIcon = { Icon(icono, contentDescription = null) },
        trailingIcon = {
            AnimatedVisibility(
                visible = error != null,
                enter = scaleIn() + fadeIn(),
                exit = scaleOut() + fadeOut()
            ) {
                Icon(Icons.Filled.Error, contentDescription = "Error", tint = MaterialTheme.colorScheme.error)
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
        keyboardOptions = keyboardOptions,
        visualTransformation = visualTransformation,
        singleLine = true,
        modifier = Modifier.fillMaxWidth()
    )
}
