package com.vanpra.amblor.ui.layouts.auth

import android.app.Activity
import android.util.Patterns
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.InteractionState
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.navigation.compose.navigate
import com.vanpra.amblor.R
import com.vanpra.amblor.Screen
import com.vanpra.amblor.util.ErrorOutlinedTextField
import com.vanpra.amblor.util.PrimaryTextButton
import com.vanpra.amblor.util.TextInputState
import com.vanpra.amblor.util.registerForActivityResult
import com.vanpra.amblor.util.rememberTextInputState

@Composable
fun LoginLayout(authViewModel: AuthViewModel) {
    val focusManager = LocalFocusManager.current
    val email = rememberTextInputState("Email", defaultError = "Invalid Email") {
        Patterns.EMAIL_ADDRESS.matcher(it).matches()
    }
    val password = rememberTextInputState("Password")

    SideEffect { authViewModel.loadingScreenNavigation() }

    Box(
        Modifier
            .fillMaxSize()
            .clickable(
                onClick = { focusManager.clearFocus() },
                indication = null,
                interactionState = InteractionState()
            ),
        contentAlignment = Alignment.Center
    ) {
        Column(Modifier.fillMaxWidth(0.75f)) {
            Image(
                painter = painterResource(R.drawable.logo),
                contentDescription = null,
                modifier = Modifier.padding(start = 30.dp, end = 30.dp, bottom = 30.dp),
                colorFilter = ColorFilter.tint(MaterialTheme.colors.onBackground)
            )

            LoginInputLayout(email, password)
            LoginButtonLayout(authViewModel, email, password)
        }
    }
}

@Composable
fun LoginInputLayout(emailState: TextInputState, passwordState: TextInputState) {
    ErrorOutlinedTextField(
        emailState,
        keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next),
        nextInput = passwordState,
        testTag = "login_email_input"
    )

    Spacer(modifier = Modifier.height(8.dp))

    ErrorOutlinedTextField(
        passwordState,
        testTag = "login_password_input",
        visualTransformation = PasswordVisualTransformation(),
        keyboardOptions = KeyboardOptions(
            keyboardType = KeyboardType.Password,
            imeAction = ImeAction.Done
        )
    )

    Spacer(modifier = Modifier.height(16.dp))
}

@Composable
fun LoginButtonLayout(
    authViewModel: AuthViewModel,
    emailState: TextInputState,
    passwordState: TextInputState
) {
    val googleSignInRes =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
            if (it.resultCode == Activity.RESULT_OK) {
                authViewModel.signInWithGoogle(it.data!!)
            }
        }

    PrimaryTextButton(
        text = "Sign in",
        enabled = passwordState.isValid() && emailState.isValid(),
        modifier = Modifier.padding(vertical = 8.dp).testTag("login_submit_btn"),
        onClick = {
            authViewModel.signInWithEmail(emailState, passwordState)
        }
    )

    PrimaryTextButton(
        text = "Sign up",
        modifier = Modifier.padding(vertical = 8.dp).testTag("signup_btn"),
        onClick = { authViewModel.navHostController.navigate(Screen.EmailSignUp.route) }
    )

    Button(
        onClick = { googleSignInRes.launch(authViewModel.getGoogleSignInClient().signInIntent) },
        modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp),
        colors = ButtonDefaults.buttonColors(backgroundColor = Color.White)
    ) {
        Image(
            painter = painterResource(R.drawable.ic_google_icon),
            contentDescription = null,
            modifier = Modifier.height(20.dp).padding(end = 16.dp)
        )
        Text("Sign in with Google", color = Color.DarkGray)
    }
}
