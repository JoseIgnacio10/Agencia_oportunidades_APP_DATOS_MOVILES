package com.example.agenciasoportunidades.model

data class OportunidadFormState(
    // valores requeridos ///
    val titulo : String ="",
    val tituloError : String? = null,

    val organizacion : String= "",
    val organizacionError: String? = null,

    val tipoEvento: String = "Evento corporativo",

    val publicoObjetivo: String = "",
    val publicoObjetivoError: String? = null,

    val ubicacion: String = "",
    val ubicacionError: String? = null,

    val fecha: String = "",
    val fechaError: String? = null,

    // Ruta de la foto capturada mediante el recurso nativo (Cámara)
    val fotoPath: String? = null,
    val fotoError: String? = null,

    // Estado de validación
    val esFormularioValido: Boolean = false
)
