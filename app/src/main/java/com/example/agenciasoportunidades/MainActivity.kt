package com.example.agenciasoportunidades

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.compose.rememberNavController
import com.example.agenciasoportunidades.navigation.AppNavigation
import com.example.agenciasoportunidades.ui.theme.AgenciasOportunidadesTheme
import com.example.agenciasoportunidades.viewmodel.LoginViewModel
import com.example.agenciasoportunidades.viewmodel.OportunidadViewModel
import com.example.agenciasoportunidades.viewmodel.ViewModelFactory

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val factory = ViewModelFactory(applicationContext)
        val viewModel = ViewModelProvider(this, factory)[OportunidadViewModel::class.java]
        // Sesión del usuario: se comparte entre el login y la lista (para cerrar sesión)
        val loginViewModel = ViewModelProvider(this, factory)[LoginViewModel::class.java]

        setContent {
            AgenciasOportunidadesTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    val navController = rememberNavController()
                    AppNavigation(
                        navController = navController,
                        viewModel = viewModel,
                        loginViewModel = loginViewModel
                    )
                }
            }
        }
    }
}