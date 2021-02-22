package com.vanpra.amblor

import androidx.compose.ui.test.junit4.ComposeContentTestRule
import androidx.compose.ui.test.junit4.ComposeTestRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performImeAction
import androidx.compose.ui.test.performTextInput

data class User(val email: String, val password: String, val username: String, val token: String)
val testUser = User("testemail@emailservice.com", "Passw0!rd", "test", "token")

fun ComposeContentTestRule.setAmblorLayout() {
    setContent { MainLayout() }
}

fun ComposeContentTestRule.signUpWithUser(user: User) {
    onNodeWithTag("signup_btn", true).performClick()
    onNodeWithTag("email_signup", true).performTextInput(user.email)
    onNodeWithTag("username_signup", true).performTextInput(user.username)
    onNodeWithTag("password_signup", true).performTextInput(user.password)
    onNodeWithTag("confirm_password_signup", true).performTextInput(user.password)
}

fun ComposeTestRule.loginWithUser(user: User) {
    val emailNode = onNodeWithTag("login_email_input")
    val passwordNode = onNodeWithTag("login_password_input")
    emailNode.performTextInput(user.email)
    passwordNode.performTextInput(user.password)
    passwordNode.performImeAction(alreadyHasFocus = true)
    onNodeWithTag("login_submit_btn").performClick()
}
