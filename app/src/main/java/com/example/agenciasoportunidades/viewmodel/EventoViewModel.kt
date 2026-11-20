package com.example.agenciasoportunidades.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.agenciasoportunidades.model.Evento
import com.example.agenciasoportunidades.model.EventoErrores
import com.example.agenciasoportunidades.model.EventoUiState
import com.example.agenciasoportunidades.repository.EventoRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Locale

class EventoViewModel(
    private val repository: EventoRepository = EventoRepository()
) : ViewModel() {

    private val _eventoList = MutableStateFlow<List<Evento>>(emptyList())

    val eventoList: StateFlow<List<Evento>> = _eventoList

    private val _mensajeError = MutableStateFlow<String?>(null)

    val mensajeError: StateFlow<String?> = _mensajeError

    private val _estado = MutableStateFlow(EventoUiState())

    val estado: StateFlow<EventoUiState> = _estado

    init {
        cargarEventos()
    }

    fun cargarEventos() {
        viewModelScope.launch {
            try {
                _eventoList.value = repository.getEventos()
                _mensajeError.value = null
            } catch (e: Exception) {
                _mensajeError.value = "No se pudo conectar con el microservicio"
            }
        }
    }

    fun nuevoEvento() {
        _estado.value = EventoUiState()
    }

    fun editarEvento(evento: Evento) {
        _estado.value = EventoUiState(
            id = evento.id,
            nombre = evento.nombre,
            tipo = evento.tipo,
            descripcion = evento.descripcion.orEmpty(),
            fecha = evento.fecha.filter { it.isDigit() },
            horario = evento.horario.orEmpty(),
            comuna = evento.comuna,
            region = evento.region,
            organizacion = evento.organizacion,
            asistentesEstimados = evento.asistentesEstimados?.toString().orEmpty(),
            condiciones = evento.condiciones.orEmpty()
        )
    }

    fun onNombreChange(valor: String) {
        _estado.update { it.copy(nombre = valor, errores = it.errores.copy(nombre = null)) }
    }

    fun onTipoChange(valor: String) {
        _estado.update { it.copy(tipo = valor) }
    }

    fun onDescripcionChange(valor: String) {
        _estado.update { it.copy(descripcion = valor) }
    }

    fun onFechaChange(valor: String) {
        _estado.update {
            it.copy(
                fecha = valor.filter { c -> c.isDigit() }.take(8),
                errores = it.errores.copy(fecha = null)
            )
        }
    }

    fun onHorarioChange(valor: String) {
        _estado.update { it.copy(horario = valor) }
    }

    fun onComunaChange(valor: String) {
        _estado.update { it.copy(comuna = valor, errores = it.errores.copy(comuna = null)) }
    }

    fun onRegionChange(valor: String) {
        _estado.update { it.copy(region = valor, errores = it.errores.copy(region = null)) }
    }

    fun onOrganizacionChange(valor: String) {
        _estado.update { it.copy(organizacion = valor, errores = it.errores.copy(organizacion = null)) }
    }

    fun onAsistentesChange(valor: String) {
        _estado.update { it.copy(asistentesEstimados = valor.filter { c -> c.isDigit() }) }
    }

    fun onCondicionesChange(valor: String) {
        _estado.update { it.copy(condiciones = valor) }
    }

    fun validarFormulario(): Boolean {
        val estadoActual = _estado.value

        val errores = EventoErrores(
            nombre = if (estadoActual.nombre.isBlank()) "El nombre es obligatorio" else null,
            fecha = when {
                estadoActual.fecha.isBlank() -> "La fecha es obligatoria"
                !esFechaValida(estadoActual.fecha) -> "Ingresa una fecha válida (dd/mm/aaaa)"
                else -> null
            },
            comuna = if (estadoActual.comuna.isBlank()) "La comuna es obligatoria" else null,
            region = if (estadoActual.region.isBlank()) "La región es obligatoria" else null,
            organizacion = if (estadoActual.organizacion.isBlank()) "La organización es obligatoria" else null
        )

        val hayErrores = listOfNotNull(
            errores.nombre,
            errores.fecha,
            errores.comuna,
            errores.region,
            errores.organizacion
        ).isNotEmpty()

        _estado.update { it.copy(errores = errores) }

        return !hayErrores
    }

    private fun esFechaValida(digitos: String): Boolean {
        if (digitos.length != 8) return false
        val formato = SimpleDateFormat("ddMMyyyy", Locale.getDefault())
        formato.isLenient = false
        return runCatching { formato.parse(digitos) }.getOrNull() != null
    }

    fun guardar(onListo: () -> Unit) {
        val a = _estado.value
        val evento = Evento(
            id = a.id,
            nombre = a.nombre.trim(),
            tipo = a.tipo,
            descripcion = a.descripcion.ifBlank { null },
            fecha = "${a.fecha.substring(0, 2)}/${a.fecha.substring(2, 4)}/${a.fecha.substring(4)}",
            horario = a.horario.ifBlank { null },
            comuna = a.comuna.trim(),
            region = a.region.trim(),
            organizacion = a.organizacion.trim(),
            asistentesEstimados = a.asistentesEstimados.toIntOrNull(),
            condiciones = a.condiciones.ifBlank { null }
        )
        viewModelScope.launch {
            try {
                if (a.id == null) {
                    repository.crearEvento(evento)
                } else {
                    repository.actualizarEvento(a.id, evento)
                }
                cargarEventos()
                onListo()
            } catch (e: Exception) {
                _estado.update { it.copy(mensajeError = "No se pudo guardar el evento") }
            }
        }
    }

    fun eliminar(evento: Evento) {
        val id = evento.id ?: return
        viewModelScope.launch {
            try {
                repository.eliminarEvento(id)
                cargarEventos()
            } catch (e: Exception) {
                _mensajeError.value = "No se pudo eliminar el evento"
            }
        }
    }
}