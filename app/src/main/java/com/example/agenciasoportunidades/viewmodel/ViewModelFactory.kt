package com.example.agenciasoportunidades.viewmodel

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.agenciasoportunidades.data.AppDatabase
import com.example.agenciasoportunidades.data.OportunidadRepository

// Crea los ViewModels entregándoles su repositorio.
// Es necesaria porque OportunidadViewModel recibe parámetros en el constructor
// y viewModel() por sí solo solo sabe crear ViewModels sin parámetros.
class ViewModelFactory(context: Context) : ViewModelProvider.Factory {

    // applicationContext: evita guardar una referencia a la Activity (fuga de memoria)
    private val appContext = context.applicationContext

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(OportunidadViewModel::class.java)) {
            val dao = AppDatabase.getDatabase(appContext).oportunidadDao()
            return OportunidadViewModel(OportunidadRepository(dao)) as T
        }
        throw IllegalArgumentException("ViewModel desconocido: ${modelClass.name}")
    }
}
