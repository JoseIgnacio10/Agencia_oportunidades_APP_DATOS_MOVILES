package com.example.agenciasoportunidades.model

data class LoginUiState(
    val correo: String = "",
    val clave: String = "",
    val mensajeError: String? = null,
    val usuarioNombre: String? = null,
    val errores: LoginErrores = LoginErrores()
)
