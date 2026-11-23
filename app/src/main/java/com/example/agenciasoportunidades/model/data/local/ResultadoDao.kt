package com.example.agenciasoportunidades.model.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface ResultadoDao {

    @Query("SELECT * FROM resultados WHERE activacionId = :id LIMIT 1")
    fun obtenerPorActivacion(id: Int): Flow<ResultadoEntity?>

    @Insert
    suspend fun insertar(resultado: ResultadoEntity)
}