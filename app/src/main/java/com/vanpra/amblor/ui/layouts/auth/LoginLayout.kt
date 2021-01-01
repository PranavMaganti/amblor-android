package com.vanpra.amblor.ui.layouts.auth

import android.util.Patterns
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.layout.WithConstraints
import androidx.compose.ui.platform.AmbientFocusManager
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.navigation.compose.navigate
import com.vanpra.amblor.AmbientNavHostController
import com.vanpra.amblor.R
import com.vanpra.amblor.Screen
import com.vanpra.amblor.util.ErrorOutlinedTextField

class TextInputState {
    val label: String
    var isInputValid: (String) -> Boolean = { true }
    private var defaultError: String = ""

    var text by mutableStateOf("")
    var error by mutableStateOf(false)
    var errorMessage by mutableStateOf("")
    var focusRequester = FocusRequester()

    constructor(label: String, errorMessage: String) {
        this.label = label
        this.errorMessage = errorMessage
    }

    constructor(label: String, defaultError: String = "", isInputValid: (String) -> Boolean) {
        this.label = label
        this.isInputValid = isInputValid
        this.defaultError = defaultError
        this.errorMessage = defaultError
    }

    constructor(label: String) {
        this.label = label
    }

    fun isError() = !isValid() && text.isNotEmpty()
    fun isValid() = !error && isInputValid(text)

    fun showError(message: String) {
        errorMessage = message
        error = true
    }

    fun resetError() {
        if (error) {
            errorMessage = defaultError
            error = false
        }
    }
}

data class LoginState(
    val email: TextInputState = TextInputState("Email"),
    val password: TextInputState = TextInputState("Password")
) {
    private fun isEmailValid(): Boolean =
        Patterns.EMAIL_ADDRESS.matcher(email.text).matches() && email.text != ""

    fun isValid(): Boolean = password.text != "" && isEmailValid()
}

@Composable
fun LoginLayout(authViewModel: AuthViewModel) {
    val focusManager = AmbientFocusManager.current

    Box(
        Modifier.fillMaxSize()
            .clickable(onClick = { focusManager.clearFocus() }, indication = null),
        contentAlignment = Alignment.Center
    ) {
        WithConstraints {
            Column(Modifier.width(maxWidth * 0.75f)) {
                Image(
                    vectorResource(R.drawable.logo),
                    modifier = Modifier
                        .padding(start = 30.dp, end = 30.dp, bottom = 30.dp)
                        .fillMaxWidth(),
                    colorFilter = ColorFilter.tint(MaterialTheme.colors.onBackground),
                )
                LoginInputLayout(authViewModel)
                LoginButtonLayout(authViewModel)
            }
        }
    }
}

@Composable
fun LoginInputLayout(authViewModel: AuthViewModel) {
    ErrorOutlinedTextField(
        authViewModel.loginState.email,
        keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next),
        nextInput = authViewModel.loginState.password,
        testTag = "login_email_input"
    )

    Spacer(modifier = Modifier.height(8.dp))

    ErrorOutlinedTextField(
        authViewModel.loginState.password,
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
fun LoginButtonLayout(authViewModel: AuthViewModel) {
    val authNavController = AmbientNavHostController.current

    // val googleSignInRes = activity.registerForActivityResult(
    //     ActivityResultContracts.StartActivityForResult()
    // ) {
    //     if (it.resultCode == Activity.RESULT_OK) {
    //     }
    // }

    Button(
        onClick = { authViewModel.signInWithEmail(authNavController) },
        modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp).testTag("login_submit_btn"),
        enabled = authViewModel.loginState.isValid()
    ) {
        Text("Sign in", Modifier.wrapContentWidth(Alignment.CenterHorizontally))
    }

    Button(
        onClick = {
            authNavController.navigate(route = Screen.EmailSignUp.route)
        },
        modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp).testTag("signup_btn")
            .background(MaterialTheme.colors.primaryVariant)
    ) {
        Text("Sign up", Modifier.wrapContentWidth(Alignment.CenterHorizontally))
    }

    Button(
        onClick = {
            // googleSignInRes.launch(authViewModel.client.signInIntent)
        },
        modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp),
        colors = ButtonDefaults.buttonColors(backgroundColor = Color.White)
    ) {
        Image(
            vectorResource(R.drawable.ic_google_icon), Modifier.height(20.dp).padding(end = 16.dp)
        )
        Text("Sign in with Google", color = Color.DarkGray)
    }
}
