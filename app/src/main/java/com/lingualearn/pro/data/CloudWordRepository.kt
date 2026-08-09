package com.lingualearn.pro.data

import android.util.Base64
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import kotlin.coroutines.suspendCoroutine

object CloudWordRepository {
    private val auth: FirebaseAuth by lazy { FirebaseAuth.getInstance() }
    private val firestore: FirebaseFirestore by lazy { FirebaseFirestore.getInstance() }

    @Volatile
    var lastSyncAtMs: Long? = null
        private set

    val isSignedIn: Boolean
        get() = auth.currentUser != null

    val userLabel: String?
        get() = auth.currentUser?.displayName ?: auth.currentUser?.email

    suspend fun signInWithGoogle(idToken: String) {
        val credential = GoogleAuthProvider.getCredential(idToken, null)
        val result = auth.signInWithCredential(credential).awaitResult()
        val user = result.user ?: error("Google sign-in did not return a Firebase user")
        firestore.collection("users")
            .document(user.uid)
            .set(
                mapOf(
                    "displayName" to user.displayName,
                    "email" to user.email,
                    "lastSeenAt" to FieldValue.serverTimestamp(),
                ),
                SetOptions.merge(),
            )
            .awaitResult()
        markSynced()
    }

    suspend fun saveWord(
        languageId: String,
        languageName: String,
        word: String,
        meaning: String,
        exampleSentence: String = "",
        source: String,
    ) {
        val user = auth.currentUser ?: error("Sign in with Google before saving words online")
        val normalizedWord = word.trim().lowercase()
        val documentId = Base64.encodeToString(
            "$languageId:$normalizedWord".toByteArray(),
            Base64.URL_SAFE or Base64.NO_WRAP or Base64.NO_PADDING,
        )
        firestore.collection("users")
            .document(user.uid)
            .collection("words")
            .document(documentId)
            .set(
                mapOf(
                    "word" to word.trim(),
                    "meaning" to meaning.trim(),
                    "exampleSentence" to exampleSentence.trim(),
                    "languageId" to languageId,
                    "languageName" to languageName,
                    "sourceLanguage" to "English",
                    "source" to source,
                    "updatedAt" to FieldValue.serverTimestamp(),
                ),
                SetOptions.merge(),
            )
            .awaitResult()
        markSynced()
    }

    fun markSynced(atMs: Long = System.currentTimeMillis()) {
        lastSyncAtMs = atMs
    }
}

private suspend fun <T> Task<T>.awaitResult(): T = suspendCoroutine { continuation ->
    addOnSuccessListener { continuation.resume(it) }
    addOnFailureListener { continuation.resumeWithException(it) }
    addOnCanceledListener { continuation.resumeWithException(IllegalStateException("Firebase task cancelled")) }
}
