package com.vanpra.amblor.ui.layouts.login

import android.app.Activity
import android.content.Intent
import android.view.View
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.Button
import androidx.compose.material.MaterialTheme
import androidx.compose.material.OutlinedTextField
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.remember
import androidx.compose.runtime.savedinstancestate.savedInstanceState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.layout.WithConstraints
import androidx.compose.ui.platform.AmbientContext
import androidx.compose.ui.platform.AmbientLifecycleOwner
import androidx.compose.ui.platform.AmbientView
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import androidx.lifecycle.lifecycleScope
import com.vanpra.amblor.R
import com.vanpra.amblor.util.enabledColor
import kotlinx.coroutines.launch

@Composable
fun LoginLayout() {
    val email = savedInstanceState(saver = TextFieldValue.Saver) { TextFieldValue() }
    val password = savedInstanceState(saver = TextFieldValue.Saver) { TextFieldValue() }
    val validInput = password.value.text != "" && email.value.text != ""

    val hostView = AmbientView.current
    val activity = AmbientContext.current as AppCompatActivity
    val result = activity.registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) {
        if (it.resultCode == Activity.RESULT_OK) {
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
        horizontalAlignment = Alignment.CenterHorizontally
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
                LoginInputLayout(email, password, hostView)
                LoginButtonLayout(email, password, validInput, result)
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
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
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
    result: ActivityResultLauncher<Intent>,
) {
    val backgroundColor = MaterialTheme.colors.primaryVariant.enabledColor(canSignIn)
    val textColor = MaterialTheme.colors.onPrimary.enabledColor(canSignIn)

    Button(
        onClick = {
            authRepo.emailLogin(email.value.text, password.value.text)
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

    Button(
        onClick = { navController.navigate(LoginNavigationState.EmailSignUp) },
        modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp),
        backgroundColor = MaterialTheme.colors.primaryVariant
    ) {
        Text(
            "Sign up",
            Modifier.wrapContentWidth(Alignment.CenterHorizontally),
            color = MaterialTheme.colors.onPrimary
        )
    }

    Button(
        onClick = { result.launch(authRepo.client.signInIntent) },
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
