package com.example.agenciasoportunidades.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.agenciasoportunidades.model.Feriado
import com.example.agenciasoportunidades.repository.FeriadoRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import java.util.Calendar

class FeriadoViewModel(
    private val repository: FeriadoRepository = FeriadoRepository()
) : ViewModel() {

    val anio: Int = Calendar.getInstance().get(Calendar.YEAR)

    private val _feriadoList = MutableStateFlow<List<Feriado>>(emptyList())

    val feriadoList: StateFlow<List<Feriado>> = _feriadoList

    private val _mensajeError = MutableStateFlow<String?>(null)

    val mensajeError: StateFlow<String?> = _mensajeError

    init {
        cargarFeriados()
    }

    fun cargarFeriados() {
        viewModelScope.launch {
            try {
                _feriadoList.value = repository.getFeriados(anio)
                _mensajeError.value = null
            } catch (e: Exception) {
                _mensajeError.value = "No se pudieron obtener los feriados"
            }
        }
    }
}