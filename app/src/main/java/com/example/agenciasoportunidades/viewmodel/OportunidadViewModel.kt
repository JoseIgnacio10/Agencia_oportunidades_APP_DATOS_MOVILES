package com.example.agenciasoportunidades.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.agenciasoportunidades.model.OportunidadErrores
import com.example.agenciasoportunidades.model.OportunidadUiState
import com.example.agenciasoportunidades.model.data.local.AppDatabase
import com.example.agenciasoportunidades.model.data.local.OportunidadEntity
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Locale

class OportunidadViewModel(app: Application) : AndroidViewModel(app) {

    private val dao = AppDatabase.getDatabase(app).oportunidadDao()

    private val _estado = MutableStateFlow(OportunidadUiState())

    val estado: StateFlow<OportunidadUiState> = _estado

    private val _oportunidades = MutableStateFlow<List<OportunidadEntity>>(emptyList())

    val oportunidades: StateFlow<List<OportunidadEntity>> = _oportunidades

    private val _busqueda = MutableStateFlow("")

    val busqueda: StateFlow<String> = _busqueda

    private var listaCompleta: List<OportunidadEntity> = emptyList()

    init {
        cargarOportunidades()
    }

    private fun cargarOportunidades() {
        viewModelScope.launch {
            dao.obtenerTodasLasOportunidades().collect { lista ->
                listaCompleta = lista
                filtrar()
            }
        }
    }

    fun onBusquedaChange(valor: String) {
        _busqueda.value = valor
        filtrar()
    }

    private fun filtrar() {
        val texto = _busqueda.value.lowercase().trim()
        _oportunidades.value = if (texto.isEmpty()) {
            listaCompleta
        } else {
            listaCompleta.filter {
                it.titulo.lowercase().contains(texto) ||
                        it.organizacion.lowercase().contains(texto) ||
                        it.ubicacion.lowercase().contains(texto) ||
                        it.tipoEvento.lowercase().contains(texto)
            }
        }
    }

    fun onTituloChange(valor: String) {
        _estado.update {
            it.copy(
                titulo = valor,
                errores = it.errores.copy(titulo = null)
            )
        }
    }

    fun onOrganizacionChange(valor: String) {
        _estado.update {
            it.copy(
                organizacion = valor,
                errores = it.errores.copy(organizacion = null)
            )
        }
    }

    fun onTipoEventoChange(valor: String) {
        _estado.update {
            it.copy(tipoEvento = valor)
        }
    }

    fun onPublicoObjetivoChange(valor: String) {
        _estado.update {
            it.copy(
                publicoObjetivo = valor,
                errores = it.errores.copy(publicoObjetivo = null)
            )
        }
    }

    fun onUbicacionChange(valor: String) {
        _estado.update {
            it.copy(
                ubicacion = valor,
                errores = it.errores.copy(ubicacion = null)
            )
        }
    }

    fun onFechaChange(valor: String) {
        _estado.update {
            it.copy(
                fecha = valor.filter { c -> c.isDigit() }.take(8),
                errores = it.errores.copy(fecha = null)
            )
        }
    }

    fun onFotoChange(ruta: String?) {
        _estado.update {
            it.copy(fotoPath = ruta)
        }
    }

    fun cargarDesdeQr(texto: String): Boolean {
        val datos = texto
            .split('\n', ';')
            .mapNotNull { linea ->
                val partes = linea.split('=', limit = 2)
                if (partes.size == 2 && partes[1].isNotBlank()) {
                    partes[0].trim().lowercase() to partes[1].trim()
                } else null
            }
            .toMap()

        val claves = listOf("titulo", "organizacion", "tipo", "publico", "ubicacion", "fecha")
        if (claves.none { it in datos }) return false

        _estado.update {
            it.copy(
                titulo = datos["titulo"] ?: it.titulo,
                organizacion = datos["organizacion"] ?: it.organizacion,
                tipoEvento = datos["tipo"] ?: it.tipoEvento,
                publicoObjetivo = datos["publico"] ?: it.publicoObjetivo,
                ubicacion = datos["ubicacion"] ?: it.ubicacion,
                fecha = datos["fecha"]?.filter { c -> c.isDigit() }?.take(8) ?: it.fecha,
                errores = OportunidadErrores()
            )
        }
        return true
    }

    fun validarFormulario(): Boolean {
        val estadoActual = _estado.value

        val errores = OportunidadErrores(
            titulo = if (estadoActual.titulo.isBlank())
                "El título es obligatorio"
            else null,

            organizacion = if (estadoActual.organizacion.isBlank())
                "La organización es obligatoria"
            else null,

            publicoObjetivo = if (estadoActual.publicoObjetivo.isBlank())
                "El público objetivo es obligatorio"
            else null,

            ubicacion = if (estadoActual.ubicacion.isBlank())
                "La ubicación es obligatoria"
            else null,

            fecha = when {
                estadoActual.fecha.isBlank() -> "La fecha es obligatoria"
                !esFechaValida(estadoActual.fecha) -> "Ingresa una fecha válida (dd/mm/aaaa)"
                else -> null
            }
        )

        val hayErrores = listOfNotNull(
            errores.titulo,
            errores.organizacion,
            errores.publicoObjetivo,
            errores.ubicacion,
            errores.fecha
        ).isNotEmpty()

        _estado.update {
            it.copy(errores = errores)
        }

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
        viewModelScope.launch {
            dao.insertarOportunidad(
                OportunidadEntity(
                    titulo = a.titulo,
                    organizacion = a.organizacion,
                    tipoEvento = a.tipoEvento,
                    publicoObjetivo = a.publicoObjetivo,
                    ubicacion = a.ubicacion,
                    fecha = "${a.fecha.substring(0, 2)}/${a.fecha.substring(2, 4)}/${a.fecha.substring(4)}",
                    fotoPath = a.fotoPath
                )
            )
            _estado.value = OportunidadUiState()
            onListo()
        }
    }

    fun eliminar(oportunidad: OportunidadEntity) {
        viewModelScope.launch {
            dao.eliminarOportunidad(oportunidad)
        }
    }
}
