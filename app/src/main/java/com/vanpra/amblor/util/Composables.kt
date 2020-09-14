package com.vanpra.amblor.util

import androidx.compose.foundation.Image
import androidx.compose.foundation.Text
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Stack
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.IconButton
import androidx.compose.material.MaterialTheme
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ChevronLeft
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun BackButton(onClick: () -> Unit) {
    Row(Modifier.height(56.dp), verticalGravity = Alignment.CenterVertically) {
        Image(
            Icons.Default.ArrowBack,
            modifier = Modifier.size(40.dp).clickable(onClick = onClick).padding(start = 8.dp),
            contentScale = ContentScale.None,
            colorFilter = ColorFilter.tint(MaterialTheme.colors.onBackground)
        )
    }
}

@Composable
fun SignUpTitle(title: String, onBackClick: () -> Unit) {
    Stack(Modifier.fillMaxWidth().padding(top = 8.dp)) {
        IconButton(onClick = { onBackClick() }) {
            Image(
                Icons.Default.ChevronLeft,
                colorFilter = MaterialTheme.colors.onBackground.filter(),
                modifier = Modifier.gravity(Alignment.Center)
            )
        }

        Text(
            title,
            modifier = Modifier.gravity(Alignment.Center),
            color = MaterialTheme.colors.onBackground,
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold
        )
    }
}

@Composable
fun ErrorWrapper(isError: Boolean, errorMessage: String, children: @Composable () -> Unit) {
    Column(Modifier.fillMaxWidth()) {
        children()
        if (isError) {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
                Text(
                    errorMessage,
                    style = MaterialTheme.typography.body2,
                    color = MaterialTheme.colors.error
                )
            }
        }
    }
}
