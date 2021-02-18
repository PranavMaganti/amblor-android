package com.vanpra.amblor.util

import androidx.compose.runtime.Composable
import java.util.regex.Pattern

class EmailSignupState {
    companion object {
        fun isPasswordValid(password: String): Boolean {
            if (password.length < 8) {
                return false
            }

            val patterns = listOf(
                ".*[0-9].*",
                ".*[A-Z].*",
                ".*[a-z].*",
                ".*[~!@#\$%\\^&*()\\-_=+\\|\\[{\\]};:'\",<.>/?].*"
            )

            patterns.forEach {
                if (!Pattern.compile(it).matcher(password).matches()) {
                    return false
                }
            }

            return true
        }

        @Composable
        fun detailsValid(
            email: TextInputState,
            username: TextInputState,
            password: TextInputState,
            confirmPassword: TextInputState
        ) = email.isValid() && username.isValid() && password.isValid() && confirmPassword.isValid()
    }
}