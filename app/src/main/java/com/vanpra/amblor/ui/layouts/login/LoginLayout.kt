package com.vanpra.amblor.ui.layouts.login

import android.app.Activity
import android.content.Intent
import android.view.View
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.animation.Crossfade
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.foundation.Box
import androidx.compose.foundation.Image
import androidx.compose.foundation.Text
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.material.MaterialTheme
import androidx.compose.material.OutlinedTextField
import androidx.compose.material.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.savedinstancestate.savedInstanceState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.WithConstraints
import androidx.compose.ui.focus.ExperimentalFocus
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.platform.ContextAmbient
import androidx.compose.ui.platform.ViewAmbient
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import androidx.lifecycle.lifecycleScope
import com.vanpra.amblor.AmblorState
import com.vanpra.amblor.LoginState
import com.vanpra.amblor.R
import com.vanpra.amblor.ViewModelAmbient
import com.vanpra.amblor.util.enabledColor
import kotlinx.coroutines.launch

@ExperimentalFocus
@ExperimentalAnimationApi
@Composable
fun LoginController() {
    Crossfade(current = ViewModelAmbient.current.loginState) {
        when (it) {
            LoginState.SignIn -> LoginLayout()
            LoginState.GoogleSignUp -> GoogleSignup()
            LoginState.EmailSignUp -> EmailSignup()
        }
    }
}

@Composable
fun LoginLayout() {
    val email = savedInstanceState(saver = TextFieldValue.Saver) { TextFieldValue() }
    val password = savedInstanceState(saver = TextFieldValue.Saver) { TextFieldValue() }
    val validInput = password.value.text != "" && email.value.text != ""

    val hostView = ViewAmbient.current
    val viewModel = ViewModelAmbient.current
    val activity = ContextAmbient.current as AppCompatActivity
    val result = activity.registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) {
        if (it.resultCode == Activity.RESULT_OK) {
            activity.lifecycleScope.launch {
                val newUser = viewModel.firebaseAuthWithGoogle(it.data!!)
                if (newUser) {
                    viewModel.loginState = LoginState.GoogleSignUp
                } else {
                    viewModel.appState = AmblorState.App
                }
            }
        }
    }

    Column(
        Modifier.fillMaxSize().clickable(
            onClick = {
                hostView.clearFocus()
            },
            indication = null
        ),
        verticalArrangement = Arrangement.Center,
        horizontalGravity = Alignment.CenterHorizontally
    ) {
        WithConstraints {
            Box(Modifier.width(maxWidth * 0.75f)) {
                Image(
                    vectorResource(R.drawable.logo),
                    modifier = Modifier
                        .padding(start = 30.dp, end = 30.dp, bottom = 30.dp)
                        .fillMaxWidth(),
                    colorFilter = ColorFilter.tint(MaterialTheme.colors.onBackground),
                )
                LoginInputLayout(email = email, password = password, hostView = hostView)
                LoginButtonLayout(email, password, canSignIn = validInput, result)
            }
        }
    }
}

@Composable
fun LoginInputLayout(
    email: MutableState<TextFieldValue>,
    password: MutableState<TextFieldValue>,
    hostView: View
) {
    OutlinedTextField(
        value = email.value,
        onValueChange = { value -> email.value = value },
        label = { Text("Email") },
        modifier = Modifier
            .padding(bottom = 16.dp)
            .fillMaxWidth(),
        imeAction = ImeAction.Next,
        onImeActionPerformed = { imeAction, _ ->
            if (imeAction == ImeAction.Next) {
                // Add focus change after API is added
            }
        }
    )

    OutlinedTextField(
        value = password.value,
        onValueChange = { value -> password.value = value },
        label = { Text("Password") },
        visualTransformation = PasswordVisualTransformation(),
        modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp),
        keyboardType = KeyboardType.Password,
        imeAction = ImeAction.Done,
        onImeActionPerformed = { imeAction, _ ->
            if (imeAction == ImeAction.Done) {
                hostView.clearFocus()
            }
        }
    )
}

@Composable
fun LoginButtonLayout(
    email: MutableState<TextFieldValue>,
    password: MutableState<TextFieldValue>,
    canSignIn: Boolean,
    result: ActivityResultLauncher<Intent>
) {
    val backgroundColor = MaterialTheme.colors.primaryVariant.enabledColor(canSignIn)
    val textColor = MaterialTheme.colors.onPrimary.enabledColor(canSignIn)
    val viewModel = ViewModelAmbient.current

    TextButton(
        onClick = {
            val task =
                viewModel.auth.signInWithEmailAndPassword(email.value.text, password.value.text)
            task.addOnCompleteListener {
                if (it.isSuccessful) {
                    viewModel.appState = AmblorState.App
                } else {
                    println(it.exception)
                }
            }
        },
        modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp),
        backgroundColor = backgroundColor,
        enabled = canSignIn
    ) {
        Text(
            "Sign in",
            Modifier.wrapContentWidth(Alignment.CenterHorizontally),
            color = textColor
        )
    }

    TextButton(
        onClick = { viewModel.loginState = LoginState.EmailSignUp },
        modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp),
        backgroundColor = MaterialTheme.colors.primaryVariant
    ) {
        Text(
            "Sign up",
            Modifier.wrapContentWidth(Alignment.CenterHorizontally),
            color = MaterialTheme.colors.onPrimary
        )
    }

    TextButton(
        onClick = { result.launch(viewModel.client.signInIntent) },
        modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp),
        elevation = 8.dp,
        backgroundColor = Color.White,
        contentColor = Color.DarkGray
    ) {
        Image(
            vectorResource(R.drawable.ic_google_icon),
            modifier = Modifier.height(20.dp).padding(end = 16.dp)
        )
        Text("Sign in with Google")
    }
}
