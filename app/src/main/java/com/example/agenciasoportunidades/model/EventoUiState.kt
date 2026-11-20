package com.example.agenciasoportunidades.model

data class EventoUiState(
    val id: String? = null,
    val nombre: String = "",
    val tipo: String = "Evento corporativo",
    val descripcion: String = "",
    val fecha: String = "",
    val horario: String = "",
    val comuna: String = "",
    val region: String = "",
    val organizacion: String = "",
    val asistentesEstimados: String = "",
    val condiciones: String = "",
    val mensajeError: String? = null,
    val errores: EventoErrores = EventoErrores()
)