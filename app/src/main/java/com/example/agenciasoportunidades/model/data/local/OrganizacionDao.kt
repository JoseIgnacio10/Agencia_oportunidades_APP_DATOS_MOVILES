package com.example.agenciasoportunidades.model.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface OrganizacionDao {

    @Query("SELECT * FROM organizaciones ORDER BY nombre ASC")
    fun obtenerTodas(): Flow<List<OrganizacionEntity>>

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertar(organizacion: OrganizacionEntity)
}