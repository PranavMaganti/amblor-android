package com.vanpra.amblor.ui.layouts.auth

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.vanpra.amblor.R
import com.vanpra.amblor.util.BackButtonTitle
import com.vanpra.amblor.util.PrimaryTextButton
import dev.chrisbanes.accompanist.coil.CoilImage

@Composable
fun EmailVerification(authViewModel: AuthViewModel) {
    Box(Modifier.fillMaxSize()) {
        BackButtonTitle(title = "Verify your email") {
            authViewModel.signOut()
        }

        Column(
            Modifier
                .align(Alignment.Center)
                .fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            CoilImage(
                data = R.drawable.ic_email_verification,
                contentDescription = null,
                colorFilter = ColorFilter.tint(MaterialTheme.colors.onBackground),
                modifier = Modifier.fillMaxWidth(0.3f)
            )

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                """An email has been sent to ${authViewModel.getEmailAddress()}. Please click on the link provided to verify you email.""",
                style = TextStyle(color = MaterialTheme.colors.onBackground, fontSize = 18.sp),
                modifier = Modifier.fillMaxWidth(0.8f),
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(16.dp))

            Column(Modifier.fillMaxWidth(0.8f)) {
                PrimaryTextButton(
                    text = "Resend Verification Email",
                    onClick = { authViewModel.sendVerificationCode() }
                )

                Spacer(modifier = Modifier.height(16.dp))

                PrimaryTextButton(
                    text = "Check Email Verification",
                    onClick = { authViewModel.refreshAndCheckVerification() }
                )
            }
        }
    }
}
