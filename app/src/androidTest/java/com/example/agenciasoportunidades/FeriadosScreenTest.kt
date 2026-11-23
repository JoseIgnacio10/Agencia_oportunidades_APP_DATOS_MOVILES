package com.example.agenciasoportunidades

import androidx.activity.ComponentActivity
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.navigation.compose.rememberNavController
import com.example.agenciasoportunidades.model.Feriado
import com.example.agenciasoportunidades.ui.screens.FeriadosScreen
import com.example.agenciasoportunidades.viewmodel.FeriadoViewModel
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.flow.MutableStateFlow
import org.junit.Rule
import org.junit.Test

class FeriadosScreenTest {

    @get:Rule
    val composeRule = createAndroidComposeRule<ComponentActivity>()

    @Test
    fun feriadosScreen_muestraTituloYListadoDeFeriados() {
        val feriadosFalsos = listOf(
            Feriado(
                date = "2026-09-18",
                localName = "Independencia Nacional",
                name = "Independence Day"
            ),
            Feriado(
                date = "2026-12-25",
                localName = "Navidad",
                name = "Christmas Day"
            )
        )

        val feriadoViewModelFalso = mockk<FeriadoViewModel>(relaxed = true)
        every { feriadoViewModelFalso.anio } returns 2026
        every { feriadoViewModelFalso.feriadoList } returns MutableStateFlow(feriadosFalsos)
        every { feriadoViewModelFalso.mensajeError } returns MutableStateFlow(null)

        composeRule.setContent {
            val navController = rememberNavController()
            FeriadosScreen(
                navController = navController,
                viewModel = feriadoViewModelFalso
            )
        }

        composeRule.onNodeWithText("Feriados de Chile 2026").assertIsDisplayed()
        composeRule.onNodeWithText("Independencia Nacional").assertIsDisplayed()
        composeRule.onNodeWithText("18/09/2026").assertIsDisplayed()
    }
}
