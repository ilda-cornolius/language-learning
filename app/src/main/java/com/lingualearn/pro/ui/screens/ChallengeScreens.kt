package com.lingualearn.pro.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.lingualearn.pro.data.LanguageCourse
import com.lingualearn.pro.data.PreferencesStore
import com.lingualearn.pro.data.ProgressState
import com.lingualearn.pro.data.ProgressStore
import com.lingualearn.pro.data.SampleContent
import com.lingualearn.pro.data.SoundEffects
import com.lingualearn.pro.data.VocabWord
import com.lingualearn.pro.ui.components.AeroButton
import com.lingualearn.pro.ui.components.AeroProgressBar
import com.lingualearn.pro.ui.components.Badge
import com.lingualearn.pro.ui.components.BodyText
import com.lingualearn.pro.ui.components.CardTitle
import com.lingualearn.pro.ui.components.GlassCard
import com.lingualearn.pro.ui.components.GlassTile
import com.lingualearn.pro.ui.theme.TextMuted
import com.lingualearn.pro.ui.theme.TextPrimary
import com.lingualearn.pro.ui.theme.VistaAccent
import com.lingualearn.pro.ui.theme.VistaBlue
import com.lingualearn.pro.ui.theme.VistaGreen
import com.lingualearn.pro.ui.theme.VistaTeal
import kotlinx.coroutines.delay
import kotlin.random.Random

private const val SPEED_ID = "speed-round"
private const val MEMORY_ID = "memory-match"
private const val GRAMMAR_ID = "grammar-sprint"

@Composable
fun ChallengesScreen(
    progressStore: ProgressStore,
    onSpeedRound: () -> Unit,
    onMemoryMatch: () -> Unit,
    onGrammarSprint: () -> Unit,
) {
    val progress = progressStore.state
    Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
        PerfectWeekCard(progress)
        ChallengeEntry(
            title = "Speed Round",
            description = "Translate 20 vocabulary words in 120 seconds. Score 12 to win.",
            reward = "500 XP",
            completed = SPEED_ID in progress.completedChallengeIds,
            best = progress.bestScores["$SPEED_ID:score"]?.let { "Best: $it/20" },
            color = VistaAccent,
            onStart = onSpeedRound,
        )
        ChallengeEntry(
            title = "Memory Match",
            description = "Match 6 terms with their meanings in as few turns as possible.",
            reward = "300 XP",
            completed = MEMORY_ID in progress.completedChallengeIds,
            best = progress.bestScores["$MEMORY_ID:turns-inverse"]?.let { "Best: ${10_000 - it} turns" },
            color = VistaTeal,
            onStart = onMemoryMatch,
        )
        ChallengeEntry(
            title = "Grammar Sprint",
            description = "Race through today’s grammar questions before time runs out.",
            reward = "400 XP",
            completed = GRAMMAR_ID in progress.completedChallengeIds,
            best = progress.bestScores["$GRAMMAR_ID:score"]?.let { "Best: $it/6" },
            color = VistaBlue,
            onStart = onGrammarSprint,
        )
    }
}

