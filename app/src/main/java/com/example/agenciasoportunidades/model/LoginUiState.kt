package com.example.agenciasoportunidades.model

// Estado de la pantalla de login: valores, errores por campo y resultado del ingreso
data class LoginUiState(
    val correo: String = "",
    val correoError: String? = null,

    val clave: String = "",
    val claveError: String? = null,

    val esFormularioValido: Boolean = false,
    val estaCargando: Boolean = false,

    // Error general (ej: correo o clave incorrectos), distinto de los errores por campo
    val mensajeError: String? = null,

    // Nombre del usuario que inició sesión; null = no hay sesión
    val usuarioNombre: String? = null
)
