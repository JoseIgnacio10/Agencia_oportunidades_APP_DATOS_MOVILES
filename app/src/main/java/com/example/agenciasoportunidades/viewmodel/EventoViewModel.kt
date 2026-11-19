package com.example.agenciasoportunidades.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.agenciasoportunidades.model.Evento
import com.example.agenciasoportunidades.repository.EventoRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class EventoViewModel(
    private val repository: EventoRepository = EventoRepository()
) : ViewModel() {

    private val _eventoList = MutableStateFlow<List<Evento>>(emptyList())

    val eventoList: StateFlow<List<Evento>> = _eventoList

    private val _mensajeError = MutableStateFlow<String?>(null)

    val mensajeError: StateFlow<String?> = _mensajeError

    init {
        cargarEventos()
    }

    fun cargarEventos(){
        viewModelScope.launch {
            try{
                _eventoList.value = repository.getEventos()
                _mensajeError.value = null
            } catch ( e: Exception){
                _mensajeError.value = "No se pudo conectar con el microservicio"
            }
        }
    }
}