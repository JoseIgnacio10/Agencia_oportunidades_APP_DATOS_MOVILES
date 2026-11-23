package com.example.agenciasoportunidades.model.data.local

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "organizaciones",
    indices = [Index(value = ["nombre"], unique = true)]
)
data class OrganizacionEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val nombre: String,
    val rubro: String,
    val comuna: String
)