package com.example.agenciasoportunidades.data

class UsuarioRepository(private val dao: UsuarioDao) {

    // Devuelve el usuario si el correo existe y la clave coincide; si no, null
    suspend fun validarCredenciales(correo: String, clave: String): UsuarioEntity? {
        val usuario = dao.buscarPorCorreo(correo.trim().lowercase())
        return usuario?.takeIf { it.clave == clave }
    }
}
