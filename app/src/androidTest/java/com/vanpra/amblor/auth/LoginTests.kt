package com.vanpra.amblor.auth

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
import com.vanpra.amblor.loginWithUser
import com.vanpra.amblor.setAmblorLayout
import com.vanpra.amblor.testUser
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

@RunWith(AndroidJUnit4::class)
class LoginTests : KoinTest {
    @get:Rule
    val composeTestRule = createComposeRule()

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

        composeTestRule.setAmblorLayout()
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

        composeTestRule.setAmblorLayout()
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

        composeTestRule.setAmblorLayout()
        composeTestRule.loginWithUser(testUser)
        composeTestRule.onNodeWithTag("login_email_input_error", true)
            .assertTextEquals("Email not registered")

        coVerify(exactly = 1) { authApi.fetchSignInMethodsForEmail(testUser.email) }
    }

    @Test
    fun checkEmailErrorWhenRegWithDifferentProvider() {
        coEvery { authApi.fetchSignInMethodsForEmail(testUser.email) } returns listOf("Google")

        composeTestRule.setAmblorLayout()
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

        composeTestRule.setAmblorLayout()
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
        composeTestRule.setAmblorLayout()
        composeTestRule.onNodeWithTag("login_submit_btn").assertIsNotEnabled()
    }

    @Test
    fun checkInvalidEmailsShowError() {
        composeTestRule.setAmblorLayout()
        val emailNode = composeTestRule.onNodeWithTag("login_email_input")

        listOf("hello", "hello@gmail", "hello.com", "hello@.com").forEach {
            emailNode.performTextInput(it)
            composeTestRule.onNodeWithTag("login_email_input_error", true).assertIsDisplayed()
                .assertTextEquals("Invalid Email")
            emailNode.performTextClearance()
        }
    }
}
