package com.vanpra.amblor

import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.performTextInput
import androidx.test.ext.junit.runners.AndroidJUnit4
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.koin.test.KoinTest
import org.koin.test.KoinTestRule

@RunWith(AndroidJUnit4::class)
class ComposeTests : KoinTest {
    @get:Rule
    val composeTestRule = createAndroidComposeRule<MainActivity>()

    @get:Rule
    val koinTestRule = KoinTestRule.create {
        modules()
    }

    @Test
    fun checkSignup() {
    }

    @ExperimentalAnimationApi
    @Test
    fun checkSignIn() {
        composeTestRule.setContent { MainLayout() }
        composeTestRule.onNodeWithContentDescription("Email")
            .performTextInput("imaginaryemail@emailservice.com")
        composeTestRule.onNodeWithContentDescription("Password").performTextInput("testing123")
    }
}
