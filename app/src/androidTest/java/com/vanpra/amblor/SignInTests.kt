package com.vanpra.amblor

import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertTextEquals
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performTextInput
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.vanpra.amblor.auth.AuthenticationApi
import com.vanpra.amblor.auth.InvalidPasswordException
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.koin.core.context.loadKoinModules
import org.koin.dsl.module
import org.koin.test.KoinTest

@RunWith(AndroidJUnit4::class)
class SignInTests : KoinTest {
    private data class User(val email: String, val password: String, val username: String)

    @get:Rule
    val composeTestRule = createAndroidComposeRule<MainActivity>()

    private val testUser = User("testemail@emailservice.com", "password", "test")

    private lateinit var authApi: AuthenticationApi

    @Before
    fun startKoinBeforeTest() {
        authApi = mockk()
        loadKoinModules(
            module {
                single(override = true) { authApi }
            }
        )
    }

    @ExperimentalAnimationApi
    fun setupLayoutAndLogin() {
        /* Need this as MainActivity onCreate calls this */
        every { authApi.isUserSignedIn() } returns false

        composeTestRule.setContent { MainLayout() }
        composeTestRule.onNodeWithTag("login_email_input").performTextInput(testUser.email)
        composeTestRule.onNodeWithTag("login_password_input").performTextInput(testUser.password)
        composeTestRule.onNodeWithTag("login_submit_btn").performClick()
    }

    @ExperimentalAnimationApi
    @Test
    fun checkEmailSignInWhenRegistered() {
        coEvery { authApi.fetchSignInMethodsForEmail(testUser.email) } returns listOf("password")
        coEvery {
            authApi.signInWithEmailAndPassword(
                testUser.email,
                testUser.password
            )
        } returns Unit

        setupLayoutAndLogin()
        composeTestRule.onNodeWithTag("app_scaffold").assertIsDisplayed()

        coVerify(exactly = 1) { authApi.fetchSignInMethodsForEmail(testUser.email) }
        coVerify(exactly = 1) {
            authApi.signInWithEmailAndPassword(testUser.email, testUser.password)
        }
    }

    @ExperimentalAnimationApi
    @Test
    fun checkEmailSignInWhenMultipleProviders() {
        coEvery { authApi.fetchSignInMethodsForEmail(testUser.email) } returns listOf(
            "Google",
            "password"
        )
        coEvery {
            authApi.signInWithEmailAndPassword(
                testUser.email,
                testUser.password
            )
        } returns Unit

        setupLayoutAndLogin()
        composeTestRule.onNodeWithTag("app_scaffold").assertIsDisplayed()

        coVerify(exactly = 1) { authApi.fetchSignInMethodsForEmail(testUser.email) }
        coVerify(exactly = 1) {
            authApi.signInWithEmailAndPassword(testUser.email, testUser.password)
        }
    }

    @ExperimentalAnimationApi
    @Test
    fun checkEmailErrorWhenNotRegistered() {
        coEvery { authApi.fetchSignInMethodsForEmail(testUser.email) } returns listOf()

        setupLayoutAndLogin()
        composeTestRule.onNodeWithTag("login_email_input_error", true).assertIsDisplayed()
            .assertTextEquals("Email not registered")

        coVerify(exactly = 1) { authApi.fetchSignInMethodsForEmail(testUser.email) }
    }

    @ExperimentalAnimationApi
    @Test
    fun checkEmailErrorWhenRegWithDifferentProvider() {
        coEvery { authApi.fetchSignInMethodsForEmail(testUser.email) } returns listOf("Google")

        setupLayoutAndLogin()
        composeTestRule.onNodeWithTag("login_email_input_error", true).assertIsDisplayed()
            .assertTextEquals("Email registered with different provider")

        coVerify(exactly = 1) { authApi.fetchSignInMethodsForEmail(testUser.email) }
    }

    @ExperimentalAnimationApi
    @Test
    fun checkPasswordErrorWhenInvalidPassword() {
        coEvery { authApi.fetchSignInMethodsForEmail(testUser.email) } returns listOf("password")
        coEvery {
            authApi.signInWithEmailAndPassword(
                testUser.email,
                testUser.password
            )
        } throws InvalidPasswordException("Invalid password given")

        setupLayoutAndLogin()
        composeTestRule.onNodeWithTag("login_password_input_error", true).assertIsDisplayed()
            .assertTextEquals("Invalid Password")

        coVerify(exactly = 1) { authApi.fetchSignInMethodsForEmail(testUser.email) }
        coVerify(exactly = 1) {
            authApi.signInWithEmailAndPassword(
                testUser.email,
                testUser.password
            )
        }
    }
}
