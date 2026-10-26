package com.example.agenciasoportunidades.data

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

// Usuario del equipo comercial que ingresa a la app con una cuenta ficticia (pedido por el caso).
// El correo es único: no pueden existir dos cuentas con el mismo correo.
@Entity(
    tableName = "usuarios",
    indices = [Index(value = ["correo"], unique = true)]
)
data class UsuarioEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val nombre: String,
    val correo: String,
    // Cuenta de demostración con datos ficticios; en una app real se guardaría un hash, no la clave
    val clave: String,
    val perfil: String
)
