package com.example.agenciasoportunidades.ui.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.AssistChip
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
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
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.agenciasoportunidades.viewmodel.LoginViewModel
import com.example.agenciasoportunidades.viewmodel.OportunidadViewModel

private val ESTADOS = listOf("Pendiente", "En gestión", "Confirmada", "Descartada")

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DetalleScreen(
    navController: NavController,
    viewModel: OportunidadViewModel,
    loginViewModel: LoginViewModel
) {
    val oportunidad by viewModel.seleccionada.collectAsState()
    val sesion by loginViewModel.estado.collectAsState()

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
                    .verticalScroll(rememberScrollState())
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
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
            }
        }
    }
}
