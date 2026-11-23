package com.example.agenciasoportunidades.model.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "resultados")
data class ResultadoEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val activacionId: Int,
    val asistentesReales: Int,
    val leads: Int,
    val comentario: String
)