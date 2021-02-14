package com.vanpra.amblor.ui.layouts.auth

import androidx.compose.foundation.InteractionState
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import com.vanpra.amblor.util.BackButtonTitle
import com.vanpra.amblor.util.ErrorOutlinedTextField
import com.vanpra.amblor.util.PrimaryTextButton

@Composable
fun GoogleSignup(authViewModel: AuthViewModel) {
    val focusManager = LocalFocusManager.current

    Column(
        Modifier
            .fillMaxSize()
            .background(MaterialTheme.colors.background)
            .clickable(
                onClick = { focusManager.clearFocus() },
                indication = null,
                interactionState = InteractionState()
            )
    ) {
        BackButtonTitle(title = "Enter a username") {
            authViewModel.signOut(deleteUser = true)
        }
        Column(Modifier.padding(start = 16.dp, end = 16.dp)) {
            ErrorOutlinedTextField(
                inputState = authViewModel.googleSignupState,
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
            )

            PrimaryTextButton(
                text = "Complete Sign up",
                modifier = Modifier.padding(vertical = 24.dp),
                enabled = authViewModel.googleSignupState.text != "",
                onClick = { authViewModel.signUpWithGoogle() }
            )
        }
    }
}
