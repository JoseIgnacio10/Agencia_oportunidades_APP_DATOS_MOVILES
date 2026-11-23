package com.example.agenciasoportunidades.model.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "gestiones")
data class GestionEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val oportunidadId: Int,
    val fecha: String,
    val tipo: String,
    val descripcion: String,
    val usuario: String
)