package com.example.agenciasoportunidades.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Business
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Event
import androidx.compose.material.icons.filled.Groups
import androidx.compose.material.icons.filled.Place
import androidx.compose.material3.AssistChip
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.example.agenciasoportunidades.data.OportunidadEntity

// Tarjeta de una oportunidad en la lista. No conoce el ViewModel:
// recibe los datos y avisa hacia arriba cuando se pulsa "eliminar".
@Composable
fun OportunidadCard(
    oportunidad: OportunidadEntity,
    onEliminar: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = oportunidad.titulo,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.weight(1f)
                )
                IconButton(onClick = onEliminar) {
                    Icon(
                        Icons.Filled.Delete,
                        contentDescription = "Eliminar oportunidad",
                        tint = MaterialTheme.colorScheme.error
                    )
                }
            }
            // Tipo de evento como etiqueta, para distinguirlo de un vistazo
            AssistChip(onClick = {}, label = { Text(oportunidad.tipoEvento) })
            Spacer(Modifier.size(4.dp))
            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                DatoConIcono(Icons.Filled.Business, oportunidad.organizacion)
                DatoConIcono(Icons.Filled.Event, oportunidad.fecha)
                DatoConIcono(Icons.Filled.Place, oportunidad.ubicacion)
                DatoConIcono(Icons.Filled.Groups, oportunidad.publicoObjetivo)
            }
        }
    }
}

@Composable
private fun DatoConIcono(icono: ImageVector, texto: String) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Icon(
            icono,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.primary,
            modifier = Modifier.size(18.dp)
        )
        Spacer(Modifier.width(8.dp))
        Text(texto, style = MaterialTheme.typography.bodyMedium)
    }
}
