package com.example.agenciasoportunidades.data

import kotlinx.coroutines.flow.Flow

class OportunidadRepository(private val dao: OportunidadDao) {

    fun obtenerTodasLasOportunidades(): Flow<List<OportunidadEntity>> {
        return dao.obtenerTodasLasOportunidades()
    }

    suspend fun insertarOportunidad(oportunidad: OportunidadEntity) {
        dao.insertarOportunidad(oportunidad)
    }

    suspend fun eliminarOportunidad(oportunidad: OportunidadEntity) {
        dao.eliminarOportunidad(oportunidad)
    }
}