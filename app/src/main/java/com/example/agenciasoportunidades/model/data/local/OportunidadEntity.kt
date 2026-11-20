package com.example.agenciasoportunidades.model.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "oportunidades")
data class OportunidadEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val titulo: String,
    val organizacion: String,
    val tipoEvento: String,
    val publicoObjetivo: String,
    val ubicacion: String,
    val fecha: String,
    val fotoPath: String? = null,
    val estado : String = "Pendiente"
)