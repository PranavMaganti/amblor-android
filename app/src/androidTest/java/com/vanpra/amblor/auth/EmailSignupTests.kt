package com.vanpra.amblor.auth

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertIsNotEnabled
import androidx.compose.ui.test.assertTextEquals
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.performClick
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.vanpra.amblor.data.AmblorDatabase
import com.vanpra.amblor.interfaces.AmblorApi
import com.vanpra.amblor.interfaces.AuthenticationApi
import com.vanpra.amblor.setAmblorLayout
import com.vanpra.amblor.signUpWithUser
import com.vanpra.amblor.testUser
import io.mockk.coEvery
import io.mockk.coJustRun
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
@Suppress("IllegalIdentifier")
class EmailSignupTests : KoinTest {
    @get:Rule
    val composeTestRule = createComposeRule()

    private lateinit var authApi: AuthenticationApi
    private lateinit var amblorApi: AmblorApi

    @Before
    fun setupMockDependencies() {
        authApi = mockk(relaxUnitFun = true) {
            /* Need this as MainActivity onCreate calls this */
            every { isUserSignedIn() } returns false
            coEvery { getToken() } returns com.vanpra.amblor.testUser.token
            coEvery { isUserEmailVerified() } returns true
        }
        val amblorDatabase = mockk<AmblorDatabase> {
            coEvery { scrobbleDao().getAllScrobbles() } returns MutableStateFlow(listOf())
            coJustRun { scrobbleDao().insertAllScrobbles(listOf()) }
        }
        amblorApi = mockk {
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
    fun checkSignupButtonDisabledWhenAllFieldsEmpty() {
        composeTestRule.setAmblorLayout()
        composeTestRule.onNodeWithTag("signup_btn", true).performClick()
        composeTestRule.onNodeWithTag("complete_signup_btn", true).assertIsNotEnabled()
    }

    @Test
    fun checkUsedUsernameDisplaysError() {
        coEvery { authApi.getToken() } returns testUser.token
        coEvery { amblorApi.signUpUser(testUser.username, testUser.token) } returns false

        composeTestRule.setAmblorLayout()
        composeTestRule.signUpWithUser(testUser)
        composeTestRule.onNodeWithTag("complete_signup_btn", true).performClick()
        composeTestRule.onNodeWithTag("username_signup_error", true).assertIsDisplayed()
            .assertTextEquals("Username Taken")
    }
}
