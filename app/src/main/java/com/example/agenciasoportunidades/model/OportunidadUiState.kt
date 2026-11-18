package com.example.agenciasoportunidades.model

data class OportunidadUiState(
    val titulo: String = "",
    val organizacion: String = "",
    val tipoEvento: String = "Evento corporativo",
    val publicoObjetivo: String = "",
    val ubicacion: String = "",
    val fecha: String = "",
    val fotoPath: String? = null,
    val errores: OportunidadErrores = OportunidadErrores()
)
