package com.vanpra.amblor.util

import android.os.Parcelable
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.Saver
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.focus.FocusRequester
import kotlinx.parcelize.Parcelize

class TextInputState(
    val label: String,
    text: String = "",
    error: Boolean = true,
    errorMessage: String = "",
    val defaultError: String = "",
    val isInputValid: (String) -> Boolean = { true }
) {

    var text by mutableStateOf(text)
    var error by mutableStateOf(error)
    var errorMessage by mutableStateOf(errorMessage)
    var focusRequester = FocusRequester()

    /* Two different functions as button should be disabled when input is empty but the text fields
       should not be red. Therefore use isValid for TF and isError for button */
    fun isError() = (error || !isInputValid(text)) && text.isNotEmpty()
    fun isValid() = !error && isInputValid(text) && text.isNotEmpty()

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
        data class SavedInputData(
            val text: String,
            val error: Boolean,
            val errorMessage: String
        ) : Parcelable

        fun Saver(
            label: String,
            defaultError: String = "",
            isInputValid: (String) -> Boolean = { true }
        ): Saver<TextInputState, *> = Saver(
            save = { SavedInputData(it.text, it.error, it.errorMessage) },
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
    val saver = remember(label, defaultError, isInputValid) {
        TextInputState.Saver(label, defaultError, isInputValid)
    }

    return rememberSaveable(text, error, errorMessage, saver = saver) {
        TextInputState(label, text, error, errorMessage, defaultError, isInputValid)
    }
}
