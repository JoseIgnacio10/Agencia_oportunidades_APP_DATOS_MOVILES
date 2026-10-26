package com.example.agenciasoportunidades.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.agenciasoportunidades.data.OportunidadEntity
import com.example.agenciasoportunidades.data.OportunidadRepository
import com.example.agenciasoportunidades.model.DatosQrEvento
import com.example.agenciasoportunidades.model.OportunidadFormState
import com.example.agenciasoportunidades.model.OportunidadListState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class OportunidadViewModel(
 private val repository: OportunidadRepository
) : ViewModel() {

 // estateflows
 private val _formState = MutableStateFlow(OportunidadFormState())
 val formState: StateFlow<OportunidadFormState> = _formState.asStateFlow()

 private val _listState = MutableStateFlow(OportunidadListState())
 val listState: StateFlow<OportunidadListState> = _listState.asStateFlow()


 private var listaCompleta: List<OportunidadEntity> = emptyList()

 // Variables
 private val _textoBusqueda = MutableStateFlow("")
 val textoBusqueda: StateFlow<String> = _textoBusqueda.asStateFlow()

 init {
  cargarOportunidades()
 }

 // 2. Consulta  a Room
 private fun cargarOportunidades() {
  viewModelScope.launch {
   _listState.value = _listState.value.copy(estaCargando = true)
   repository.obtenerTodasLasOportunidades().collectLatest { lista ->
    listaCompleta = lista
    aplicarFiltros()
   }
  }
 }

 // Filtros
 fun onTextoBusquedaChanged(nuevoTexto: String) {
  _textoBusqueda.value = nuevoTexto
  aplicarFiltros()
 }

 private fun aplicarFiltros() {
  val query = _textoBusqueda.value.lowercase().trim()
  val listaFiltrada = if (query.isEmpty()) {
   listaCompleta
  } else {
   listaCompleta.filter { op ->
    op.titulo.lowercase().contains(query) ||
            op.organizacion.lowercase().contains(query) ||
            op.ubicacion.lowercase().contains(query) ||
            op.tipoEvento.lowercase().contains(query)
   }
  }
  _listState.value = _listState.value.copy(
   oportunidades = listaFiltrada,
   estaCargando = false
  )
 }

 // validaciones
 fun onTituloChanged(nuevoTitulo: String) {
  val error = if (nuevoTitulo.isBlank()) "El título es obligatorio" else null
  _formState.value = _formState.value.copy(
   titulo = nuevoTitulo,
   tituloError = error
  )
  validarFormularioGlobal()
 }

 fun onOrganizacionChanged(nuevaOrganizacion: String) {
  val error = if (nuevaOrganizacion.isBlank()) "La organización es obligatoria" else null
  _formState.value = _formState.value.copy(
   organizacion = nuevaOrganizacion,
   organizacionError = error
  )
  validarFormularioGlobal()
 }

 fun onTipoEventoChanged(nuevoTipo: String) {
  _formState.value = _formState.value.copy(tipoEvento = nuevoTipo)
  validarFormularioGlobal()
 }

 fun onPublicoObjetivoChanged(nuevoPublico: String) {
  val error = if (nuevoPublico.isBlank()) "El público objetivo es obligatorio" else null
  _formState.value = _formState.value.copy(
   publicoObjetivo = nuevoPublico,
   publicoObjetivoError = error
  )
  validarFormularioGlobal()
 }

 fun onUbicacionChanged(nuevaUbicacion: String) {
  val error = if (nuevaUbicacion.isBlank()) "La ubicación es obligatoria" else null
  _formState.value = _formState.value.copy(
   ubicacion = nuevaUbicacion,
   ubicacionError = error
  )
  validarFormularioGlobal()
 }

 fun onFechaChanged(nuevaFecha: String) {
  val error = if (nuevaFecha.isBlank()) "La fecha es obligatoria" else null
  _formState.value = _formState.value.copy(
   fecha = nuevaFecha,
   fechaError = error
  )
  validarFormularioGlobal()
 }

 fun onFotoPathChanged(nuevaRutaFoto: String?) {
  _formState.value = _formState.value.copy(
   fotoPath = nuevaRutaFoto,
   fotoError = if (nuevaRutaFoto == null) "Debe adjuntar una foto de evidencia" else null
  )
  validarFormularioGlobal()
 }

 // Autocompletar desde un QR (recurso nativo 2).
 // Reutiliza las mismas funciones de cada campo, así los datos del QR pasan por las mismas validaciones.
 // Devuelve false si el QR no corresponde a un evento.
 fun cargarDesdeQr(textoQr: String): Boolean {
  val datos = DatosQrEvento.desdeTexto(textoQr) ?: return false
  datos.titulo?.let { onTituloChanged(it) }
  datos.organizacion?.let { onOrganizacionChanged(it) }
  datos.tipoEvento?.let { onTipoEventoChanged(it) }
  datos.publicoObjetivo?.let { onPublicoObjetivoChanged(it) }
  datos.ubicacion?.let { onUbicacionChanged(it) }
  datos.fecha?.let { onFechaChanged(it) }
  return true
 }

 // Validación global
 private fun validarFormularioGlobal() {
  val state = _formState.value
  val esValido = state.titulo.isNotBlank() &&
          state.organizacion.isNotBlank() &&
          state.publicoObjetivo.isNotBlank() &&
          state.ubicacion.isNotBlank() &&
          state.fecha.isNotBlank()

  _formState.value = state.copy(esFormularioValido = esValido)
 }

 //  base de datos
 fun guardarOportunidad(onExito: () -> Unit) {
  val state = _formState.value
  if (state.esFormularioValido) {
   viewModelScope.launch {
    val nuevaOportunidad = OportunidadEntity(
     titulo = state.titulo,
     organizacion = state.organizacion,
     tipoEvento = state.tipoEvento,
     publicoObjetivo = state.publicoObjetivo,
     ubicacion = state.ubicacion,
     fecha = state.fecha,
     fotoPath = state.fotoPath
    )
    repository.insertarOportunidad(nuevaOportunidad)
    limpiarFormulario()
    onExito()
   }
  }
 }

 fun eliminarOportunidad(oportunidad: OportunidadEntity) {
  viewModelScope.launch {
   repository.eliminarOportunidad(oportunidad)
  }
 }

 private fun limpiarFormulario() {
  _formState.value = OportunidadFormState()
 }
}