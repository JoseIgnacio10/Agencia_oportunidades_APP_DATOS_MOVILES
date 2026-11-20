package com.example.agenciasoportunidades.remote

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitInstance {
    val api: ApiService by lazy {
        Retrofit.Builder()
            .baseUrl("http://10.0.2.2:8080/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(ApiService::class.java)
    }


    val feriadosApi: FeriadoService by lazy {
        Retrofit.Builder()
            .baseUrl("https://date.nager.at/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(FeriadoService::class.java)
    }
}
