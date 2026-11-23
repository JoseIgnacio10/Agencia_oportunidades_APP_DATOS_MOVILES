package com.example.agenciasoportunidades.model.data.local

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface OportunidadDao {
    @Query("SELECT * FROM oportunidades ORDER BY id DESC")
    fun obtenerTodasLasOportunidades(): Flow<List<OportunidadEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertarOportunidad(oportunidad: OportunidadEntity): Long

    @Update
    suspend fun actualizarOportunidad(oportunidad: OportunidadEntity)

    @Delete
    suspend fun eliminarOportunidad(oportunidad: OportunidadEntity)
}