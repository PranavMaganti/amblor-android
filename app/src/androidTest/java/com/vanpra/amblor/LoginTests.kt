package com.vanpra.amblor

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertIsNotEnabled
import androidx.compose.ui.test.assertTextEquals
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.performTextClearance
import androidx.compose.ui.test.performTextInput
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.vanpra.amblor.data.AmblorDatabase
import com.vanpra.amblor.interfaces.AmblorApi
import com.vanpra.amblor.interfaces.AuthenticationApi
import com.vanpra.amblor.interfaces.InvalidPasswordException
import io.mockk.coEvery
import io.mockk.coJustRun
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.flow.MutableStateFlow
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.koin.core.context.loadKoinModules
import org.koin.dsl.module
import org.koin.test.KoinTest

data class User(val email: String, val password: String, val username: String, val token: String)

@RunWith(AndroidJUnit4::class)
class SignInTests : KoinTest {
    @get:Rule
    val composeTestRule = createComposeRule()

    private val testUser = User("testemail@emailservice.com", "password", "test", "token")
    private lateinit var authApi: AuthenticationApi

    @Before
    fun setupMockDependencies() {
        authApi = mockk(relaxUnitFun = true) {
            /* Need this as MainActivity onCreate calls this */
            every { isUserSignedIn() } returns false
            coEvery { getToken() } returns testUser.token
            coEvery { isUserEmailVerified() } returns true
        }
        val amblorDatabase = mockk<AmblorDatabase> {
            coEvery { scrobbleDao().getAllScrobbles() } returns MutableStateFlow(listOf())
            coJustRun { scrobbleDao().insertAllScrobbles(listOf()) }
        }
        val amblorApi = mockk<AmblorApi> {
            coEvery { getAllScrobbles(testUser.token) } returns listOf()
        }

        val mockModules = module(override = true) {
            single { authApi }
            single { amblorDatabase }
            single { amblorApi }
        }
        loadKoinModules(mockModules)
    }

    @Test
    fun checkEmailSignInWhenRegistered() {
        coEvery { authApi.fetchSignInMethodsForEmail(testUser.email) } returns listOf("password")
        coJustRun {
            authApi.signInWithEmailAndPassword(
                testUser.email,
                testUser.password
            )
        }

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
        coJustRun {
            authApi.signInWithEmailAndPassword(
                testUser.email,
                testUser.password
            )
        }

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
        composeTestRule.onNodeWithTag("login_email_input_error", true)
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

    @Test
    fun checkSignInButtonDisabledWhenFieldsEmpty() {
        composeTestRule.launchAmblorApp()
        composeTestRule.onNodeWithTag("login_submit_btn").assertIsNotEnabled()
    }

    @Test
    fun checkInvalidEmailsShowError() {
        composeTestRule.launchAmblorApp()
        val emailNode = composeTestRule.onNodeWithTag("login_email_input")

        listOf("hello", "hello@gmail", "hello.com", "hello@.com").forEach {
            emailNode.performTextInput(it)
            composeTestRule.onNodeWithTag("login_email_input_error", true).assertIsDisplayed()
                .assertTextEquals("Invalid Email")
            emailNode.performTextClearance()
        }
    }
}
