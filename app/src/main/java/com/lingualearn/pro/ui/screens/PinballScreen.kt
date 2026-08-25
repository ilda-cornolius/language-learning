package com.lingualearn.pro.ui.screens

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.runtime.withFrameNanos
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextMeasurer
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.drawText
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.rememberTextMeasurer
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Constraints
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.lingualearn.pro.data.LanguageCourse
import com.lingualearn.pro.data.PreferencesStore
import com.lingualearn.pro.data.ProgressStore
import com.lingualearn.pro.data.SampleContent
import com.lingualearn.pro.data.SoundEffects
import com.lingualearn.pro.data.VocabWord
import com.lingualearn.pro.ui.components.AeroButton
import com.lingualearn.pro.ui.components.Badge
import com.lingualearn.pro.ui.components.BodyText
import com.lingualearn.pro.ui.components.CardTitle
import com.lingualearn.pro.ui.components.GlassCard
import com.lingualearn.pro.ui.components.PhraseWithReading
import com.lingualearn.pro.ui.theme.TextMuted
import com.lingualearn.pro.ui.theme.TextPrimary
import com.lingualearn.pro.ui.theme.VistaGreen
import com.lingualearn.pro.ui.theme.VistaTeal
import kotlin.math.PI
import kotlin.math.abs
import kotlin.math.cos
import kotlin.math.hypot
import kotlin.math.sin
import kotlin.random.Random

internal const val PINBALL_ID = "vocab-pinball"
internal const val PINBALL_FIRST_XP = 800
internal const val PINBALL_DAILY_XP = 250
private const val PINBALL_TARGET = 5

private enum class PinballPhase { Ready, Playing, Won, Lost }

