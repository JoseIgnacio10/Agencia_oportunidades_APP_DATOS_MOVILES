package com.example.agenciasoportunidades.model

data class Evento (
    val id: String? = null,
    val nombre: String,
    val tipo: String,
    val descripcion: String? = null,
    val fecha : String,
    val horario: String? = null,
    val comuna: String,
    val region: String,
    val organizacion: String,
    val asistentesEstimados: Int? = null,
    val condiciones: String? = null

)