package com.example.agenciasoportunidades.navigation

import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.runtime.Composable
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.agenciasoportunidades.ui.screens.EventoFormularioScreen
import com.example.agenciasoportunidades.ui.screens.EventosScreen
import com.example.agenciasoportunidades.ui.screens.FormularioScreen
import com.example.agenciasoportunidades.ui.screens.ListaScreen
import com.example.agenciasoportunidades.ui.screens.LoginScreen
import com.example.agenciasoportunidades.ui.screens.QrScannerScreen
import com.example.agenciasoportunidades.viewmodel.EventoViewModel
import com.example.agenciasoportunidades.viewmodel.LoginViewModel
import com.example.agenciasoportunidades.viewmodel.OportunidadViewModel

@Composable
fun AppNavigation() {
    val navController = rememberNavController()

    val loginViewModel: LoginViewModel = viewModel()
    val oportunidadViewModel: OportunidadViewModel = viewModel()
    val eventoViewModel: EventoViewModel = viewModel()

    NavHost(
        navController = navController,
        startDestination = "login"
    ) {
        composable(
            route = "login",
            enterTransition = { fadeIn(animationSpec = tween(350)) },
            exitTransition = { fadeOut(animationSpec = tween(350)) }
        ) {
            LoginScreen(navController, loginViewModel)
        }

        composable(
            route = "lista",
            enterTransition = {
                slideIntoContainer(AnimatedContentTransitionScope.SlideDirection.Right, tween(350)) +
                        fadeIn(animationSpec = tween(350))
            },
            exitTransition = {
                slideOutOfContainer(AnimatedContentTransitionScope.SlideDirection.Left, tween(350)) +
                        fadeOut(animationSpec = tween(350))
            }
        ) {
            ListaScreen(navController, oportunidadViewModel, loginViewModel)
        }

        composable(
            route = "formulario",
            enterTransition = {
                slideIntoContainer(AnimatedContentTransitionScope.SlideDirection.Left, tween(350)) +
                        fadeIn(animationSpec = tween(350))
            },
            exitTransition = {
                slideOutOfContainer(AnimatedContentTransitionScope.SlideDirection.Right, tween(350)) +
                        fadeOut(animationSpec = tween(350))
            }
        ) {
            FormularioScreen(navController, oportunidadViewModel)
        }

        composable(
            route = "escanear_qr",
            enterTransition = { fadeIn(animationSpec = tween(350)) },
            exitTransition = { fadeOut(animationSpec = tween(350)) }
        ) {
            QrScannerScreen(navController, oportunidadViewModel)
        }

        composable (
            route = "eventos",
            enterTransition = { fadeIn(animationSpec = tween(350))},
            exitTransition = { fadeOut(animationSpec = tween (350))}
        ) {
            EventosScreen(navController, eventoViewModel)
        }

        composable(
            route = "evento_formulario",
            enterTransition = {
                slideIntoContainer(AnimatedContentTransitionScope.SlideDirection.Left, tween(350)) +
                        fadeIn(animationSpec = tween(350))
            },
            exitTransition = {
                slideOutOfContainer(AnimatedContentTransitionScope.SlideDirection.Right, tween(350)) +
                        fadeOut(animationSpec = tween(350))
            }
        ) {
            EventoFormularioScreen(navController, eventoViewModel)
        }
    }
}