@Composable
fun VocabPinballScreen(
    course: LanguageCourse,
    progressStore: ProgressStore,
    onBack: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val context = LocalContext.current
    val preferencesStore = remember(context) { PreferencesStore(context.applicationContext) }
    var run by rememberSaveable(course.id) { mutableStateOf(0) }
    val words = remember(course.id, run) {
        val source = SampleContent.dictionaryWords(course.id).ifEmpty {
            listOf(VocabWord("hola", "hello"))
        }
        List(PINBALL_TARGET) { source[it % source.size] }
            .distinctBy { it.term }
            .let { taken ->
                if (taken.size >= PINBALL_TARGET) taken.take(PINBALL_TARGET)
                else List(PINBALL_TARGET) { source[it % source.size] }
            }
    }
    val sim = remember(course.id, run, words) { PinballSim(words) }
    var frame by remember(course.id, run) { mutableIntStateOf(0) }
    var pulse by remember(course.id, run) { mutableIntStateOf(0) }
    var xpEarned by rememberSaveable(course.id, run) { mutableStateOf<Int?>(null) }
    val pendingXp = remember(course.id, run, progressStore.state.totalXp) {
        progressStore.pendingQuestXp(PINBALL_ID, PINBALL_FIRST_XP, PINBALL_DAILY_XP)
    }
    val textMeasurer = rememberTextMeasurer()
    val lastHit = sim.lastHit
    val collected = sim.collected.size

    LaunchedEffect(run, pulse) {
        if (sim.phase != PinballPhase.Playing) return@LaunchedEffect
        var last = 0L
        while (sim.phase == PinballPhase.Playing) {
            withFrameNanos { now ->
                if (last != 0L) {
                    val dt = ((now - last) / 1_000_000_000f).coerceIn(0.001f, 0.033f)
                    val events = sim.step(dt)
                    if (events.newWord) SoundEffects.playCorrect(context, preferencesStore)
                    else if (events.bumper) SoundEffects.playBumper(context, preferencesStore)
                    if (events.drain) SoundEffects.playIncorrect(context, preferencesStore)
                    frame++
                }
                last = now
            }
        }
    }

    LaunchedEffect(sim.phase, frame) {
        if (sim.phase == PinballPhase.Won && xpEarned == null) {
            xpEarned = progressStore.completeQuest(
                questId = PINBALL_ID,
                firstClearXp = PINBALL_FIRST_XP,
                dailyXp = PINBALL_DAILY_XP,
                score = sim.score,
                scoreKey = "$PINBALL_ID:score",
                courseId = course.id,
            )
            SoundEffects.playSuccess(context, preferencesStore)
        }
    }

    Column(
        modifier.fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(8.dp),
    ) {
    AeroButton("Back to Quests", onClick = onBack, color = Color(0xFF526777))
    GlassCard(Modifier.fillMaxWidth()) {
        Column(Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Row(
                Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                CardTitle("Word Pinball")
                Badge(
                    if (pendingXp > 0) "+$pendingXp XP" else "Replay",
                    if (pendingXp > 0) VistaTeal else VistaGreen,
                )
            }
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                BodyText("Balls ${sim.ballsLeft}", color = TextMuted)
                BodyText("${sim.score} pts", color = TextPrimary)
                BodyText("$collected / $PINBALL_TARGET words", color = TextMuted)
            }
            if (lastHit != null) {
                PhraseWithReading(
                    phrase = "${lastHit.term}  ·  ${lastHit.meaning}",
                    reading = lastHit.reading,
                    phraseWeight = FontWeight.SemiBold,
                )
            } else {
                BodyText("Hold left / right to flip. Light every orb to bank the quest XP.")
            }
        }
    }

    Box(
        Modifier
            .weight(1f)
            .fillMaxWidth()
            .pointerInput(run) {
                awaitPointerEventScope {
                    while (true) {
                        val event = awaitPointerEvent()
                        var left = false
                        var right = false
                        var any = false
                        event.changes.forEach { change ->
                            if (change.pressed) {
                                any = true
                                if (change.position.x < size.width * 0.5f) left = true else right = true
                            }
                        }
                        sim.leftPressed = left
                        sim.rightPressed = right
                        if (any && sim.phase == PinballPhase.Ready) {
                            sim.launch()
                            pulse++
                        }
                    }
                }
            },
    ) {
        Canvas(Modifier.fillMaxSize()) {
            @Suppress("UNUSED_EXPRESSION")
            frame
            drawPinballTable(sim, textMeasurer)
        }
        when (sim.phase) {
            PinballPhase.Ready -> PinballOverlay(
                title = if (sim.ballsLeft == 3) "Word Pinball" else "Next ball",
                body = if (sim.ballsLeft == 3) {
                    "Launch the glass orb, then hold left / right to flip. Light five vocab bumpers."
                } else {
                    "${sim.ballsLeft} ball${if (sim.ballsLeft == 1) "" else "s"} left."
                },
                action = "Launch" to {
                    sim.launch()
                    pulse++
                },
            )
            PinballPhase.Won -> PinballOverlay(
                title = "Quest clear",
                body = if ((xpEarned ?: 0) > 0) {
                    "Every orb lit. You earned ${xpEarned} XP!"
                } else {
                    "Every orb lit. Today's pinball XP is already in your pocket."
                },
                action = "Play again" to { run++ },
            )
            PinballPhase.Lost -> PinballOverlay(
                title = "Drain",
                body = "Light all five orbs to claim the quest. ${sim.collected.size} collected · ${sim.score} pts.",
                action = "Try again" to { run++ },
            )
            PinballPhase.Playing -> Unit
        }
    }
    }
}

@Composable
private fun PinballOverlay(
    title: String,
    body: String,
    action: Pair<String, () -> Unit>?,
) {
    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        GlassCard(Modifier.fillMaxWidth(0.86f)) {
            Column(
                Modifier.padding(20.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleLarge,
                    color = TextPrimary,
                    fontWeight = FontWeight.SemiBold,
                    textAlign = TextAlign.Center,
                )
                BodyText(body, color = TextMuted)
                action?.let { (label, onClick) ->
                    AeroButton(label, onClick = onClick, color = VistaTeal)
                }
            }
        }
    }
}

