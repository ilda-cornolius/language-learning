package com.lingualearn.pro.ui.screens

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.lingualearn.pro.R
import com.lingualearn.pro.data.CloudWordRepository
import com.lingualearn.pro.ui.components.AeroBackground
import com.lingualearn.pro.ui.components.AeroButton
import com.lingualearn.pro.ui.components.BodyText
import com.lingualearn.pro.ui.components.GlassCard
import com.lingualearn.pro.ui.theme.TextMuted
import com.lingualearn.pro.ui.theme.TextPrimary
import com.lingualearn.pro.ui.theme.VistaAccent
import com.lingualearn.pro.ui.theme.VistaBlue
import kotlinx.coroutines.launch

@Composable
fun TitleAuthScreen(onSignedIn: () -> Unit) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    var signingIn by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    val googleSignInClient = remember(context) {
        val options = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(context.getString(R.string.default_web_client_id))
            .requestEmail()
            .build()
        GoogleSignIn.getClient(context, options)
    }

    val signInLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.StartActivityForResult(),
    ) { result ->
        val account = runCatching {
            GoogleSignIn.getSignedInAccountFromIntent(result.data)
                .getResult(ApiException::class.java)
        }.getOrNull()
        val idToken = account?.idToken
        if (idToken == null) {
            signingIn = false
            errorMessage = "Google sign-in was cancelled or unsuccessful."
            return@rememberLauncherForActivityResult
        }
        scope.launch {
            signingIn = true
            errorMessage = null
            runCatching { CloudWordRepository.signInWithGoogle(idToken) }
                .onSuccess {
                    signingIn = false
                    onSignedIn()
                }
                .onFailure {
                    signingIn = false
                    errorMessage = it.message ?: "Firebase sign-in failed."
                }
        }
    }

    AeroBackground {
        Box(
            Modifier
                .fillMaxSize()
                .safeDrawingPadding()
                .padding(horizontal = 28.dp),
            contentAlignment = Alignment.Center,
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(20.dp),
                modifier = Modifier.fillMaxWidth(),
            ) {
                Text(
                    text = "LinguaLearn Pro",
                    color = TextPrimary,
                    fontWeight = FontWeight.Bold,
                    fontSize = 40.sp,
                    lineHeight = 46.sp,
                    textAlign = TextAlign.Center,
                    style = MaterialTheme.typography.headlineSmall.copy(
                        fontWeight = FontWeight.Bold,
                        fontSize = 40.sp,
                        lineHeight = 46.sp,
                    ),
                )
                BodyText(
                    text = "Learn languages with lessons, practice, and challenges — progress that follows you.",
                    color = TextMuted,
                    modifier = Modifier.fillMaxWidth(),
                )
                GlassCard(Modifier.fillMaxWidth()) {
                    Column(
                        Modifier.padding(20.dp),
                        verticalArrangement = Arrangement.spacedBy(14.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                    ) {
                        AeroButton(
                            text = if (signingIn) "Signing in…" else "Continue with Google",
                            onClick = {
                                if (signingIn) return@AeroButton
                                errorMessage = null
                                signingIn = true
                                signInLauncher.launch(googleSignInClient.signInIntent)
                            },
                            color = VistaAccent,
                            modifier = Modifier.fillMaxWidth(),
                        )
                        if (signingIn) {
                            BodyText("Connecting your Google account…", color = TextMuted)
                        }
                        errorMessage?.let {
                            BodyText(it, color = VistaBlue)
                        }
                    }
                }
            }
        }
    }
}
