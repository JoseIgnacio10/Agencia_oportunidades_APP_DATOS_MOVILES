package com.example.agenciasoportunidades.repository

import com.example.agenciasoportunidades.model.Evento
import com.example.agenciasoportunidades.remote.RetrofitInstance

class EventoRepository {

    suspend fun getEventos(): List<Evento>{
        return RetrofitInstance.api.getEventos()
    }

    suspend fun crearEvento(evento: Evento): Evento{
        return RetrofitInstance.api.crearEvento(evento)
    }

    suspend fun actualizarEvento(id: String, evento: Evento): Evento{
        return RetrofitInstance.api.actualizarEvento(id, evento)
    }

    suspend fun eliminarEvento(id: String){
        RetrofitInstance.api.eliminarEvento(id)
    }
}