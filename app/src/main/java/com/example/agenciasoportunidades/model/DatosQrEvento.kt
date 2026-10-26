package com.example.agenciasoportunidades.model

// Datos de un evento leídos desde un código QR (ej: el afiche de una feria).
// Formato esperado: una línea por dato "clave=valor" (también acepta ";" como separador):
//   titulo=Feria de Beneficios 2026
//   organizacion=Municipalidad Ficticia
//   tipo=Feria empresarial
//   publico=Trabajadores municipales
//   ubicacion=Puente Alto, RM
//   fecha=15/11/2026
// Todos los campos son opcionales: se completa lo que venga y el resto lo escribe el usuario.
data class DatosQrEvento(
    val titulo: String? = null,
    val organizacion: String? = null,
    val tipoEvento: String? = null,
    val publicoObjetivo: String? = null,
    val ubicacion: String? = null,
    val fecha: String? = null
) {
    companion object {
        // Devuelve null si el QR no trae ningún dato de evento (ej: un QR de una página web)
        fun desdeTexto(texto: String): DatosQrEvento? {
            val valores = texto
                .split('\n', ';')
                .mapNotNull { linea ->
                    val partes = linea.split('=', limit = 2)
                    if (partes.size == 2 && partes[1].isNotBlank()) {
                        partes[0].trim().lowercase() to partes[1].trim()
                    } else null
                }
                .toMap()

            val datos = DatosQrEvento(
                titulo = valores["titulo"],
                organizacion = valores["organizacion"],
                tipoEvento = valores["tipo"],
                publicoObjetivo = valores["publico"],
                ubicacion = valores["ubicacion"],
                fecha = valores["fecha"]
            )
            return if (datos == DatosQrEvento()) null else datos
        }
    }
}
