package com.vanpra.amblor

import androidx.compose.ui.test.junit4.ComposeContentTestRule
import androidx.compose.ui.test.junit4.ComposeTestRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performImeAction
import androidx.compose.ui.test.performTextInput
import androidx.compose.ui.text.input.ImeAction
import kotlinx.coroutines.delay
import kotlinx.coroutines.runBlocking

fun ComposeContentTestRule.launchAmblorApp() {
    setContent { MainLayout() }
}

fun ComposeTestRule.loginWithUser(user: User) {
    val emailNode = onNodeWithTag("login_email_input")
    val passwordNode = onNodeWithTag("login_password_input")
    emailNode.performTextInput(user.email)
    passwordNode.performTextInput(user.password)
    passwordNode.performImeAction(alreadyHasFocus = true)
    onNodeWithTag("login_submit_btn").performClick()
}
