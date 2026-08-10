package com.lingualearn.pro.data

import android.util.Base64
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import kotlin.coroutines.suspendCoroutine

data class UserProgressSnapshot(
    val state: ProgressState,
    val awardIds: Set<String>,
) {
    val level: Int get() = state.level
}

object CloudWordRepository {
    private val auth: FirebaseAuth by lazy { FirebaseAuth.getInstance() }
    private val firestore: FirebaseFirestore by lazy { FirebaseFirestore.getInstance() }

    @Volatile
    var lastSyncAtMs: Long? = null
        private set

    val isSignedIn: Boolean
        get() = auth.currentUser != null

    val uid: String?
        get() = auth.currentUser?.uid

    val userLabel: String?
        get() = auth.currentUser?.displayName ?: auth.currentUser?.email

    val userDisplayName: String?
        get() = auth.currentUser?.displayName

    val userEmail: String?
        get() = auth.currentUser?.email

    val userPhotoUrl: String?
        get() = auth.currentUser?.photoUrl?.toString()

    /**
     * Signs in with Google, ensures a `users/{uid}` progress document exists,
     * and returns the user's cloud progress snapshot.
     */
    suspend fun signInWithGoogle(idToken: String): UserProgressSnapshot {
        val credential = GoogleAuthProvider.getCredential(idToken, null)
        val result = auth.signInWithCredential(credential).awaitResult()
        val user = result.user ?: error("Google sign-in did not return a Firebase user")
        val docRef = firestore.collection("users").document(user.uid)
        val existing = docRef.get().awaitResult()

        val profileMerge = mapOf(
            "displayName" to user.displayName,
            "email" to user.email,
            "lastSeenAt" to FieldValue.serverTimestamp(),
        )

        if (!existing.hasProgressFields()) {
            docRef.set(
                profileMerge + mapOf(
                    "totalXp" to 0,
                    "lessonsCompleted" to 0,
                    "level" to 1,
                    "completionDates" to emptyList<String>(),
                    "completedChallengeIds" to emptyList<String>(),
                    "bestScores" to emptyMap<String, Int>(),
                    "courseXp" to emptyMap<String, Int>(),
                    "awardIds" to emptyList<String>(),
                    "createdAt" to FieldValue.serverTimestamp(),
                    "updatedAt" to FieldValue.serverTimestamp(),
                ),
                SetOptions.merge(),
            ).awaitResult()
            markSynced()
            return UserProgressSnapshot(ProgressState(), emptySet())
        }

        docRef.set(profileMerge, SetOptions.merge()).awaitResult()
        markSynced()
        return existing.toProgressSnapshot()
    }

    suspend fun loadProgress(): UserProgressSnapshot {
        val user = auth.currentUser ?: error("Sign in with Google before loading progress")
        val snapshot = firestore.collection("users").document(user.uid).get().awaitResult()
        if (!snapshot.hasProgressFields()) {
            return UserProgressSnapshot(ProgressState(), emptySet())
        }
        return snapshot.toProgressSnapshot()
    }

    suspend fun saveProgress(state: ProgressState, awardIds: Set<String>) {
        val user = auth.currentUser ?: error("Sign in with Google before saving progress")
        firestore.collection("users")
            .document(user.uid)
            .set(
                mapOf(
                    "totalXp" to state.totalXp,
                    "lessonsCompleted" to state.lessonsCompleted,
                    "level" to state.level,
                    "completionDates" to state.completionDates.toList(),
                    "completedChallengeIds" to state.completedChallengeIds.toList(),
                    "bestScores" to state.bestScores,
                    "courseXp" to state.courseXp,
                    "awardIds" to awardIds.toList(),
                    "updatedAt" to FieldValue.serverTimestamp(),
                    "lastSeenAt" to FieldValue.serverTimestamp(),
                ),
                SetOptions.merge(),
            )
            .awaitResult()
        markSynced()
    }

    fun signOut() {
        auth.signOut()
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

private fun DocumentSnapshot.hasProgressFields(): Boolean =
    exists() && contains("totalXp") && contains("awardIds")

@Suppress("UNCHECKED_CAST")
private fun DocumentSnapshot.toProgressSnapshot(): UserProgressSnapshot {
    val completionDates = (get("completionDates") as? List<*>)
        ?.mapNotNull { it as? String }
        ?.toSet()
        ?: emptySet()
    val completedChallengeIds = (get("completedChallengeIds") as? List<*>)
        ?.mapNotNull { it as? String }
        ?.toSet()
        ?: emptySet()
    val awardIds = (get("awardIds") as? List<*>)
        ?.mapNotNull { it as? String }
        ?.toSet()
        ?: emptySet()
    val bestScores = (get("bestScores") as? Map<*, *>)
        ?.mapNotNull { (k, v) ->
            val key = k as? String ?: return@mapNotNull null
            val value = when (v) {
                is Number -> v.toInt()
                else -> return@mapNotNull null
            }
            key to value
        }
        ?.toMap()
        ?: emptyMap()
    val courseXp = (get("courseXp") as? Map<*, *>)
        ?.mapNotNull { (k, v) ->
            val key = k as? String ?: return@mapNotNull null
            val value = when (v) {
                is Number -> v.toInt()
                else -> return@mapNotNull null
            }
            key to value
        }
        ?.toMap()
        ?: emptyMap()
    val state = ProgressState(
        totalXp = getLong("totalXp")?.toInt() ?: 0,
        lessonsCompleted = getLong("lessonsCompleted")?.toInt() ?: 0,
        completionDates = completionDates,
        completedChallengeIds = completedChallengeIds,
        bestScores = bestScores,
        courseXp = courseXp,
    )
    return UserProgressSnapshot(state, awardIds)
}

private suspend fun <T> Task<T>.awaitResult(): T = suspendCoroutine { continuation ->
    addOnSuccessListener { continuation.resume(it) }
    addOnFailureListener { continuation.resumeWithException(it) }
    addOnCanceledListener { continuation.resumeWithException(IllegalStateException("Firebase task cancelled")) }
}
