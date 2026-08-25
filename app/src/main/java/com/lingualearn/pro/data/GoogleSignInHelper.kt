package com.lingualearn.pro.data

import android.content.Context
import android.content.Intent
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.common.api.CommonStatusCodes
import com.lingualearn.pro.R

object GoogleSignInHelper {
    fun options(context: Context): GoogleSignInOptions =
        GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(context.getString(R.string.default_web_client_id))
            .requestEmail()
            .requestProfile()
            .build()

    fun idTokenFromResult(data: Intent?): String {
        val account = GoogleSignIn.getSignedInAccountFromIntent(data)
            .getResult(ApiException::class.java)
        return account.idToken
            ?: error("Google did not return an ID token. The web client ID may be wrong.")
    }

    fun userMessage(error: Throwable): String {
        val api = error as? ApiException ?: error.cause as? ApiException
        return when (api?.statusCode) {
            CommonStatusCodes.DEVELOPER_ERROR, 10 ->
                "This install isn't authorized for Google sign-in (error 10). The signing certificate still needs a few minutes to propagate, or Play App Signing’s SHA-1 still needs to be added in Firebase."
            CommonStatusCodes.NETWORK_ERROR, 7 ->
                "Network error during Google sign-in. Check your connection and try again."
            CommonStatusCodes.CANCELED, 12501 ->
                "Google sign-in was cancelled."
            12500 ->
                "Google sign-in failed. Update Google Play services and try again."
            12502 ->
                "Google sign-in is already in progress."
            else -> error.message?.takeIf { it.isNotBlank() && it != "12500: " }
                ?: "Google sign-in failed."
        }
    }
}
