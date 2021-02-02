package com.vanpra.amblor

import androidx.compose.ui.test.junit4.ComposeContentTestRule
import androidx.compose.ui.test.junit4.ComposeTestRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performTextInput

fun ComposeContentTestRule.launchAmblorApp() {
    setContent { MainLayout() }
}

fun ComposeTestRule.loginWithUser(user: User) {
    onNodeWithTag("login_email_input").performTextInput(user.email)
    onNodeWithTag("login_password_input").performTextInput(user.password)
    onNodeWithTag("login_submit_btn").performClick()
}
