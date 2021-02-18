package com.vanpra.amblor.util

import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.ActivityResultRegistryOwner
import androidx.activity.result.contract.ActivityResultContract
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.Button
import androidx.compose.material.IconButton
import androidx.compose.material.MaterialTheme
import androidx.compose.material.OutlinedTextField
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ChevronLeft
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.app.ActivityOptionsCompat
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewmodel.compose.LocalViewModelStoreOwner
import org.koin.androidx.viewmodel.ViewModelOwner
import org.koin.androidx.viewmodel.koin.getViewModel
import org.koin.core.context.GlobalContext
import org.koin.core.parameter.ParametersDefinition
import org.koin.core.qualifier.Qualifier
import java.util.UUID

// From koin source (temporary until koin is updated)
@Composable
inline fun <reified T : ViewModel> getViewModel(
    qualifier: Qualifier? = null,
    noinline parameters: ParametersDefinition? = null,
): T {
    val owner = LocalViewModelStoreOwner.current.viewModelStore
    return remember {
        GlobalContext.get().getViewModel(
            qualifier,
            owner = { ViewModelOwner.from(owner) },
            parameters = parameters
        )
    }
}

@Composable
fun BackButton(onClick: () -> Unit) {
    Row(Modifier.height(56.dp), verticalAlignment = Alignment.CenterVertically) {
        Image(
            Icons.Default.ArrowBack,
            contentDescription = null,
            modifier = Modifier
                .size(40.dp)
                .clickable(onClick = onClick)
                .padding(start = 8.dp),
            contentScale = ContentScale.None,
            colorFilter = ColorFilter.tint(MaterialTheme.colors.onBackground)
        )
    }
}

@Composable
fun BackButtonTitle(title: String, onBackClick: () -> Unit) {
    Box(
        Modifier
            .fillMaxWidth()
            .padding(top = 8.dp)
    ) {
        IconButton(onClick = { onBackClick() }) {
            Image(
                Icons.Default.ChevronLeft,
                contentDescription = null,
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
private fun TextInputErrorText(
    textInputState: TextInputState,
    showText: Boolean,
    testTag: String,
) {
    if (textInputState.isError() && showText) {
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
    val focusManager = LocalFocusManager.current

    Column {
        OutlinedTextField(
            value = inputState.text,
            modifier = modifier
                .fillMaxWidth()
                .focusRequester(inputState.focusRequester)
                .testTag(testTag),
            label = { Text(inputState.label) },
            keyboardOptions = keyboardOptions.copy(autoCorrect = false),
            onValueChange = { value -> inputState.text = value },
            // Bug causing instrumentation tests to fail. Bug reported:
            // https://issuetracker.google.com/issues/180513112
            // isErrorValue = inputState.isError(),
            onTextInputStarted = {
                inputState.resetError()
            },
            keyboardActions = KeyboardActions(
                onNext = { nextInput?.focusRequester?.requestFocus() },
                onDone = { focusManager.clearFocus() }
            ),
            visualTransformation = visualTransformation,
            textStyle = TextStyle(MaterialTheme.colors.onBackground, fontSize = 16.sp)
        )
        TextInputErrorText(inputState, showErrorText, testTag + "_error")
    }
}

/* From https://stackoverflow.com/questions/64721218/jetpack-compose-launch-activityresultcontract-request-from-composable-function */
@Composable
fun <I, O> registerForActivityResult(
    contract: ActivityResultContract<I, O>,
    onResult: (O) -> Unit
): ActivityResultLauncher<I> {
    // First, find the ActivityResultRegistry by casting the Context
    // (which is actually a ComponentActivity) to ActivityResultRegistryOwner
    val owner = LocalContext.current as ActivityResultRegistryOwner
    val activityResultRegistry = owner.activityResultRegistry

    // Keep track of the current onResult listener
    val currentOnResult = rememberUpdatedState(onResult)

    // It doesn't really matter what the key is, just that it is unique
    // and consistent across configuration changes
    arrayOf<Any?>()
    val key = rememberSaveable { UUID.randomUUID().toString() }

    // Since we don't have a reference to the real ActivityResultLauncher
    // until we register(), we build a layer of indirection so we can
    // immediately return an ActivityResultLauncher
    // (this is the same approach that Fragment.registerForActivityResult uses)
    val realLauncher = remember { mutableStateOf<ActivityResultLauncher<I>?>(null) }
    val returnedLauncher = remember {
        object : ActivityResultLauncher<I>() {
            override fun launch(input: I, options: ActivityOptionsCompat?) {
                realLauncher.value?.launch(input, options)
            }

            override fun unregister() {
                realLauncher.value?.unregister()
            }

            override fun getContract() = contract
        }
    }

    // DisposableEffect ensures that we only register once
    // and that we unregister when the composable is disposed
    DisposableEffect(activityResultRegistry, key, contract) {
        realLauncher.value = activityResultRegistry.register(key, contract) {
            currentOnResult.value(it)
        }
        onDispose {
            realLauncher.value?.unregister()
        }
    }
    return returnedLauncher
}

@Composable
fun PrimaryTextButton(
    text: String,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    onClick: () -> Unit
) {
    Button(
        onClick = onClick,
        enabled = enabled,
        modifier = modifier.fillMaxWidth().background(MaterialTheme.colors.primaryVariant)
    ) {
        Text(text, Modifier.wrapContentWidth(Alignment.CenterHorizontally))
    }
}
