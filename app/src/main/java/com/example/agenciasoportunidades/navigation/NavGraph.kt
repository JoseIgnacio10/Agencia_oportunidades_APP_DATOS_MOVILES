package com.example.agenciasoportunidades.navigation

import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.agenciasoportunidades.ui.screens.FormularioScreen
import com.example.agenciasoportunidades.ui.screens.ListaScreen
import com.example.agenciasoportunidades.viewmodel.OportunidadViewModel

/**
 * Rutas de la aplicación para navegación estructurada.
 */
sealed class Screen(val route: String) {
    object Lista : Screen("lista_oportunidades")
    object Formulario : Screen("formulario_oportunidad")
}

/**
 * Grafaas de navegación central con animaciones entre pantallas (Cumple IE 2.1.1 y IE 2.2.2).
 */
@Composable
fun AppNavigation(
    navController: NavHostController,
    viewModel: OportunidadViewModel
) {
    NavHost(
        navController = navController,
        startDestination = Screen.Lista.route
    ) {
        // Pantalla de Lista con animación lateral
        composable(
            route = Screen.Lista.route,
            enterTransition = {
                slideIntoContainer(
                    towards = AnimatedContentTransitionScope.SlideDirection.Right,
                    animationSpec = tween(350)
                ) + fadeIn(animationSpec = tween(350))
            },
            exitTransition = {
                slideOutOfContainer(
                    towards = AnimatedContentTransitionScope.SlideDirection.Left,
                    animationSpec = tween(350)
                ) + fadeOut(animationSpec = tween(350))
            }
        ) {
            ListaScreen(
                viewModel = viewModel,
                onNuevaOportunidad = { navController.navigate(Screen.Formulario.route) }
            )
        }

        // Pantalla de Formulario con animación lateral
        composable(
            route = Screen.Formulario.route,
            enterTransition = {
                slideIntoContainer(
                    towards = AnimatedContentTransitionScope.SlideDirection.Left,
                    animationSpec = tween(350)
                ) + fadeIn(animationSpec = tween(350))
            },
            exitTransition = {
                slideOutOfContainer(
                    towards = AnimatedContentTransitionScope.SlideDirection.Right,
                    animationSpec = tween(350)
                ) + fadeOut(animationSpec = tween(350))
            }
        ) {
            FormularioScreen(
                viewModel = viewModel,
                onVolver = { navController.popBackStack() },
                onGuardado = { navController.popBackStack() }
            )
        }
    }
}