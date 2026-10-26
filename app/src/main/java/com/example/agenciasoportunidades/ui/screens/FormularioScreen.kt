package com.example.agenciasoportunidades.ui.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
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
    onVolver: () -> Unit
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
