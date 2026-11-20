package com.example.agenciasoportunidades.remote

import com.example.agenciasoportunidades.model.Feriado
import retrofit2.http.GET
import retrofit2.http.Path

interface FeriadoService {

    @GET("api/v3/PublicHolidays/{anio}/CL")
    suspend fun getFeriados(@Path("anio") anio: Int): List<Feriado>
}