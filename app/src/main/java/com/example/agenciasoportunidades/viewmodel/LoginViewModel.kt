package com.example.agenciasoportunidades.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.agenciasoportunidades.data.UsuarioRepository
import com.example.agenciasoportunidades.model.LoginUiState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

// Lógica del login: valida los campos y consulta la cuenta en Room.
// La pantalla solo muestra este estado; no valida nada por su cuenta.
class LoginViewModel(
    private val repository: UsuarioRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(LoginUiState())
    val uiState: StateFlow<LoginUiState> = _uiState.asStateFlow()

    // Formato básico de correo: texto@texto.dominio
    private val formatoCorreo = Regex("^[\\w.+-]+@[\\w-]+(\\.[\\w-]+)+$")

    fun onCorreoChanged(nuevoCorreo: String) {
        val error = when {
            nuevoCorreo.isBlank() -> "El correo es obligatorio"
            !formatoCorreo.matches(nuevoCorreo.trim()) -> "Ingresa un correo válido (ej: nombre@empresa.cl)"
            else -> null
        }
        // mensajeError = null: al corregir un dato se limpia el aviso de "credenciales incorrectas"
        _uiState.update { it.copy(correo = nuevoCorreo, correoError = error, mensajeError = null) }
        validarFormulario()
    }

    fun onClaveChanged(nuevaClave: String) {
        val error = when {
            nuevaClave.isBlank() -> "La clave es obligatoria"
            nuevaClave.length < 6 -> "La clave debe tener al menos 6 caracteres"
            else -> null
        }
        _uiState.update { it.copy(clave = nuevaClave, claveError = error, mensajeError = null) }
        validarFormulario()
    }

    private fun validarFormulario() {
        _uiState.update {
            it.copy(
                esFormularioValido = it.correo.isNotBlank() && it.clave.isNotBlank() &&
                        it.correoError == null && it.claveError == null
            )
        }
    }

    fun iniciarSesion() {
        val estado = _uiState.value
        if (!estado.esFormularioValido || estado.estaCargando) return

        viewModelScope.launch {
            _uiState.update { it.copy(estaCargando = true, mensajeError = null) }
            val usuario = repository.validarCredenciales(estado.correo, estado.clave)
            _uiState.update {
                if (usuario != null) {
                    // Sesión iniciada: se borra la clave del estado por seguridad
                    it.copy(estaCargando = false, usuarioNombre = usuario.nombre, clave = "")
                } else {
                    it.copy(estaCargando = false, mensajeError = "Correo o clave incorrectos")
                }
            }
        }
    }

    // Control de sesión: vuelve al estado inicial (sin usuario)
    fun cerrarSesion() {
        _uiState.value = LoginUiState()
    }
}
