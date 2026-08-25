package com.lingualearn.pro.ui.screens

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.lingualearn.pro.R
import com.lingualearn.pro.data.CloudWordRepository
import com.lingualearn.pro.data.GoogleSignInHelper
import com.lingualearn.pro.ui.components.BodyText
import com.lingualearn.pro.ui.components.CinematicBackground
import com.lingualearn.pro.ui.components.CinematicCommandButton
import com.lingualearn.pro.ui.components.CinematicFadeIn
import com.lingualearn.pro.ui.components.CinematicLogoFade
import com.lingualearn.pro.ui.components.cinematicGlow
import com.lingualearn.pro.ui.theme.TextMuted
import com.lingualearn.pro.ui.theme.TextPrimary
import com.lingualearn.pro.ui.theme.VistaBlue
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@Composable
fun TitleAuthScreen(onSignedIn: () -> Unit) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    var signingIn by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    var phase by remember { mutableIntStateOf(0) }

    val googleSignInClient = remember(context) {
        GoogleSignIn.getClient(context, GoogleSignInHelper.options(context))
    }

    val signInLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.StartActivityForResult(),
    ) { result ->
        val idToken = runCatching { GoogleSignInHelper.idTokenFromResult(result.data) }
            .onFailure { error ->
                signingIn = false
                errorMessage = GoogleSignInHelper.userMessage(error)
            }
            .getOrNull() ?: return@rememberLauncherForActivityResult
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

    LaunchedEffect(Unit) {
        delay(280)
        phase = 1
        delay(1100)
        phase = 2
        delay(900)
        phase = 3
        delay(850)
        phase = 4
    }

    CinematicBackground {
        Box(
            Modifier
                .fillMaxSize()
                .safeDrawingPadding()
                .padding(horizontal = 32.dp),
            contentAlignment = Alignment.Center,
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(18.dp),
                modifier = Modifier.fillMaxWidth(),
            ) {
                CinematicLogoFade(visible = phase >= 1) {
                    Box(
                        Modifier.cinematicGlow(Color(0x66F4FBFF), 48.dp),
                        contentAlignment = Alignment.Center,
                    ) {
                        Image(
                            painter = painterResource(R.drawable.ic_lumina_icon),
                            contentDescription = "Lumina logo",
                            modifier = Modifier.size(72.dp),
                            contentScale = ContentScale.Fit,
                        )
                    }
                }
                CinematicFadeIn(visible = phase >= 2, durationMillis = 1600, rise = 10.dp) {
                    Text(
                        text = "LUMINA",
                        color = TextPrimary,
                        fontFamily = FontFamily.Serif,
                        fontWeight = FontWeight.Light,
                        fontSize = 48.sp,
                        letterSpacing = 14.sp,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.fillMaxWidth(),
                    )
                }
                CinematicFadeIn(visible = phase >= 3, durationMillis = 1400, rise = 12.dp) {
                    Text(
                        text = "A light for every language.",
                        color = TextMuted,
                        fontFamily = FontFamily.Serif,
                        fontWeight = FontWeight.Normal,
                        fontSize = 16.sp,
                        letterSpacing = 1.4.sp,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.fillMaxWidth(),
                    )
                }
                CinematicFadeIn(
                    visible = phase >= 4,
                    durationMillis = 1300,
                    rise = 14.dp,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 28.dp),
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(14.dp),
                        modifier = Modifier.fillMaxWidth(),
                    ) {
                        val enabled = !signingIn
                        CinematicCommandButton(
                            text = if (signingIn) "Logging in" else "Continue with Google",
                            onClick = {
                                errorMessage = null
                                signingIn = true
                                googleSignInClient.signOut().addOnCompleteListener {
                                    signInLauncher.launch(googleSignInClient.signInIntent)
                                }
                            },
                            enabled = enabled,
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