private fun DrawScope.drawPinballTable(sim: PinballSim, textMeasurer: TextMeasurer) {
    val w = size.width
    val h = size.height
    val table = minOf(w, h * 0.62f)
    val left = (w - table) / 2f
    val top = (h - table / 0.62f).coerceAtLeast(0f) / 2f
    val scaleX = table
    val scaleY = table / 0.62f
    fun sx(x: Float) = left + x * scaleX
    fun sy(y: Float) = top + y * scaleY
    fun sr(r: Float) = r * scaleX

    drawRect(
        brush = Brush.verticalGradient(
            colors = listOf(Color(0xFF163A62), Color(0xFF0B6B7A), Color(0xFF14532D)),
        ),
    )
    drawCircle(
        brush = Brush.radialGradient(
            colors = listOf(Color(0x66E0F9FF), Color.Transparent),
            center = Offset(sx(0.5f), sy(0.18f)),
            radius = scaleX * 0.7f,
        ),
        radius = scaleX * 0.7f,
        center = Offset(sx(0.5f), sy(0.18f)),
    )
    val water = Path().apply {
        moveTo(sx(0.04f), sy(0.04f))
        cubicTo(sx(0.35f), sy(-0.02f), sx(0.7f), sy(0.08f), sx(0.96f), sy(0.04f))
        lineTo(sx(0.96f), sy(0.98f))
        lineTo(sx(0.04f), sy(0.98f))
        close()
    }
    drawPath(
        water,
        brush = Brush.verticalGradient(
            colors = listOf(Color(0x5538BDF8), Color(0x3322D3EE), Color(0x2210B981)),
        ),
    )
    drawPath(water, Color.White.copy(alpha = 0.22f), style = Stroke(width = 3f))

    sim.walls.forEach { wall ->
        drawLine(
            color = Color.White.copy(alpha = 0.28f),
            start = Offset(sx(wall.ax), sy(wall.ay)),
            end = Offset(sx(wall.bx), sy(wall.by)),
            strokeWidth = sr(0.018f),
        )
    }

    sim.bumpers.forEachIndexed { index, bumper ->
        val lit = index in sim.collected
        val glow = if (sim.time - bumper.hitAt < 0.18f) 1f else if (lit) 0.55f else 0f
        val color = if (lit) Color(0xFF86EFAC) else Color(0xFF67E8F9)
        val c = Offset(sx(bumper.x), sy(bumper.y))
        val r = sr(bumper.r)
        if (glow > 0f) {
            drawCircle(color.copy(alpha = 0.28f * glow + 0.12f), radius = r * 1.35f, center = c)
        }
        drawCircle(
            brush = Brush.radialGradient(
                colors = listOf(
                    Color.White.copy(alpha = 0.95f),
                    color,
                    color.copy(alpha = 0.7f),
                    Color(0xFF0B3B4A),
                ),
                center = Offset(c.x - r * 0.28f, c.y - r * 0.32f),
                radius = r * 1.2f,
            ),
            radius = r,
            center = c,
        )
        drawCircle(
            Color.White.copy(alpha = 0.55f),
            radius = r * 0.2f,
            center = Offset(c.x - r * 0.28f, c.y - r * 0.32f),
        )
        val layout = textMeasurer.measure(
            text = bumper.word.term,
            style = TextStyle(
                color = Color.White,
                fontSize = (if (bumper.word.term.length > 8) 9 else 11).sp,
                fontWeight = FontWeight.SemiBold,
                textAlign = TextAlign.Center,
            ),
            constraints = Constraints(maxWidth = (r * 1.7f).toInt().coerceAtLeast(24)),
        )
        drawText(
            layout,
            topLeft = Offset(c.x - layout.size.width / 2f, c.y - layout.size.height / 2f),
        )
    }

    drawFlipper(sim.left, true, ::sx, ::sy, ::sr)
    drawFlipper(sim.right, false, ::sx, ::sy, ::sr)

    val ball = Offset(sx(sim.ballX), sy(sim.ballY))
    val br = sr(sim.ballR)
    drawCircle(Color.White.copy(alpha = 0.22f), radius = br * 1.35f, center = ball)
    drawCircle(
        brush = Brush.radialGradient(
            colors = listOf(Color.White, Color(0xFFBAE6FD), Color(0xFF0369A1)),
            center = Offset(ball.x - br * 0.3f, ball.y - br * 0.32f),
            radius = br * 1.2f,
        ),
        radius = br,
        center = ball,
    )
    drawCircle(
        Color.White.copy(alpha = 0.7f),
        radius = br * 0.22f,
        center = Offset(ball.x - br * 0.28f, ball.y - br * 0.3f),
    )
}

