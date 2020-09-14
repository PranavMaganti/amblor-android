package com.vanpra.amblor.util

import androidx.compose.material.EmphasisAmbient
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import kotlinx.datetime.Instant
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toJavaLocalDateTime
import kotlinx.datetime.toLocalDateTime
import java.time.LocalDate

@Composable
fun Color.enabledColor(bool: Boolean): Color = if (bool) {
    this
} else {
    EmphasisAmbient.current.disabled.applyEmphasis(this)
}

@Composable
fun Color.filter() = ColorFilter.tint(this)

fun Instant.toJavaDate(): LocalDate =
    this.toLocalDateTime(TimeZone.currentSystemDefault()).toJavaLocalDateTime().toLocalDate()
