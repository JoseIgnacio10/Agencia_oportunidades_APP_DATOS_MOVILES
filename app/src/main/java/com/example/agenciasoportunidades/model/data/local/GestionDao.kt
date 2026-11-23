package com.example.agenciasoportunidades.model.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface GestionDao {

    @Query("SELECT * FROM gestiones WHERE oportunidadId = :id ORDER BY id DESC")
    fun obtenerPorOportunidad(id: Int): Flow<List<GestionEntity>>

    @Insert
    suspend fun insertar(gestion: GestionEntity)
}