private fun DrawScope.drawFlipper(
    flipper: Flipper,
    left: Boolean,
    sx: (Float) -> Float,
    sy: (Float) -> Float,
    sr: (Float) -> Float,
) {
    val tipX = flipper.pivotX + cos(flipper.angle) * flipper.length
    val tipY = flipper.pivotY + sin(flipper.angle) * flipper.length
    drawLine(
        brush = Brush.linearGradient(
            colors = listOf(Color.White, if (left) Color(0xFF7DD3FC) else Color(0xFF6EE7B7)),
        ),
        start = Offset(sx(flipper.pivotX), sy(flipper.pivotY)),
        end = Offset(sx(tipX), sy(tipY)),
        strokeWidth = sr(0.04f),
    )
    drawCircle(Color.White.copy(alpha = 0.85f), radius = sr(0.022f), center = Offset(sx(flipper.pivotX), sy(flipper.pivotY)))
}

private class PinballSim(words: List<VocabWord>) {
    val bumpers = listOf(
        Bumper(0.32f, 0.22f, 0.078f, words[0]),
        Bumper(0.68f, 0.22f, 0.078f, words[1]),
        Bumper(0.50f, 0.38f, 0.082f, words[2]),
        Bumper(0.22f, 0.50f, 0.072f, words.getOrElse(3) { words[0] }),
        Bumper(0.78f, 0.50f, 0.072f, words.getOrElse(4) { words[1] }),
    )
    val walls = listOf(
        Wall(0.06f, 0.06f, 0.94f, 0.06f),
        Wall(0.06f, 0.06f, 0.06f, 0.78f),
        Wall(0.94f, 0.06f, 0.94f, 0.78f),
        Wall(0.06f, 0.78f, 0.30f, 0.92f),
        Wall(0.94f, 0.78f, 0.70f, 0.92f),
    )
    val left = Flipper(0.30f, 0.915f, 0.19f, rest = 0.32f, active = -0.72f)
    val right = Flipper(0.70f, 0.915f, 0.19f, rest = (PI.toFloat() - 0.32f), active = (PI.toFloat() + 0.72f))

    var ballX = 0.50f
    var ballY = 0.16f
    var vx = 0f
    var vy = 0f
    val ballR = 0.026f
    var leftPressed = false
    var rightPressed = false
    var score = 0
    var ballsLeft = 3
    val collected = linkedSetOf<Int>()
    var lastHit: VocabWord? = null
    var phase = PinballPhase.Ready
    var time = 0f
    private var still = 0f
    private val rng = Random(words.hashCode())

    fun launch() {
        if (phase != PinballPhase.Ready) return
        phase = PinballPhase.Playing
        ballX = 0.50f
        ballY = 0.14f
        vx = rng.nextFloat() * 0.9f - 0.45f
        vy = 0.55f
    }

