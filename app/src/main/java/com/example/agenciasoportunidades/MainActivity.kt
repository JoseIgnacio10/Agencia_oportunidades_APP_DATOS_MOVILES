package com.example.agenciasoportunidades

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.agenciasoportunidades.ui.screens.FormularioScreen
import com.example.agenciasoportunidades.ui.screens.ListaScreen
import com.example.agenciasoportunidades.ui.theme.AgenciasOportunidadesTheme
import com.example.agenciasoportunidades.viewmodel.OportunidadViewModel
import com.example.agenciasoportunidades.viewmodel.ViewModelFactory

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            AgenciasOportunidadesTheme {
                // Un solo ViewModel compartido: lo que se guarda en el formulario aparece en la lista
                val oportunidadViewModel: OportunidadViewModel =
                    viewModel(factory = ViewModelFactory(LocalContext.current))

                // Conexión mínima entre pantallas para poder probarlas.
                // Se reemplaza por el NavHost de navigation/ cuando esté la navegación.
                var mostrarFormulario by rememberSaveable { mutableStateOf(false) }

                if (mostrarFormulario) {
                    // El botón "atrás" del teléfono vuelve a la lista
                    BackHandler { mostrarFormulario = false }
                    FormularioScreen(
                        viewModel = oportunidadViewModel,
                        onGuardado = { mostrarFormulario = false },
                        onVolver = { mostrarFormulario = false }
                    )
                } else {
                    ListaScreen(
                        viewModel = oportunidadViewModel,
                        onNuevaOportunidad = { mostrarFormulario = true }
                    )
                }
            }
        }
    }
}
