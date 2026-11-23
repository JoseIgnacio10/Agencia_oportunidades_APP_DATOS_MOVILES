package com.example.agenciasoportunidades.model.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface ActivacionDao {

    @Query("SELECT * FROM activaciones WHERE oportunidadId = :id LIMIT 1")
    fun obtenerPorOportunidad(id: Int): Flow<ActivacionEntity?>

    @Insert
    suspend fun insertar(activacion: ActivacionEntity)

    @Update
    suspend fun actualizar(activacion: ActivacionEntity)
}