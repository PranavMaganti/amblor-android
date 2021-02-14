package com.vanpra.amblor.ui.layouts.auth

import android.app.Activity
import android.os.Parcelable
import android.util.Patterns
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.InteractionState
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.Saver
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.navigation.compose.navigate
import com.vanpra.amblor.LocalNavHostController
import com.vanpra.amblor.R
import com.vanpra.amblor.Screen
import com.vanpra.amblor.util.ErrorOutlinedTextField
import com.vanpra.amblor.util.PrimaryTextButton
import com.vanpra.amblor.util.registerForActivityResult
import dev.chrisbanes.accompanist.coil.CoilImage
import kotlinx.parcelize.Parcelize

class TextInputState(
    val label: String,
    text: String = "",
    error: Boolean = true,
    errorMessage: String = "",
    private val defaultError: String = "",
    val isInputValid: (String) -> Boolean = { true }
) {

    var text by mutableStateOf(text)
    var error by mutableStateOf(error)
    var errorMessage by mutableStateOf(errorMessage)
    var focusRequester = FocusRequester()

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

    companion object {
        @Parcelize
        private data class SavedInputData(
            val text: String,
            val error: Boolean,
            val errorMessage: String
        ) : Parcelable

        fun saver(
            label: String,
            text: String = "",
            error: Boolean = true,
            errorMessage: String = "",
            defaultError: String = "",
            isInputValid: (String) -> Boolean = { true }
        ): Saver<TextInputState, *> = Saver(
            save = { SavedInputData(text, error, errorMessage) },
            restore = {
                TextInputState(
                    label,
                    it.text,
                    it.error,
                    it.errorMessage,
                    defaultError,
                    isInputValid
                )
            }
        )
    }
}

@Composable
fun rememberTextInputState(
    label: String,
    text: String = "",
    error: Boolean = true,
    errorMessage: String = "",
    defaultError: String = "",
    isInputValid: (String) -> Boolean = { true }
): TextInputState {

    val saver = remember(text, error, errorMessage) {
        TextInputState.saver(label, text, error, errorMessage, defaultError, isInputValid)
    }

    return rememberSaveable(text, error, errorMessage, saver = saver) {
        TextInputState(label, text, error, errorMessage, defaultError, isInputValid)
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
    val focusManager = LocalFocusManager.current

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
            CoilImage(
                R.drawable.logo,
                contentDescription = null,
                modifier = Modifier.padding(start = 30.dp, end = 30.dp, bottom = 30.dp)
                    .size(100.dp),
                colorFilter = ColorFilter.tint(MaterialTheme.colors.onBackground),
            )
            LoginInputLayout(authViewModel)
            LoginButtonLayout(authViewModel)
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
    val authNavController = LocalNavHostController.current

    val googleSignInRes =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
            if (it.resultCode == Activity.RESULT_OK) {
                authViewModel.signInWithGoogle(it.data!!)
            }
        }

    PrimaryTextButton(
        text = "Sign in",
        modifier = Modifier
            .padding(vertical = 8.dp)
            .testTag("login_submit_btn"),
        onClick = { authViewModel.signInWithEmail() }
    )

    PrimaryTextButton(
        text = "Sign up",
        modifier = Modifier
            .padding(vertical = 8.dp)
            .testTag("signup_btn"),
        onClick = { authNavController.navigate(route = Screen.EmailSignUp.route) }
    )

    Button(
        onClick = {
            googleSignInRes.launch(authViewModel.client.signInIntent)
        },
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        colors = ButtonDefaults.buttonColors(backgroundColor = Color.White)
    ) {
        CoilImage(
            R.drawable.ic_google_icon,
            contentDescription = null,
            Modifier
                .height(20.dp)
                .padding(end = 16.dp)
        )
        Text("Sign in with Google", color = Color.DarkGray)
    }
}
