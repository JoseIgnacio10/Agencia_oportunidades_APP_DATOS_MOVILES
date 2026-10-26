package com.example.agenciasoportunidades.navigation

import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import android.widget.Toast
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.agenciasoportunidades.ui.screens.FormularioScreen
import com.example.agenciasoportunidades.ui.screens.ListaScreen
import com.example.agenciasoportunidades.ui.screens.QrScannerScreen
import com.example.agenciasoportunidades.viewmodel.OportunidadViewModel

/**
 * Rutas de la aplicación para navegación estructurada.
 */
sealed class Screen(val route: String) {
    object Lista : Screen("lista_oportunidades")
    object Formulario : Screen("formulario_oportunidad")
    object EscanearQr : Screen("escanear_qr")
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
                onGuardado = { navController.popBackStack() },
                onEscanearQr = { navController.navigate(Screen.EscanearQr.route) }
            )
        }

        // Lector QR (recurso nativo 2): al leer un código vuelve al formulario ya completado
        composable(
            route = Screen.EscanearQr.route,
            enterTransition = { fadeIn(animationSpec = tween(350)) },
            exitTransition = { fadeOut(animationSpec = tween(350)) }
        ) {
            val context = LocalContext.current
            QrScannerScreen(
                onQrLeido = { texto ->
                    // El ViewModel interpreta el QR; aquí solo se informa el resultado
                    val cargado = viewModel.cargarDesdeQr(texto)
                    val mensaje = if (cargado) "Datos del evento cargados desde el QR"
                    else "Este QR no corresponde a un evento"
                    Toast.makeText(context, mensaje, Toast.LENGTH_SHORT).show()
                    navController.popBackStack()
                },
                onVolver = { navController.popBackStack() }
            )
        }
    }
}