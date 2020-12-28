package com.vanpra.amblor.ui.layouts.auth

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.material.Button
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.AmbientView
import androidx.compose.ui.unit.dp
import com.vanpra.amblor.util.BackButtonTitle
import com.vanpra.amblor.util.ErrorOutlinedTextField
import com.vanpra.amblor.util.enabledColor

@Composable
fun GoogleSignup() {
    val username = TextInputState("Username", "Username Taken")
    val hostView = AmbientView.current

    Column(
        Modifier.fillMaxSize()
            .background(MaterialTheme.colors.background)
            .clickable(onClick = { hostView.clearFocus() }, indication = null)
    ) {
        BackButtonTitle(title = "Enter a username") {
            // Signout from google
        }
        Column(Modifier.padding(start = 16.dp, end = 16.dp)) {
            ErrorOutlinedTextField(inputState = username)

            val canSignIn = username.text != ""
            Button(
                onClick = {
                    // Send username to server
                },
                modifier = Modifier.fillMaxWidth().padding(vertical = 24.dp)
                    .background(MaterialTheme.colors.primaryVariant.enabledColor(canSignIn)),
                enabled = canSignIn
            ) {
                Text(
                    "Complete Sign up",
                    Modifier.wrapContentWidth(Alignment.CenterHorizontally),
                    color = MaterialTheme.colors.onPrimary.enabledColor(canSignIn)
                )
            }
        }
    }
}
