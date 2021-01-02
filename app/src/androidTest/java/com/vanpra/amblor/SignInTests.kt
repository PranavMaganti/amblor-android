package com.vanpra.amblor

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertTextEquals
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
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

data class User(val email: String, val password: String, val username: String) {
    companion object {
        val testUser = User("testemail@emailservice.com", "password", "test")
    }
}

@RunWith(AndroidJUnit4::class)
class SignInTests : KoinTest {
    @get:Rule
    val composeTestRule = createComposeRule()

    private val testUser = User.testUser
    private lateinit var authApi: AuthenticationApi

    @Before
    fun setupMockDependencies() {
        authApi = mockk {
            /* Need this as MainActivity onCreate calls this */
            every { isUserSignedIn() } returns false
        }

        val mockModules = module(override = true) { single { authApi } }
        loadKoinModules(mockModules)
    }

    @Test
    fun checkEmailSignInWhenRegistered() {
        coEvery { authApi.fetchSignInMethodsForEmail(testUser.email) } returns listOf("password")
        coEvery {
            authApi.signInWithEmailAndPassword(
                testUser.email,
                testUser.password
            )
        } returns Unit

        composeTestRule.launchAmblorApp()
        composeTestRule.loginWithUser(testUser)
        composeTestRule.onNodeWithTag("app_scaffold").assertIsDisplayed()

        coVerify(exactly = 1) { authApi.fetchSignInMethodsForEmail(testUser.email) }
        coVerify(exactly = 1) {
            authApi.signInWithEmailAndPassword(testUser.email, testUser.password)
        }
    }

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

        composeTestRule.launchAmblorApp()
        composeTestRule.loginWithUser(testUser)
        composeTestRule.onNodeWithTag("app_scaffold").assertIsDisplayed()

        coVerify(exactly = 1) { authApi.fetchSignInMethodsForEmail(testUser.email) }
        coVerify(exactly = 1) {
            authApi.signInWithEmailAndPassword(testUser.email, testUser.password)
        }
    }

    @Test
    fun checkEmailErrorWhenNotRegistered() {
        coEvery { authApi.fetchSignInMethodsForEmail(testUser.email) } returns listOf()

        composeTestRule.launchAmblorApp()
        composeTestRule.loginWithUser(testUser)
        composeTestRule.onNodeWithTag("login_email_input_error", true).assertIsDisplayed()
            .assertTextEquals("Email not registered")

        coVerify(exactly = 1) { authApi.fetchSignInMethodsForEmail(testUser.email) }
    }

    @Test
    fun checkEmailErrorWhenRegWithDifferentProvider() {
        coEvery { authApi.fetchSignInMethodsForEmail(testUser.email) } returns listOf("Google")

        composeTestRule.launchAmblorApp()
        composeTestRule.loginWithUser(testUser)
        composeTestRule.onNodeWithTag("login_email_input_error", true).assertIsDisplayed()
            .assertTextEquals("Email registered with different provider")

        coVerify(exactly = 1) { authApi.fetchSignInMethodsForEmail(testUser.email) }
    }

    @Test
    fun checkPasswordErrorWhenInvalidPassword() {
        coEvery { authApi.fetchSignInMethodsForEmail(testUser.email) } returns listOf("password")
        coEvery {
            authApi.signInWithEmailAndPassword(
                testUser.email,
                testUser.password
            )
        } throws InvalidPasswordException("Invalid password given")

        composeTestRule.launchAmblorApp()
        composeTestRule.loginWithUser(testUser)
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
