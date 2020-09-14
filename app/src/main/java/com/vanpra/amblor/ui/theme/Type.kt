package com.vanpra.amblor.ui.theme

import androidx.compose.material.Typography
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.font.font
import androidx.compose.ui.text.font.fontFamily
import androidx.compose.ui.unit.sp
import com.vanpra.amblor.R

// Set of Material typography styles to start with

val notoSans = fontFamily(
    listOf(
        font(
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
    /* Other default text styles to override
button = TextStyle(
fontFamily = FontFamily.Default,
fontWeight = FontWeight.W500,
fontSize = 14.sp
),
caption = TextStyle(
fontFamily = FontFamily.Default,
fontWeight = FontWeight.Normal,
fontSize = 12.sp
)
*/
)
