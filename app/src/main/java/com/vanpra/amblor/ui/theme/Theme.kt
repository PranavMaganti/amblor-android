package com.vanpra.amblor.ui.theme

import androidx.compose.foundation.background
import androidx.compose.material.Text
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.MaterialTheme
import androidx.compose.material.darkColors
import androidx.compose.material.lightColors
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview

private val DarkColorPalette = darkColors(
    primary = blue500,
    primaryVariant = blue700,
    secondary = blue200,
    onSurface = Color.White,
    onBackground = Color.White,
    onPrimary = Color.White,
    background = darkBackground
)

private val LightColorPalette = lightColors(
    primary = blue500,
    primaryVariant = blue700,
    onPrimary = Color.White,
    secondary = blue200
    /* Other default colors to override
background = Color.White,
surface = Color.White,
onPrimary = Color.White,
onSecondary = Color.Black,
onBackground = Color.Black,
onSurface = Color.Black,
*/
)

@Composable
fun AmblorTheme(darkTheme: Boolean = isSystemInDarkTheme(), content: @Composable () -> Unit) {
    val colors = if (darkTheme) {
        DarkColorPalette
    } else {
        LightColorPalette
    }

    MaterialTheme(
        colors = colors,
        typography = typography,
        shapes = shapes,
        content = content
    )
}

@Preview
@Composable
fun ThemePreview() {
    AmblorTheme(true) {
        Box(modifier = Modifier.fillMaxSize().background(MaterialTheme.colors.background)) {
            Text("This is random text", color = MaterialTheme.colors.onBackground)
        }
    }
}
