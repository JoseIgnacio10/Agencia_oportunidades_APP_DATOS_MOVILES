package com.example.agenciasoportunidades.viewmodel

import android.app.Application
import android.util.Patterns
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.agenciasoportunidades.model.OportunidadErrores
import com.example.agenciasoportunidades.model.OportunidadUiState
import com.example.agenciasoportunidades.model.data.local.ActivacionEntity
import com.example.agenciasoportunidades.model.data.local.AppDatabase
import com.example.agenciasoportunidades.model.data.local.ContactoEntity
import com.example.agenciasoportunidades.model.data.local.GestionEntity
import com.example.agenciasoportunidades.model.data.local.OportunidadEntity
import com.example.agenciasoportunidades.model.data.local.OrganizacionEntity
import com.example.agenciasoportunidades.model.data.local.ResultadoEntity
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class OportunidadViewModel(app: Application) : AndroidViewModel(app) {

    private val db = AppDatabase.getDatabase(app)
    private val dao = db.oportunidadDao()
    private val organizacionDao = db.organizacionDao()
    private val contactoDao = db.contactoDao()
    private val gestionDao = db.gestionDao()
    private val activacionDao = db.activacionDao()
    private val resultadoDao = db.resultadoDao()

    private val _estado = MutableStateFlow(OportunidadUiState())
    val estado: StateFlow<OportunidadUiState> = _estado

    private val _oportunidades = MutableStateFlow<List<OportunidadEntity>>(emptyList())
    val oportunidades: StateFlow<List<OportunidadEntity>> = _oportunidades

    private val _seleccionada = MutableStateFlow<OportunidadEntity?>(null)
    val seleccionada: StateFlow<OportunidadEntity?> = _seleccionada

    private val _busqueda = MutableStateFlow("")
    val busqueda: StateFlow<String> = _busqueda

    private val _organizaciones = MutableStateFlow<List<OrganizacionEntity>>(emptyList())
    val organizaciones: StateFlow<List<OrganizacionEntity>> = _organizaciones

    private val _contactos = MutableStateFlow<List<ContactoEntity>>(emptyList())
    val contactos: StateFlow<List<ContactoEntity>> = _contactos

    private val _gestiones = MutableStateFlow<List<GestionEntity>>(emptyList())
    val gestiones: StateFlow<List<GestionEntity>> = _gestiones

    private val _activacion = MutableStateFlow<ActivacionEntity?>(null)
    val activacion: StateFlow<ActivacionEntity?> = _activacion

    private val _resultado = MutableStateFlow<ResultadoEntity?>(null)
    val resultado: StateFlow<ResultadoEntity?> = _resultado

    private var listaCompleta: List<OportunidadEntity> = emptyList()
    private var detalleJob: Job? = null
    private var resultadoJob: Job? = null

    init {
        cargarOportunidades()
        cargarOrganizaciones()
    }

    private fun cargarOportunidades() {
        viewModelScope.launch {
            dao.obtenerTodasLasOportunidades().collect { lista ->
                listaCompleta = lista
                filtrar()
            }
        }
    }

    private fun cargarOrganizaciones() {
        viewModelScope.launch {
            organizacionDao.obtenerTodas().collect { lista ->
                _organizaciones.value = lista
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
                        it.tipoEvento.lowercase().contains(texto) ||
                        it.estado.lowercase().contains(texto)
            }
        }
    }

    fun nuevaOportunidad() {
        _estado.value = OportunidadUiState()
    }

    fun editarOportunidad(oportunidad: OportunidadEntity) {
        viewModelScope.launch {
            val primerContacto = contactoDao.obtenerPorOportunidad(oportunidad.id)
                .firstOrNull()
                ?.firstOrNull()

            _estado.value = OportunidadUiState(
                id = oportunidad.id,
                titulo = oportunidad.titulo,
                organizacion = oportunidad.organizacion,
                tipoEvento = oportunidad.tipoEvento,
                publicoObjetivo = oportunidad.publicoObjetivo,
                ubicacion = oportunidad.ubicacion,
                fecha = oportunidad.fecha.filter { it.isDigit() },
                fotoPath = oportunidad.fotoPath,
                contactoNombre = primerContacto?.nombre ?: "",
                contactoCargo = primerContacto?.cargo ?: "",
                contactoTelefono = primerContacto?.telefono ?: "",
                contactoCorreo = primerContacto?.correo ?: ""
            )
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

    fun onContactoNombreChange(valor: String) {
        _estado.update {
            it.copy(
                contactoNombre = valor,
                errores = it.errores.copy(contactoNombre = null)
            )
        }
    }

    fun onContactoCargoChange(valor: String) {
        _estado.update {
            it.copy(contactoCargo = valor)
        }
    }

    fun onContactoTelefonoChange(valor: String) {
        _estado.update {
            it.copy(contactoTelefono = valor)
        }
    }

    fun onContactoCorreoChange(valor: String) {
        _estado.update {
            it.copy(
                contactoCorreo = valor,
                errores = it.errores.copy(contactoCorreo = null)
            )
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
            },

            contactoNombre = if (estadoActual.contactoNombre.isBlank())
                "El nombre del contacto es obligatorio"
            else null,

            contactoCorreo = if (estadoActual.contactoCorreo.isNotBlank() &&
                !Patterns.EMAIL_ADDRESS.matcher(estadoActual.contactoCorreo.trim()).matches()
            )
                "Correo de contacto no válido"
            else null
        )

        val hayErrores = listOfNotNull(
            errores.titulo,
            errores.organizacion,
            errores.publicoObjetivo,
            errores.ubicacion,
            errores.fecha,
            errores.contactoNombre,
            errores.contactoCorreo
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
        val fecha = "${a.fecha.substring(0, 2)}/${a.fecha.substring(2, 4)}/${a.fecha.substring(4)}"
        viewModelScope.launch {
            val original = listaCompleta.find { it.id == a.id }
            if (original == null) {
                val nuevoId = dao.insertarOportunidad(
                    OportunidadEntity(
                        titulo = a.titulo,
                        organizacion = a.organizacion,
                        tipoEvento = a.tipoEvento,
                        publicoObjetivo = a.publicoObjetivo,
                        ubicacion = a.ubicacion,
                        fecha = fecha,
                        fotoPath = a.fotoPath
                    )
                ).toInt()

                if (a.contactoNombre.isNotBlank() && nuevoId > 0) {
                    contactoDao.insertar(
                        ContactoEntity(
                            oportunidadId = nuevoId,
                            nombre = a.contactoNombre.trim(),
                            cargo = a.contactoCargo.trim(),
                            telefono = a.contactoTelefono.trim(),
                            correo = a.contactoCorreo.trim()
                        )
                    )
                }
            } else {
                dao.actualizarOportunidad(
                    original.copy(
                        titulo = a.titulo,
                        organizacion = a.organizacion,
                        tipoEvento = a.tipoEvento,
                        publicoObjetivo = a.publicoObjetivo,
                        ubicacion = a.ubicacion,
                        fecha = fecha,
                        fotoPath = a.fotoPath
                    )
                )
            }
            _estado.value = OportunidadUiState()
            onListo()
        }
    }

    fun seleccionar(oportunidad: OportunidadEntity) {
        _seleccionada.value = oportunidad
        detalleJob?.cancel()
        resultadoJob?.cancel()
        _contactos.value = emptyList()
        _gestiones.value = emptyList()
        _activacion.value = null
        _resultado.value = null

        detalleJob = viewModelScope.launch {
            launch {
                contactoDao.obtenerPorOportunidad(oportunidad.id).collect {
                    _contactos.value = it
                }
            }
            launch {
                gestionDao.obtenerPorOportunidad(oportunidad.id).collect {
                    _gestiones.value = it
                }
            }
            launch {
                activacionDao.obtenerPorOportunidad(oportunidad.id).collect { act ->
                    _activacion.value = act
                    resultadoJob?.cancel()
                    if (act != null) {
                        resultadoJob = viewModelScope.launch {
                            resultadoDao.obtenerPorActivacion(act.id).collect {
                                _resultado.value = it
                            }
                        }
                    } else {
                        _resultado.value = null
                    }
                }
            }
        }
    }

    fun cambiarEstado(estado: String) {
        val actual = _seleccionada.value ?: return
        val actualizada = actual.copy(estado = estado)
        _seleccionada.value = actualizada
        viewModelScope.launch {
            dao.actualizarOportunidad(actualizada)
        }
    }

    fun eliminar(oportunidad: OportunidadEntity) {
        viewModelScope.launch {
            dao.eliminarOportunidad(oportunidad)
        }
    }

    fun registrarGestion(tipo: String, descripcion: String, usuario: String): Boolean {
        val oportunidadActual = _seleccionada.value ?: return false
        if (descripcion.isBlank()) return false
        val fechaHoy = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(Date())
        viewModelScope.launch {
            gestionDao.insertar(
                GestionEntity(
                    oportunidadId = oportunidadActual.id,
                    fecha = fechaHoy,
                    tipo = tipo.trim(),
                    descripcion = descripcion.trim(),
                    usuario = usuario.trim()
                )
            )
        }
        return true
    }

    fun programarActivacion(fecha: String, responsable: String): Boolean {
        val oportunidadActual = _seleccionada.value ?: return false
        val digitos = fecha.filter { it.isDigit() }
        if (!esFechaValida(digitos) || responsable.isBlank()) return false
        val fechaFormateada = "${digitos.substring(0, 2)}/${digitos.substring(2, 4)}/${digitos.substring(4)}"
        viewModelScope.launch {
            activacionDao.insertar(
                ActivacionEntity(
                    oportunidadId = oportunidadActual.id,
                    fecha = fechaFormateada,
                    responsable = responsable.trim(),
                    estado = "Programada"
                )
            )
        }
        return true
    }

    fun marcarActivacionRealizada() {
        val actual = _activacion.value ?: return
        viewModelScope.launch {
            activacionDao.actualizar(actual.copy(estado = "Realizada"))
        }
    }

    fun registrarResultado(asistentes: String, leads: String, comentario: String): Boolean {
        val activacionActual = _activacion.value ?: return false
        if (activacionActual.estado != "Realizada") return false
        val asistentesNum = asistentes.trim().toIntOrNull() ?: return false
        val leadsNum = leads.trim().toIntOrNull() ?: return false
        if (asistentesNum < 0 || leadsNum < 0) return false
        viewModelScope.launch {
            resultadoDao.insertar(
                ResultadoEntity(
                    activacionId = activacionActual.id,
                    asistentesReales = asistentesNum,
                    leads = leadsNum,
                    comentario = comentario.trim()
                )
            )
        }
        return true
    }
}