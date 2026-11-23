package com.example.agenciasoportunidades.model.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "activaciones")
data class ActivacionEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val oportunidadId: Int,
    val fecha: String,
    val responsable: String,
    val estado: String
)