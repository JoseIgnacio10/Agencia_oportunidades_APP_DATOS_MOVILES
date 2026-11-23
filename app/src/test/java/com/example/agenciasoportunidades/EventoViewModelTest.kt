package com.example.agenciasoportunidades

import com.example.agenciasoportunidades.model.Evento
import com.example.agenciasoportunidades.repository.EventoRepository
import com.example.agenciasoportunidades.viewmodel.EventoViewModel
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
class EventoViewModelTest : StringSpec({

    val dispatcher = StandardTestDispatcher()

    beforeSpec {
        Dispatchers.setMain(dispatcher)
    }

    afterSpec {
        Dispatchers.resetMain()
    }

    "eventoList debe contener los datos retornados por el repositorio" {
        runTest(dispatcher) {
            val eventosFalsos = listOf(
                Evento(
                    id = "1",
                    nombre = "Feria Corporativa Santiago",
                    tipo = "Feria empresarial",
                    descripcion = "Activacion de convenios dentales",
                    fecha = "15/11/2026",
                    horario = "09:00 a 14:00",
                    comuna = "Providencia",
                    region = "Metropolitana",
                    organizacion = "Corporacion Ficticia Central",
                    asistentesEstimados = 120,
                    condiciones = "Stand techado"
                ),
                Evento(
                    id = "2",
                    nombre = "Jornada Beneficios Duoc",
                    tipo = "Jornada de beneficios",
                    descripcion = "Atencion directa a colaboradores",
                    fecha = "20/11/2026",
                    horario = "10:00 a 16:00",
                    comuna = "Puente Alto",
                    region = "Metropolitana",
                    organizacion = "Municipalidad Ficticia Sur",
                    asistentesEstimados = 80,
                    condiciones = "Acceso vehicular"
                )
            )

            val repositorioFalso = mockk<EventoRepository>()
            coEvery { repositorioFalso.getEventos() } returns eventosFalsos

            val viewModel = EventoViewModel(repositorioFalso)
            advanceUntilIdle()

            viewModel.eventoList.value shouldContainExactly eventosFalsos
        }
    }

    "validarFormulario debe retornar false si los campos obligatorios estan vacios" {
        val repositorioFalso = mockk<EventoRepository>(relaxed = true)
        val viewModel = EventoViewModel(repositorioFalso)

        val esValido = viewModel.validarFormulario()

        esValido shouldBe false
    }
})