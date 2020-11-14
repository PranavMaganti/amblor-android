package com.vanpra.amblor.ui.layouts.login

import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.Box
import androidx.compose.foundation.Text
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.material.Button
import androidx.compose.material.MaterialTheme
import androidx.compose.material.OutlinedTextField
import androidx.compose.material.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.savedinstancestate.savedInstanceState
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.ContextAmbient
import androidx.compose.ui.platform.ViewAmbient
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import androidx.lifecycle.lifecycleScope
import com.vanpra.amblor.AuthAmbient
import com.vanpra.amblor.util.ErrorWrapper
import com.vanpra.amblor.util.SignUpTitle
import com.vanpra.amblor.util.enabledColor
import kotlinx.coroutines.launch

@Composable
fun GoogleSignup() {
    var username by savedInstanceState(saver = TextFieldValue.Saver) { TextFieldValue() }
    val hostView = ViewAmbient.current
    val activity = ContextAmbient.current as AppCompatActivity
    val authRepo = AuthAmbient.current

    var error by mutableStateOf(false)

    Column(
        Modifier.fillMaxSize()
            .background(MaterialTheme.colors.background)
            .clickable(onClick = { hostView.clearFocus() }, indication = null)
    ) {
        SignUpTitle(title = "Enter a username") { authRepo.googleSignOut() }
        Box(Modifier.padding(start = 16.dp, end = 16.dp)) {
            ErrorWrapper(isError = error, errorMessage = "Username Taken") {
                OutlinedTextField(
                    value = username,
                    onValueChange = { value -> username = value },
                    label = { Text("Username") },
                    isErrorValue = error,
                    modifier = Modifier.fillMaxWidth(),
                    imeAction = ImeAction.Done
                )
            }

            val canSignIn = username.text != ""
            Button(
                onClick = {
                    activity.lifecycleScope.launch {
                        authRepo.addNewUser(username.text) { error = true }
                    }
                },
                modifier = Modifier.fillMaxWidth().padding(vertical = 24.dp),
                backgroundColor = MaterialTheme.colors.primaryVariant.enabledColor(canSignIn),
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
