package com.example.agenciasoportunidades.repository

import com.example.agenciasoportunidades.model.Feriado
import com.example.agenciasoportunidades.remote.RetrofitInstance

class FeriadoRepository {

    suspend fun getFeriados(anio: Int): List<Feriado> {
        return RetrofitInstance.feriadosApi.getFeriados(anio)
    }
}