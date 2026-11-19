package com.example.agenciasoportunidades.remote

import com.example.agenciasoportunidades.model.Evento

import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path

interface ApiService {
    @GET("api/eventos")
    suspend fun getEventos(): List<Evento>

    @GET("api/eventos/{id}")
    suspend fun getEvento(@Path("id") id: String): Evento

    @POST("api/eventos")
    suspend fun crearEvento(@Body evento: Evento): Evento

    @PUT("api/eventos/{id}")
    suspend fun actualizarEvento(@Path("id") id: String, @Body evento: Evento): Evento

    @DELETE("api/eventos/{id}")
    suspend fun eliminarEvento(@Path("id") id: String): Response<Unit>
}