@Composable
private fun PerfectWeekCard(progress: ProgressState) {
    val done = ProgressState.PERFECT_WEEK_ID in progress.completedChallengeIds
    val days = progress.perfectWeekProgress
    GlassCard(Modifier.fillMaxWidth()) {
        Column(Modifier.padding(20.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Row(
                Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                CardTitle("Perfect Week")
                Badge(if (done) "Completed" else "1000 XP", if (done) VistaGreen else VistaAccent)
            }
            BodyText("Complete lessons 7 days in a row")
            BodyText("$days / 7 days", color = TextMuted)
            AeroProgressBar(days / 7f, VistaAccent)
        }
    }
}

@Composable
private fun ChallengeEntry(
    title: String,
    description: String,
    reward: String,
    completed: Boolean,
    best: String?,
    color: Color,
    onStart: () -> Unit,
) {
    GlassCard(Modifier.fillMaxWidth()) {
        Column(Modifier.padding(20.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Row(
                Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                CardTitle(title)
                Badge(if (completed) "Completed" else reward, if (completed) VistaGreen else color)
            }
            BodyText(description)
            best?.let { BodyText(it, color = TextMuted) }
            AeroButton(if (completed) "Replay" else "Start", onClick = onStart, color = color)
        }
    }
}

@Composable
fun SpeedRoundScreen(course: LanguageCourse, progressStore: ProgressStore, onBack: () -> Unit) {
    val context = LocalContext.current
    val preferencesStore = remember(context) { PreferencesStore(context.applicationContext) }
    var run by rememberSaveable(course.id) { mutableStateOf(0) }
    var index by rememberSaveable(course.id, run) { mutableStateOf(0) }
    var score by rememberSaveable(course.id, run) { mutableStateOf(0) }
    var seconds by rememberSaveable(course.id, run) { mutableStateOf(120) }
    var finished by rememberSaveable(course.id, run) { mutableStateOf(false) }
    var xpEarned by rememberSaveable(course.id, run) { mutableStateOf<Int?>(null) }
    val words = remember(course.id) {
        val source = SampleContent.dictionaryWords(course.id)
        List(20) { source[it % source.size] }
    }

    fun finish(finalScore: Int) {
        if (finished) return
        finished = true
        progressStore.updateBestScore("$SPEED_ID:score", finalScore)
        xpEarned = if (finalScore >= 12) {
            progressStore.completeChallenge(
                SPEED_ID,
                500,
                finalScore,
                "$SPEED_ID:score",
                courseId = course.id,
            )
        } else {
            0
        }
    }
    LaunchedEffect(run, finished, seconds) {
        if (!finished && seconds > 0) {
            delay(1000)
            seconds--
            if (seconds == 0) finish(score)
        }
    }

    ChallengeHeader("Speed Round", "Translate 20 words · 12 correct to succeed", onBack)
    GlassCard(Modifier.fillMaxWidth()) {
        Column(Modifier.padding(20.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
            if (finished) {
                val won = score >= 12
                ChallengeResult(
                    success = won,
                    score = "$score / 20",
                    successText = if ((xpEarned ?: 0) > 0) {
                        "Speed Round marked complete. You earned ${xpEarned} XP!"
                    } else {
                        "Speed Round complete! You've already claimed this challenge reward."
                    },
                    failureText = "Score 12 or more to win.",
                    onRetry = { run++ },
                    onBack = onBack,
                    xpEarned = if (won) xpEarned else null,
                    xpReward = 500,
                )
            } else {
                val word = words[index]
                ChallengeStats("Time", formatSeconds(seconds), "Score", "$score · ${index + 1}/20")
                AeroProgressBar(index / 20f, course.accent)
                BodyText("Choose the English translation", color = TextMuted)
                CardTitle(word.term)
                speedOptions(words, index).forEach { option ->
                    ChallengeOption(option, course.accent) {
                        val correct = option == word.meaning
                        if (correct) SoundEffects.playCorrect(context, preferencesStore)
                        else SoundEffects.playIncorrect(context, preferencesStore)
                        val newScore = score + if (correct) 1 else 0
                        score = newScore
                        if (index == 19) {
                            if (newScore >= 12) SoundEffects.playSuccess(context, preferencesStore)
                            finish(newScore)
                        } else {
                            index++
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun MemoryMatchScreen(course: LanguageCourse, progressStore: ProgressStore, onBack: () -> Unit) {
    val context = LocalContext.current
    val preferencesStore = remember(context) { PreferencesStore(context.applicationContext) }
    var run by rememberSaveable(course.id) { mutableStateOf(0) }
    val pairs = remember(course.id) { SampleContent.practicePack(course.id).reviewWords.take(6) }
    val cards = remember(course.id, run) {
        (pairs.mapIndexed { i, word -> MemoryCard(i, word.term) } +
            pairs.mapIndexed { i, word -> MemoryCard(i, word.meaning) })
            .shuffled(Random(course.id.hashCode() + run))
    }
    var firstIndex by rememberSaveable(course.id, run) { mutableStateOf<Int?>(null) }
    var secondIndex by rememberSaveable(course.id, run) { mutableStateOf<Int?>(null) }
    var matchedPairs by rememberSaveable(course.id, run) { mutableStateOf(setOf<Int>()) }
    var turns by rememberSaveable(course.id, run) { mutableStateOf(0) }
    var message by rememberSaveable(course.id, run) { mutableStateOf("Select a term, then its meaning.") }
    var inputLocked by remember(course.id, run) { mutableStateOf(false) }
    var xpEarned by rememberSaveable(course.id, run) { mutableStateOf<Int?>(null) }
    val finished = matchedPairs.size == pairs.size

    LaunchedEffect(secondIndex, firstIndex) {
        val first = firstIndex
        val second = secondIndex
        if (first == null || second == null) return@LaunchedEffect
        inputLocked = true
        turns++
        if (cards[first].pairId == cards[second].pairId) {
            matchedPairs = matchedPairs + cards[first].pairId
            message = "Match found!"
            SoundEffects.playCorrect(context, preferencesStore)
            firstIndex = null
            secondIndex = null
            inputLocked = false
        } else {
            message = "Not a match. Try again."
            SoundEffects.playIncorrect(context, preferencesStore)
            delay(600)
            firstIndex = null
            secondIndex = null
            inputLocked = false
        }
    }

    LaunchedEffect(finished) {
        if (finished && xpEarned == null) {
            progressStore.updateBestScore("$MEMORY_ID:turns-inverse", 10_000 - turns)
            xpEarned = progressStore.completeChallenge(MEMORY_ID, 300, pairs.size, courseId = course.id)
            SoundEffects.playSuccess(context, preferencesStore)
        }
    }

    ChallengeHeader("Memory Match", "Match every ${course.name} term with its meaning", onBack)
    GlassCard(Modifier.fillMaxWidth()) {
        Column(Modifier.padding(20.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
            if (finished) {
                ChallengeResult(
                    success = true,
                    score = "$turns turns",
                    successText = if ((xpEarned ?: 0) > 0) {
                        "Memory Match marked complete. You earned ${xpEarned} XP!"
                    } else {
                        "All pairs matched! You've already claimed this challenge reward."
                    },
                    failureText = "",
                    onRetry = { run++ },
                    onBack = onBack,
                    xpEarned = xpEarned,
                    xpReward = 300,
                )
            } else {
                ChallengeStats("Turns", turns.toString(), "Matches", "${matchedPairs.size}/6")
                AeroProgressBar(matchedPairs.size / 6f, course.accent)
                BodyText(message)
                cards.chunked(2).forEach { row ->
                    Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                        row.forEach { card ->
                            val cardIndex = cards.indexOf(card)
                            val matched = card.pairId in matchedPairs
                            val faceUp = matched ||
                                firstIndex == cardIndex ||
                                secondIndex == cardIndex
                            GlassTile(
                                modifier = Modifier.weight(1f),
                                color = when {
                                    matched -> VistaGreen.copy(alpha = 0.45f)
                                    faceUp -> course.accent.copy(alpha = 0.45f)
                                    else -> Color(0x33FFFFFF)
                                },
                                onClick = {
                                    if (matched || inputLocked || faceUp) return@GlassTile
                                    if (firstIndex == null) {
                                        firstIndex = cardIndex
                                        message = "Now choose the matching card."
                                    } else if (secondIndex == null && firstIndex != cardIndex) {
                                        secondIndex = cardIndex
                                    }
                                },
                            ) {
                                Text(
                                    text = when {
                                        matched -> "✓ ${card.label}"
                                        faceUp -> card.label
                                        else -> "?"
                                    },
                                    color = TextPrimary,
                                    modifier = Modifier.fillMaxWidth().padding(14.dp),
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun GrammarSprintScreen(course: LanguageCourse, progressStore: ProgressStore, onBack: () -> Unit) {
    val context = LocalContext.current
    val preferencesStore = remember(context) { PreferencesStore(context.applicationContext) }
    var run by rememberSaveable(course.id) { mutableStateOf(0) }
    val questions = remember(course.id) {
        val source = SampleContent.dailyGrammarLesson(course.id).questions +
            SampleContent.practicePack(course.id).grammarQuestions
        List(6) { source[it % source.size] }
    }
    var index by rememberSaveable(course.id, run) { mutableStateOf(0) }
    var score by rememberSaveable(course.id, run) { mutableStateOf(0) }
    var seconds by rememberSaveable(course.id, run) { mutableStateOf(60) }
    var finished by rememberSaveable(course.id, run) { mutableStateOf(false) }
    var xpEarned by rememberSaveable(course.id, run) { mutableStateOf<Int?>(null) }

    fun finish(finalScore: Int) {
        if (finished) return
        finished = true
        progressStore.updateBestScore("$GRAMMAR_ID:score", finalScore)
        val won = finalScore >= 4 && seconds > 0
        xpEarned = if (won) {
            progressStore.completeChallenge(
                GRAMMAR_ID,
                400,
                finalScore,
                "$GRAMMAR_ID:score",
                courseId = course.id,
            )
        } else {
            0
        }
    }
    LaunchedEffect(run, finished, seconds) {
        if (!finished && seconds > 0) {
            delay(1000)
            seconds--
            if (seconds == 0) finish(score)
        }
    }

    ChallengeHeader("Grammar Sprint", "Answer 6 questions in 60 seconds · 4 correct to win", onBack)
    GlassCard(Modifier.fillMaxWidth()) {
        Column(Modifier.padding(20.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
            if (finished) {
                val won = score >= 4 && seconds > 0
                ChallengeResult(
                    success = won,
                    score = "$score / 6",
                    successText = if ((xpEarned ?: 0) > 0) {
                        "Grammar Sprint marked complete. You earned ${xpEarned} XP!"
                    } else {
                        "Grammar Sprint complete! You've already claimed this challenge reward."
                    },
                    failureText = if (seconds == 0) {
                        "Time expired. Get 4 correct answers to win."
                    } else {
                        "Get at least 4 correct answers to win."
                    },
                    onRetry = { run++ },
                    onBack = onBack,
                    xpEarned = if (won) xpEarned else null,
                    xpReward = 400,
                )
            } else {
                val question = questions[index]
                ChallengeStats("Time", formatSeconds(seconds), "Score", "$score · ${index + 1}/6")
                AeroProgressBar(index / 6f, course.accent)
                CardTitle(question.prompt)
                question.options.forEachIndexed { optionIndex, option ->
                    ChallengeOption(option, course.accent) {
                        val correct = optionIndex == question.correctIndex
                        if (correct) SoundEffects.playCorrect(context, preferencesStore)
                        else SoundEffects.playIncorrect(context, preferencesStore)
                        val newScore = score + if (correct) 1 else 0
                        score = newScore
                        if (index == questions.lastIndex) {
                            if (newScore >= 4) SoundEffects.playSuccess(context, preferencesStore)
                            finish(newScore)
                        } else {
                            index++
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun ChallengeHeader(title: String, subtitle: String, onBack: () -> Unit) {
    AeroButton("Back to Challenges", onClick = onBack, color = Color(0xFF526777))
    GlassCard(Modifier.fillMaxWidth()) {
        Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(5.dp)) {
            CardTitle(title)
            BodyText(subtitle)
        }
    }
}

@Composable
private fun ChallengeStats(labelOne: String, valueOne: String, labelTwo: String, valueTwo: String) {
    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
        Column {
            BodyText(labelOne, color = TextMuted)
            Text(valueOne, color = TextPrimary, fontWeight = FontWeight.Bold)
        }
        Column(horizontalAlignment = Alignment.End) {
            BodyText(labelTwo, color = TextMuted)
            Text(valueTwo, color = TextPrimary, fontWeight = FontWeight.Bold)
        }
    }
}

@Composable
private fun ChallengeOption(text: String, color: Color, onClick: () -> Unit) {
    GlassTile(Modifier.fillMaxWidth(), onClick = onClick, color = color.copy(alpha = 0.22f)) {
        Text(text, color = TextPrimary, modifier = Modifier.padding(14.dp))
    }
}

@Composable
private fun ChallengeResult(
    success: Boolean,
    score: String,
    successText: String,
    failureText: String,
    onRetry: () -> Unit,
    onBack: () -> Unit,
    xpEarned: Int? = null,
    xpReward: Int = 0,
) {
    Badge(
        text = when {
            !success -> "TRY AGAIN"
            xpEarned != null && xpEarned > 0 -> "COMPLETED · +$xpEarned XP"
            else -> "COMPLETED"
        },
        color = if (success) VistaGreen else VistaAccent,
    )
    Text(
        text = score,
        style = MaterialTheme.typography.headlineMedium,
        color = if (success) VistaGreen else VistaAccent,
    )
    if (success && xpEarned != null) {
        Text(
            text = if (xpEarned > 0) "+$xpEarned XP" else "Already completed · +0 XP this run",
            color = if (xpEarned > 0) VistaGreen else TextMuted,
            fontWeight = FontWeight.Bold,
            style = MaterialTheme.typography.titleLarge,
        )
        if (xpEarned > 0 && xpReward > 0 && xpEarned != xpReward) {
            BodyText("Reward for this challenge: $xpReward XP", color = TextMuted)
        }
    }
    BodyText(if (success) successText else failureText)
    Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
        AeroButton("Retry", onClick = onRetry, color = VistaTeal)
        AeroButton("Back", onClick = onBack, color = Color(0xFF526777))
    }
}

private fun speedOptions(words: List<VocabWord>, index: Int): List<String> {
    val answers = mutableListOf(words[index].meaning)
    var offset = 1
    while (answers.size < 4) {
        val candidate = words[(index + offset) % words.size].meaning
        if (candidate !in answers) answers += candidate
        offset++
    }
    return answers.shuffled(Random(index * 37 + 11))
}

private fun formatSeconds(seconds: Int) = "%d:%02d".format(seconds / 60, seconds % 60)

private data class MemoryCard(val pairId: Int, val label: String)
