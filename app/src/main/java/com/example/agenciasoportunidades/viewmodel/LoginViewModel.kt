package com.example.agenciasoportunidades.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.agenciasoportunidades.model.LoginErrores
import com.example.agenciasoportunidades.model.LoginUiState
import com.example.agenciasoportunidades.model.data.local.AppDatabase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class LoginViewModel(app: Application) : AndroidViewModel(app) {

    private val dao = AppDatabase.getDatabase(app).usuarioDao()

    private val _estado = MutableStateFlow(LoginUiState())

    val estado: StateFlow<LoginUiState> = _estado

    private val formatoCorreo = Regex("^[\\w.+-]+@[\\w-]+(\\.[\\w-]+)+$")

    fun onCorreoChange(valor: String) {
        _estado.update {
            it.copy(
                correo = valor,
                mensajeError = null,
                errores = it.errores.copy(correo = null)
            )
        }
    }

    fun onClaveChange(valor: String) {
        _estado.update {
            it.copy(
                clave = valor,
                mensajeError = null,
                errores = it.errores.copy(clave = null)
            )
        }
    }

    fun validarFormulario(): Boolean {
        val estadoActual = _estado.value

        val errores = LoginErrores(
            correo = when {
                estadoActual.correo.isBlank() -> "El correo es obligatorio"
                !formatoCorreo.matches(estadoActual.correo.trim()) -> "Ingresa un correo válido (ej: nombre@empresa.cl)"
                else -> null
            },
            clave = when {
                estadoActual.clave.isBlank() -> "La clave es obligatoria"
                estadoActual.clave.length < 6 -> "La clave debe tener al menos 6 caracteres"
                else -> null
            }
        )

        val hayErrores = listOfNotNull(
            errores.correo,
            errores.clave
        ).isNotEmpty()

        _estado.update {
            it.copy(errores = errores)
        }

        return !hayErrores
    }

    fun iniciarSesion(onListo: () -> Unit) {
        val a = _estado.value
        viewModelScope.launch {
            val usuario = dao.buscarPorCorreo(a.correo.trim().lowercase())
            if (usuario != null && usuario.clave == a.clave) {
                _estado.update {
                    it.copy(usuarioNombre = usuario.nombre, clave = "")
                }
                onListo()
            } else {
                _estado.update {
                    it.copy(mensajeError = "Correo o clave incorrectos")
                }
            }
        }
    }

    fun cerrarSesion() {
        _estado.value = LoginUiState()
    }
}
