package com.vanpra.amblor.ui.theme

import androidx.compose.material.Typography
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import com.vanpra.amblor.R

val notoSans = FontFamily(
    listOf(
        Font(
            resId = R.font.noto_sans_regular,
            weight = FontWeight.W400,
            style = FontStyle.Normal
        )
    )
)

val typography = Typography(
    body1 = TextStyle(
        fontFamily = notoSans,
        fontWeight = FontWeight.Normal,
        fontSize = 16.sp
    )
)
