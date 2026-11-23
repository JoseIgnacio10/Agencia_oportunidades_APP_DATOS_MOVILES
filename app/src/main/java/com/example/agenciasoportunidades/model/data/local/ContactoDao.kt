package com.example.agenciasoportunidades.model.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface ContactoDao {

    @Query("SELECT * FROM contactos WHERE oportunidadId = :id ORDER BY id ASC")
    fun obtenerPorOportunidad(id: Int): Flow<List<ContactoEntity>>

    @Insert
    suspend fun insertar(contacto: ContactoEntity)
}