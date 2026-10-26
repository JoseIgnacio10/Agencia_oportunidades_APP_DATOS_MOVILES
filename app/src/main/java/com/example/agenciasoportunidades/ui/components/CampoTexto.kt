package com.example.agenciasoportunidades.ui.components

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Error
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector

// Campo reutilizable para todos los formularios.
// Solo MUESTRA el error: la validación la decide el ViewModel (la UI no valida nada).
@Composable
fun CampoTexto(
    valor: String,
    onValorChange: (String) -> Unit,
    etiqueta: String,
    icono: ImageVector,
    error: String?,
    modifier: Modifier = Modifier,
    placeholder: String? = null,
    keyboardOptions: KeyboardOptions = KeyboardOptions.Default,
    singleLine: Boolean = true
) {
    OutlinedTextField(
        value = valor,
        onValueChange = onValorChange,
        label = { Text(etiqueta) },
        placeholder = placeholder?.let { { Text(it) } },
        // Ícono que identifica el campo
        leadingIcon = { Icon(icono, contentDescription = null) },
        // Ícono rojo de error solo cuando el ViewModel informa un error
        trailingIcon = {
            if (error != null) {
                Icon(
                    Icons.Filled.Error,
                    contentDescription = "Error",
                    tint = MaterialTheme.colorScheme.error
                )
            }
        },
        isError = error != null,
        // Mensaje de error bajo el campo
        supportingText = { if (error != null) Text(error) },
        keyboardOptions = keyboardOptions,
        singleLine = singleLine,
        modifier = modifier.fillMaxWidth()
    )
}
