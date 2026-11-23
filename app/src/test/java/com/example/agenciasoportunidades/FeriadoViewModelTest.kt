package com.example.agenciasoportunidades

import com.example.agenciasoportunidades.model.Feriado
import com.example.agenciasoportunidades.repository.FeriadoRepository
import com.example.agenciasoportunidades.viewmodel.FeriadoViewModel
import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.collections.shouldContainExactly
import io.kotest.matchers.shouldBe
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain

@OptIn(ExperimentalCoroutinesApi::class)
class FeriadoViewModelTest : StringSpec({

    val dispatcher = StandardTestDispatcher()

    beforeSpec {
        Dispatchers.setMain(dispatcher)
    }

    afterSpec {
        Dispatchers.resetMain()
    }

    "feriadoList debe contener los feriados retornados por la API externa" {
        runTest(dispatcher) {
            val feriadosFalsos = listOf(
                Feriado(
                    date = "2026-01-01",
                    localName = "Ano Nuevo",
                    name = "New Year's Day"
                ),
                Feriado(
                    date = "2026-09-18",
                    localName = "Independencia Nacional",
                    name = "Independence Day"
                )
            )

            val repositorioFalso = mockk<FeriadoRepository>()
            coEvery { repositorioFalso.getFeriados(any()) } returns feriadosFalsos

            val viewModel = FeriadoViewModel(repositorioFalso)
            advanceUntilIdle()

            viewModel.feriadoList.value shouldContainExactly feriadosFalsos
            viewModel.mensajeError.value shouldBe null
        }
    }

    "mensajeError debe mostrar aviso si falla la conexion con la API de feriados" {
        runTest(dispatcher) {
            val repositorioFalso = mockk<FeriadoRepository>()
            coEvery { repositorioFalso.getFeriados(any()) } throws RuntimeException("Sin internet")

            val viewModel = FeriadoViewModel(repositorioFalso)
            advanceUntilIdle()

            viewModel.feriadoList.value shouldBe emptyList()
            viewModel.mensajeError.value shouldBe "No se pudieron obtener los feriados"
        }
    }
})