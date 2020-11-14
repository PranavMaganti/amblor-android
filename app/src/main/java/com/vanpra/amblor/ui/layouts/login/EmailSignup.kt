package com.vanpra.amblor.ui.layouts.login

import android.util.Patterns
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.foundation.Box
import androidx.compose.foundation.Image
import androidx.compose.foundation.Text
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.material.Button
import androidx.compose.material.MaterialTheme
import androidx.compose.material.OutlinedTextField
import androidx.compose.material.TextButton
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.navigation.AmbientNavController
import androidx.compose.navigation.navigate
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.ExperimentalFocus
import androidx.compose.ui.focus.isFocused
import androidx.compose.ui.focusObserver
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.platform.ViewAmbient
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import com.vanpra.amblor.AuthAmbient
import com.vanpra.amblor.ui.LoginNavigationState
import com.vanpra.amblor.util.ErrorWrapper
import com.vanpra.amblor.util.SignUpTitle
import com.vanpra.amblor.util.enabledColor
import java.util.regex.Pattern

class EmailSignupModel {
    var email by mutableStateOf("")
    var username by mutableStateOf("")
    var password by mutableStateOf("")
    var confirmPassword by mutableStateOf("")

    var usernameUsed by mutableStateOf(false)
    var emailUsed by mutableStateOf(false)

    fun passwordsMatch() = password == confirmPassword

    fun isPasswordValid(): Boolean {
        var valid = password.length >= 8
        val patterns = listOf(
            ".*[0-9].*",
            ".*[A-Z].*",
            ".*[a-z].*",
            ".*[~!@#\$%\\^&*()\\-_=+\\|\\[{\\]};:'\",<.>/?].*"
        )

        patterns.forEach {
            val matcher = Pattern.compile(it).matcher(password)
            valid = valid && matcher.matches()
        }

        return valid
    }

    fun isEmailValid() = Patterns.EMAIL_ADDRESS.matcher(email).matches() || email == ""

    fun detailsValid() =
        passwordsMatch() && isPasswordValid() && isEmailValid() && !usernameUsed && !emailUsed

    @Composable
    fun buttonBackgroundColor() = MaterialTheme.colors.primaryVariant.enabledColor(detailsValid())

    @Composable
    fun buttonTextColor() = MaterialTheme.colors.onPrimary.enabledColor(detailsValid())
}

@ExperimentalAnimationApi
@ExperimentalFocus
@Composable
fun EmailSignup() {
    val hostView = ViewAmbient.current

    val signupModel = remember { EmailSignupModel() }
    var showingPasswordList by remember { mutableStateOf(false) }

    val authRepo = AuthAmbient.current
    val navController = AmbientNavController.current

    Column(
        Modifier.clickable(onClick = { hostView.clearFocus() }, indication = null).fillMaxSize()
    ) {
        SignUpTitle(title = "Sign up") { navController.navigate(LoginNavigationState.Login) }
        Box(Modifier.padding(start = 16.dp, end = 16.dp)) {
            ErrorWrapper(
                isError = !signupModel.isEmailValid() || signupModel.emailUsed,
                errorMessage = if (signupModel.emailUsed) "Email is already registered" else "Invalid Email"
            ) {
                OutlinedTextField(
                    value = signupModel.email,
                    onValueChange = { value -> signupModel.email = value },
                    label = { Text("Email") },
                    modifier = Modifier.fillMaxWidth().padding(bottom = 8.dp),
                    imeAction = ImeAction.Next,
                    isErrorValue = !signupModel.isEmailValid() || signupModel.emailUsed,
                    onTextInputStarted = {
                        signupModel.emailUsed = false
                    }
                )
            }

            ErrorWrapper(isError = signupModel.usernameUsed, errorMessage = "Username Taken") {
                OutlinedTextField(
                    value = signupModel.username,
                    onValueChange = { value -> signupModel.username = value },
                    label = { Text("Username") },
                    modifier = Modifier.fillMaxWidth().padding(bottom = 8.dp),
                    imeAction = ImeAction.Next,
                    isErrorValue = signupModel.usernameUsed,
                    onTextInputStarted = {
                        signupModel.usernameUsed = false
                    }
                )
            }

            OutlinedTextField(
                value = signupModel.password,
                onValueChange = { value -> signupModel.password = value },
                label = { Text("Password") },
                visualTransformation = PasswordVisualTransformation(),
                modifier = Modifier.fillMaxWidth().padding(bottom = 8.dp).focusObserver {
                    showingPasswordList = it.isFocused
                },
                keyboardType = KeyboardType.Password,
                imeAction = ImeAction.Next,
                isErrorValue = !signupModel.isPasswordValid(),
            )

            PasswordCriteria(signUpModel = signupModel, showing = showingPasswordList)

            ErrorWrapper(
                isError = !signupModel.passwordsMatch() && signupModel.confirmPassword.isNotEmpty(),
                errorMessage = "Passwords don't match"
            ) {
                OutlinedTextField(
                    value = signupModel.confirmPassword,
                    onValueChange = { value -> signupModel.confirmPassword = value },
                    label = { Text("Confirm Password") },
                    visualTransformation = PasswordVisualTransformation(),
                    modifier = Modifier.fillMaxWidth(),
                    keyboardType = KeyboardType.Password,
                    imeAction = ImeAction.Done,
                    isErrorValue = !signupModel.passwordsMatch() &&
                        signupModel.confirmPassword.isNotEmpty()
                )
            }

            Button(
                onClick = { authRepo.emailSignup(signupModel) },
                modifier = Modifier.fillMaxWidth().padding(vertical = 24.dp),
                backgroundColor = signupModel.buttonBackgroundColor(),
                enabled = signupModel.detailsValid()
            ) {
                Text(
                    "Complete Sign up",
                    Modifier.wrapContentWidth(Alignment.CenterHorizontally),
                    color = signupModel.buttonTextColor()
                )
            }
        }
    }
}

@ExperimentalAnimationApi
@Composable
private fun PasswordCriteria(signUpModel: EmailSignupModel, showing: Boolean) {
    AnimatedVisibility(visible = showing) {
        Column(Modifier.padding(bottom = 8.dp)) {
            PasswordCriteriaItem(
                text = "At least 8 characters long",
                satisfied = signUpModel.password.length >= 8
            )
            PasswordCriteriaItem(
                text = "At least 1 uppercase letter",
                satisfied = Pattern.compile(
                    ".*[A-Z].*"
                ).matcher(signUpModel.password).matches()
            )
            PasswordCriteriaItem(
                text = "At least 1 number",
                satisfied = Pattern.compile(
                    ".*[0-9].*"
                ).matcher(signUpModel.password).matches()
            )
            PasswordCriteriaItem(
                text = "At least 1 special character",
                satisfied = Pattern.compile(
                    ".*[~!@#\$%^&*()\\-_=+|\\[{\\]};:'\",<.>/?].*"
                ).matcher(signUpModel.password).matches()
            )
        }
    }
}

@Composable
private fun PasswordCriteriaItem(text: String, satisfied: Boolean) {
    Row(Modifier.padding(8.dp)) {
        val imageTint = ColorFilter.tint(if (satisfied) Color.Green else Color.Red)
        val image = if (satisfied) Icons.Default.Check else Icons.Default.Close
        Image(image, colorFilter = imageTint)
        Text(
            text,
            color = MaterialTheme.colors.onBackground,
            modifier = Modifier.padding(start = 8.dp)
        )
    }
}
