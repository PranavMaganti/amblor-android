package com.vanpra.amblor.util

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.IconButton
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ChevronLeft
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focusRequester
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.AmbientFocusManager
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.vanpra.amblor.ui.layouts.auth.TextInputState

@Composable
fun BackButton(onClick: () -> Unit) {
    Row(Modifier.height(56.dp), verticalAlignment = Alignment.CenterVertically) {
        Image(
            Icons.Default.ArrowBack,
            modifier = Modifier.size(40.dp).clickable(onClick = onClick).padding(start = 8.dp),
            contentScale = ContentScale.None,
            colorFilter = ColorFilter.tint(MaterialTheme.colors.onBackground)
        )
    }
}

@Composable
fun BackButtonTitle(title: String, onBackClick: () -> Unit) {
    Box(Modifier.fillMaxWidth().padding(top = 8.dp)) {
        IconButton(onClick = { onBackClick() }) {
            Image(
                Icons.Default.ChevronLeft,
                colorFilter = MaterialTheme.colors.onBackground.filter(),
                modifier = Modifier.align(Alignment.Center)
            )
        }

        Text(
            title,
            modifier = Modifier.align(Alignment.Center),
            color = MaterialTheme.colors.onBackground,
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold
        )
    }
}

@Composable
fun LoadingScreen() {
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        CircularProgressIndicator()
    }
}

@Composable
private fun ErrorWrapper(
    textInputState: TextInputState,
    showErrorText: Boolean,
    testTag: String,
    children: @Composable () -> Unit
) {
    Column(Modifier.fillMaxWidth()) {
        children()
        if (textInputState.isError() && showErrorText) {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
                Text(
                    textInputState.errorMessage,
                    style = MaterialTheme.typography.body2,
                    color = MaterialTheme.colors.error,
                    modifier = Modifier.testTag(testTag)
                )
            }
        }
    }
}

@Composable
fun ErrorOutlinedTextField(
    inputState: TextInputState,
    modifier: Modifier = Modifier,
    testTag: String = "",
    keyboardOptions: KeyboardOptions = KeyboardOptions(),
    nextInput: TextInputState? = null,
    visualTransformation: VisualTransformation = VisualTransformation.None,
    showErrorText: Boolean = true
) {
    val focusManager = AmbientFocusManager.current
    ErrorWrapper(inputState, showErrorText, testTag + "_error") {
        androidx.compose.material.OutlinedTextField(
            value = inputState.text,
            modifier = modifier.fillMaxWidth().focusRequester(inputState.focusRequester)
                .testTag(testTag),
            label = { Text(inputState.label) },
            keyboardOptions = keyboardOptions.copy(autoCorrect = false),
            onValueChange = { value -> inputState.text = value },
            isErrorValue = inputState.isError(),
            onTextInputStarted = {
                inputState.resetError()
            },
            visualTransformation = visualTransformation,
            onImeActionPerformed = { imeAction, _ ->
                if (imeAction == ImeAction.Next) {
                    nextInput?.focusRequester?.requestFocus()
                } else if (imeAction == ImeAction.Done) {
                    focusManager.clearFocus()
                }
            },
            textStyle = TextStyle(MaterialTheme.colors.onBackground, fontSize = 16.sp)
        )
    }
}
