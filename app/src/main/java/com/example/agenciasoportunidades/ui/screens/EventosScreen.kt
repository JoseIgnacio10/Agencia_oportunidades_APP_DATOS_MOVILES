package com.example.agenciasoportunidades.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Celebration
import androidx.compose.material.icons.filled.CloudOff
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.AssistChip
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.agenciasoportunidades.viewmodel.EventoViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable

fun EventosScreen(
    navController: NavController,
    viewModel: EventoViewModel
){
    val eventos by viewModel.eventoList.collectAsState()
    val mensajeError by viewModel.mensajeError.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Catálogo de eventos") },
                navigationIcon = {
                    IconButton(onClick =  {
                        navController.popBackStack() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack,contentDescription = "Volver")
                    }
                },
                actions = {
                    IconButton(onClick = { navController.navigate("feriados") }) {
                        Icon(Icons.Filled.Celebration, contentDescription = "Feriados")
                    }
                    IconButton(onClick = { viewModel.cargarEventos() }) {
                        Icon(Icons.Filled.Refresh, contentDescription = "Recargar")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer
                )
            )
        },
        floatingActionButton = {
            ExtendedFloatingActionButton(
                onClick = {
                    viewModel.nuevoEvento()
                    navController.navigate("evento_formulario")
                },
                icon = { Icon(Icons.Filled.Add, contentDescription = null) },
                text = { Text("Nuevo evento") }
            )
        }
    ) { padding ->
        if (mensajeError != null) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .padding(32.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Icon(
                    Icons.Filled.CloudOff,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.error,
                    modifier = Modifier.size(64.dp)
                )
                Spacer(Modifier.size(12.dp))
                Text(
                    text = mensajeError.orEmpty(),
                    textAlign = TextAlign.Center,
                    style = MaterialTheme.typography.bodyLarge
                )
                Spacer(Modifier.size(16.dp))
                Button(onClick = { viewModel.cargarEventos() }) {
                    Text("Reintentar")
                }
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(eventos) { evento ->
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(
                                    text = evento.nombre,
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.Bold,
                                    modifier = Modifier.weight(1f)
                                )
                                IconButton(onClick = {
                                    viewModel.editarEvento(evento)
                                    navController.navigate("evento_formulario")
                                }) {
                                    Icon(Icons.Filled.Edit, contentDescription = "Editar evento")
                                }
                                IconButton(onClick = { viewModel.eliminar(evento) }) {
                                    Icon(Icons.Filled.Delete, contentDescription = "Eliminar evento", tint = MaterialTheme.colorScheme.error)
                                }
                            }
                            AssistChip(onClick = {}, label = { Text(evento.tipo) })
                            Text("Organización: ${evento.organizacion}")
                            Text("Fecha: ${evento.fecha} ${evento.horario.orEmpty()}")
                            Text("Lugar: ${evento.comuna}, ${evento.region}")
                            evento.asistentesEstimados?.let { Text("Asistentes estimados: $it") }
                        }
                    }
                }
            }
        }
    }
}