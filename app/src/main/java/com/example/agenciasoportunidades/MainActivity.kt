package com.example.agenciasoportunidades

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import com.example.agenciasoportunidades.navigation.AppNavigation
import com.example.agenciasoportunidades.ui.theme.AgenciasOportunidadesTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            AgenciasOportunidadesTheme {
                AppNavigation()
            }
        }
    }
}