package com.example.agenciasoportunidades.model

import com.example.agenciasoportunidades.data.OportunidadEntity
import com.example.agenciasoportunidades.data.OportunidadRepository

data class OportunidadListState(
    val oportunidades: List<OportunidadEntity> = emptyList(),
    val estaCargando: Boolean = false,
    val mensajeError: String?= null

)
