package com.example.agenciasoportunidades

import androidx.activity.ComponentActivity
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.navigation.compose.rememberNavController
import com.example.agenciasoportunidades.model.Evento
import com.example.agenciasoportunidades.model.LoginUiState
import com.example.agenciasoportunidades.ui.screens.EventosScreen
import com.example.agenciasoportunidades.viewmodel.EventoViewModel
import com.example.agenciasoportunidades.viewmodel.LoginViewModel
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.flow.MutableStateFlow
import org.junit.Rule
import org.junit.Test

class EventosScreenTest {

    @get:Rule
    val composeRule = createAndroidComposeRule<ComponentActivity>()

    @Test
    fun eventosScreen_muestraTituloYListadoDeEventos() {
        val eventosFalsos = listOf(
            Evento(
                id = "1",
                nombre = "Feria Corporativa Santiago",
                tipo = "Feria empresarial",
                descripcion = "Activacion comercial",
                fecha = "15/11/2026",
                horario = "09:00 a 14:00",
                comuna = "Providencia",
                region = "Metropolitana",
                organizacion = "Corporacion Central",
                asistentesEstimados = 100,
                condiciones = "Stand techado"
            )
        )

        val eventoViewModelFalso = mockk<EventoViewModel>(relaxed = true)
        every { eventoViewModelFalso.eventoList } returns MutableStateFlow(eventosFalsos)
        every { eventoViewModelFalso.mensajeError } returns MutableStateFlow(null)

        val loginEstadoFalso = mockk<LoginUiState>(relaxed = true)
        every { loginEstadoFalso.esSupervisor } returns true

        val loginViewModelFalso = mockk<LoginViewModel>(relaxed = true)
        every { loginViewModelFalso.estado } returns MutableStateFlow(loginEstadoFalso)

        composeRule.setContent {
            val navController = rememberNavController()
            EventosScreen(
                navController = navController,
                viewModel = eventoViewModelFalso,
                loginViewModel = loginViewModelFalso
            )
        }

        composeRule.onNodeWithText("Catálogo de eventos").assertIsDisplayed()
        composeRule.onNodeWithText("Feria Corporativa Santiago").assertIsDisplayed()
        composeRule.onNodeWithText("Nuevo evento").assertIsDisplayed()
    }
}