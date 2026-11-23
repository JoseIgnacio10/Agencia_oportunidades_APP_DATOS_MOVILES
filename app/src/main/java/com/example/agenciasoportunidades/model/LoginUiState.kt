package com.example.agenciasoportunidades.model

data class LoginUiState(
    val correo: String = "",
    val clave: String = "",
    val mensajeError: String? = null,
    val usuarioNombre: String? = null,
    val usuarioPerfil: String? = null,
    val errores: LoginErrores = LoginErrores()
) {
    val esEjecutivo: Boolean
        get() = usuarioPerfil == "Ejecutivo comercial"
    val esSupervisor: Boolean
        get() = usuarioPerfil == "Supervisor comercial"

    val esActivaciones: Boolean
        get() = usuarioPerfil == "Gestión de activaciones"
}