package com.example.agenciasoportunidades

import androidx.activity.ComponentActivity
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.hasText
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.navigation.compose.rememberNavController
import com.example.agenciasoportunidades.model.Feriado
import com.example.agenciasoportunidades.repository.FeriadoRepository
import com.example.agenciasoportunidades.ui.screens.FeriadosScreen
import com.example.agenciasoportunidades.viewmodel.FeriadoViewModel
import io.mockk.coEvery
import io.mockk.mockk
import org.junit.Rule
import org.junit.Test

class FeriadosScreenTest {

    @get:Rule
    val composeRule = createAndroidComposeRule<ComponentActivity>()

    @Test
    fun el_nombre_del_feriado_debe_aparecer_en_pantalla() {

        val fakeFeriados = listOf(
            Feriado(date = "2026-09-18", localName = "Independencia Nacional", name = "Independence Day"),
            Feriado(date = "2026-12-25", localName = "Navidad", name = "Christmas Day")
        )

        val fakeRepository = mockk<FeriadoRepository>()

        coEvery { fakeRepository.getFeriados(any()) } returns fakeFeriados

        val fakeViewModel = FeriadoViewModel(fakeRepository)

        composeRule.setContent {
            FeriadosScreen(
                navController = rememberNavController(),
                viewModel = fakeViewModel
            )
        }

        composeRule.waitUntil(timeoutMillis = 10000) {
            composeRule.onAllNodes(hasText("Independencia Nacional"))
                .fetchSemanticsNodes().isNotEmpty()
        }

        composeRule.onNodeWithText("Independencia Nacional").assertIsDisplayed()
        composeRule.onNodeWithText("18/09/2026").assertIsDisplayed()
    }
}