    fun step(dt: Float): PinballEvents {
        val events = PinballEvents()
        if (phase != PinballPhase.Playing) return events
        time += dt
        stepFlipper(left, leftPressed, dt)
        stepFlipper(right, rightPressed, dt)
        val n = 6
        val h = dt / n
        repeat(n) {
            vy += 2.35f * h
            ballX += vx * h
            ballY += vy * h
            walls.forEach { collideCapsule(it.ax, it.ay, it.bx, it.by, 0.012f, 0.68f, 0f) }
            collideFlipper(left, true)
            collideFlipper(right, false)
            bumpers.forEachIndexed { index, bumper ->
                if (time - bumper.hitAt < 0.16f) return@forEachIndexed
                if (collideCircle(bumper.x, bumper.y, bumper.r, 1.18f, 1.15f)) {
                    bumper.hitAt = time
                    score += 120
                    events.bumper = true
                    lastHit = bumper.word
                    if (collected.add(index)) {
                        score += 400
                        events.newWord = true
                    }
                }
            }
        }
        val speed = hypot(vx, vy)
        if (speed > 4.4f) {
            vx *= 4.4f / speed
            vy *= 4.4f / speed
        }
        if (speed < 0.12f) {
            still += dt
            if (still > 1.1f) {
                vx += if (ballX < 0.5f) 0.4f else -0.4f
                vy -= 0.6f
                still = 0f
            }
        } else {
            still = 0f
        }
        if (collected.size >= PINBALL_TARGET) {
            phase = PinballPhase.Won
            return events
        }
        if (ballY > 1.05f) {
            events.drain = true
            ballsLeft--
            if (ballsLeft <= 0) {
                phase = PinballPhase.Lost
            } else {
                phase = PinballPhase.Ready
                ballX = 0.50f
                ballY = 0.16f
                vx = 0f
                vy = 0f
            }
        }
        return events
    }

    private fun stepFlipper(flipper: Flipper, pressed: Boolean, dt: Float) {
        val target = if (pressed) flipper.active else flipper.rest
        val maxStep = (if (pressed) 16f else 11f) * dt
        val diff = (target - flipper.angle).coerceIn(-maxStep, maxStep)
        flipper.omega = if (dt > 1e-5f) diff / dt else 0f
        flipper.angle += diff
    }

    private fun collideFlipper(flipper: Flipper, isLeft: Boolean) {
        val tipX = flipper.pivotX + cos(flipper.angle) * flipper.length
        val tipY = flipper.pivotY + sin(flipper.angle) * flipper.length
        val swinging = if (isLeft) flipper.omega < -0.4f else flipper.omega > 0.4f
        val kick = if (swinging) abs(flipper.omega) * 0.2f else 0f
        collideCapsule(flipper.pivotX, flipper.pivotY, tipX, tipY, 0.02f, 0.28f, kick)
    }

    private fun collideCircle(cx: Float, cy: Float, cr: Float, restitution: Float, extra: Float): Boolean {
        val dx = ballX - cx
        val dy = ballY - cy
        val dist = hypot(dx, dy)
        val min = ballR + cr
        if (dist >= min || dist < 1e-5f) return false
        val nx = dx / dist
        val ny = dy / dist
        val overlap = min - dist
        ballX += nx * overlap
        ballY += ny * overlap
        val vn = vx * nx + vy * ny
        if (vn < 0f) {
            vx -= (1f + restitution) * vn * nx
            vy -= (1f + restitution) * vn * ny
            vx += nx * extra
            vy += ny * extra
            return true
        }
        return false
    }

    private fun collideCapsule(
        ax: Float,
        ay: Float,
        bx: Float,
        by: Float,
        capR: Float,
        restitution: Float,
        extra: Float,
    ) {
        val abx = bx - ax
        val aby = by - ay
        val ab2 = abx * abx + aby * aby
        val t = if (ab2 < 1e-8f) 0f else ((ballX - ax) * abx + (ballY - ay) * aby) / ab2
        val tt = t.coerceIn(0f, 1f)
        val cx = ax + abx * tt
        val cy = ay + aby * tt
        collideCircle(cx, cy, capR, restitution, extra)
    }
}

private class Bumper(val x: Float, val y: Float, val r: Float, val word: VocabWord) {
    var hitAt: Float = -10f
}

private class Flipper(
    val pivotX: Float,
    val pivotY: Float,
    val length: Float,
    val rest: Float,
    val active: Float,
) {
    var angle: Float = rest
    var omega: Float = 0f
}

private data class Wall(val ax: Float, val ay: Float, val bx: Float, val by: Float)

private class PinballEvents {
    var bumper: Boolean = false
    var newWord: Boolean = false
    var drain: Boolean = false
}
