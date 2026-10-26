package com.example.agenciasoportunidades.ui.components

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.layout.Row
import androidx.compose.material.icons.filled.Error
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.IconButton
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
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
    singleLine: Boolean = true,
    // true para claves: oculta el texto y agrega el botón para verlo
    esClave: Boolean = false
) {
    var claveVisible by remember { mutableStateOf(false) }

    OutlinedTextField(
        value = valor,
        onValueChange = onValorChange,
        label = { Text(etiqueta) },
        placeholder = placeholder?.let { { Text(it) } },
        // Ícono que identifica el campo
        leadingIcon = { Icon(icono, contentDescription = null) },
        // Ícono rojo de error solo cuando el ViewModel informa un error
        trailingIcon = {
            Row(verticalAlignment = Alignment.CenterVertically) {
                // El ícono de error "aparece" creciendo, para llamar la atención sin ser brusco
                AnimatedVisibility(
                    visible = error != null,
                    enter = scaleIn() + fadeIn(),
                    exit = scaleOut() + fadeOut()
                ) {
                    Icon(
                        Icons.Filled.Error,
                        contentDescription = "Error",
                        tint = MaterialTheme.colorScheme.error
                    )
                }
                if (esClave) {
                    IconButton(onClick = { claveVisible = !claveVisible }) {
                        Icon(
                            if (claveVisible) Icons.Filled.VisibilityOff else Icons.Filled.Visibility,
                            contentDescription = if (claveVisible) "Ocultar clave" else "Mostrar clave"
                        )
                    }
                }
            }
        },
        // Las claves se muestran como puntos salvo que el usuario pida verlas
        visualTransformation = if (esClave && !claveVisible) PasswordVisualTransformation()
        else VisualTransformation.None,
        isError = error != null,
        // Mensaje de error bajo el campo: se despliega y se esconde con animación
        supportingText = {
            AnimatedVisibility(
                visible = error != null,
                enter = expandVertically() + fadeIn(),
                exit = shrinkVertically() + fadeOut()
            ) {
                Text(error.orEmpty())
            }
        },
        keyboardOptions = keyboardOptions,
        singleLine = singleLine,
        modifier = modifier.fillMaxWidth()
    )
}
