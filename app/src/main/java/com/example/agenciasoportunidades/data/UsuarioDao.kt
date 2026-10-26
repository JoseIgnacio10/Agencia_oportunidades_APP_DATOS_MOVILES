package com.example.agenciasoportunidades.data

import androidx.room.Dao
import androidx.room.Query

@Dao
interface UsuarioDao {

    // null si no existe una cuenta con ese correo
    @Query("SELECT * FROM usuarios WHERE correo = :correo LIMIT 1")
    suspend fun buscarPorCorreo(correo: String): UsuarioEntity?
}
