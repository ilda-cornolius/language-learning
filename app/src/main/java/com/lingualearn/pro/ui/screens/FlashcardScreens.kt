package com.lingualearn.pro.ui.screens

import android.Manifest
import android.content.pm.PackageManager
import android.graphics.ImageDecoder
import android.net.Uri
import android.os.Build
import android.provider.MediaStore
import android.speech.tts.TextToSpeech
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.VolumeUp
import androidx.compose.material.icons.filled.PhotoCamera
import androidx.compose.material.icons.filled.PhotoLibrary
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.text.TextRecognition
import com.google.mlkit.vision.text.latin.TextRecognizerOptions
import com.lingualearn.pro.data.AnkiDeckIo
import com.lingualearn.pro.data.DictionaryLookupRepository
import com.lingualearn.pro.data.Flashcard
import com.lingualearn.pro.data.FlashcardRating
import com.lingualearn.pro.data.FlashcardRepository
import com.lingualearn.pro.data.LanguageCourse
import com.lingualearn.pro.data.PreferencesStore
import com.lingualearn.pro.data.SampleContent
import com.lingualearn.pro.data.SoundEffects
import com.lingualearn.pro.ui.components.AeroButton
import com.lingualearn.pro.ui.components.Badge
import com.lingualearn.pro.ui.components.BodyText
import com.lingualearn.pro.ui.components.CardTitle
import com.lingualearn.pro.ui.components.GlassCard
import com.lingualearn.pro.ui.components.GlassTextField
import com.lingualearn.pro.ui.components.GlassTile
import com.lingualearn.pro.ui.theme.GlassTileStrong
import com.lingualearn.pro.ui.theme.TextMuted
import com.lingualearn.pro.ui.theme.TextPrimary
import com.lingualearn.pro.ui.theme.VistaGreen
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import java.io.File
import java.util.Locale

@Composable
fun FlashcardsHubScreen(
    course: LanguageCourse,
    onStudy: () -> Unit,
    onBrowse: () -> Unit,
    onImport: () -> Unit = {},
    onExport: () -> Unit = {},
    onOcr: () -> Unit,
    onBack: () -> Unit,
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    var dueCount by remember { mutableIntStateOf(0) }
    var totalCount by remember { mutableIntStateOf(0) }
    var message by remember { mutableStateOf("") }
    var deckId by remember { mutableStateOf(0L) }

    suspend fun refresh() {
        val deck = FlashcardRepository.ensureDefaultDeck(context, course.id)
        deckId = deck.id
        dueCount = FlashcardRepository.countDue(context, course.id)
        totalCount = FlashcardRepository.countCards(context, course.id)
    }

    LaunchedEffect(course.id) { refresh() }

    val importLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.OpenDocument(),
    ) { uri ->
        if (uri == null) return@rememberLauncherForActivityResult
        scope.launch {
            val notes = AnkiDeckIo.readUri(context, uri)
            if (notes.isEmpty()) {
                message = "No cards found. Use Anki File → Export → Notes in Plain Text (CSV/TSV)."
                return@launch
            }
            val deck = FlashcardRepository.ensureDefaultDeck(context, course.id, seed = false)
            val added = FlashcardRepository.addCards(
                context,
                deck.id,
                notes.map { Triple(it.front, it.back, it.extra) },
            )
            refresh()
            message = "Imported $added of ${notes.size} notes into ${deck.name}."
        }
    }

    val exportLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.CreateDocument("text/tab-separated-values"),
    ) { uri ->
        if (uri == null) return@rememberLauncherForActivityResult
        scope.launch {
            val deck = FlashcardRepository.ensureDefaultDeck(context, course.id, seed = false)
            val cards = FlashcardRepository.cardsInDeck(context, deck.id)
            val ok = AnkiDeckIo.writeUri(context, uri, AnkiDeckIo.exportTsv(cards))
            message = if (ok) {
                "Exported ${cards.size} cards for Anki import (tab-separated)."
            } else {
                "Export failed."
            }
        }
    }

    AeroButton(
        "Back to Practice",
        onClick = onBack,
        color = Color(0xFF526777),
        leadingIcon = { Icon(Icons.AutoMirrored.Filled.ArrowBack, null) },
    )

    GlassCard(Modifier.fillMaxWidth()) {
        Column(Modifier.padding(20.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
            Badge("Anki-style SRS", course.accent.copy(alpha = 0.7f))
            CardTitle("Flashcards")
            BodyText("Spaced repetition for ${course.name}. Ratings mirror Anki: Again, Hard, Good, Easy.")
            Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                GlassTile(Modifier.weight(1f), color = GlassTileStrong) {
                    Column(Modifier.padding(14.dp)) {
                        Text("$dueCount", color = course.accent, fontWeight = FontWeight.Bold)
                        BodyText("Due today", color = TextMuted)
                    }
                }
                GlassTile(Modifier.weight(1f), color = GlassTileStrong) {
                    Column(Modifier.padding(14.dp)) {
                        Text("$totalCount", color = TextPrimary, fontWeight = FontWeight.Bold)
                        BodyText("Total cards", color = TextMuted)
                    }
                }
            }
            AeroButton("Study Now", onClick = onStudy, color = course.accent)
            AeroButton("Browse Deck", onClick = onBrowse, color = Color(0xFF2563EB))
            AeroButton(
                "Import Anki CSV",
                onClick = {
                    onImport()
                    importLauncher.launch(arrayOf("text/*", "text/csv", "text/tab-separated-values", "*/*"))
                },
                color = Color(0xFF526777),
            )
            AeroButton(
                "Export for Anki",
                onClick = {
                    onExport()
                    exportLauncher.launch("${course.name.lowercase()}_anki_export.txt")
                },
                color = Color(0xFF526777),
            )
            AeroButton("OCR Capture", onClick = onOcr, color = Color(0xFFFF6B1A))
            AeroButton(
                "Sync from Saved Words",
                onClick = {
                    scope.launch {
                        val added = FlashcardRepository.importFromSavedWords(context, course.id)
                        refresh()
                        message = if (added > 0) {
                            "Added $added cards from saved words & dictionary."
                        } else {
                            "Deck already has those words — nothing new to add."
                        }
                    }
                },
                color = VistaGreen,
            )
            if (message.isNotBlank()) BodyText(message, color = TextMuted)
            BodyText(
                "Tip: In Anki use File → Export → Notes in Plain Text. .apkg zip packages are not imported — use CSV/TSV/.txt.",
                color = TextMuted,
            )
        }
    }
}

@Composable
fun FlashcardStudyScreen(
    course: LanguageCourse,
    preferencesStore: PreferencesStore,
    onBack: () -> Unit,
    onSessionComplete: (reviews: Int) -> Unit = {},
) {
    Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
        AeroButton(
            "Back to Flashcards",
            onClick = onBack,
            color = Color(0xFF526777),
            leadingIcon = { Icon(Icons.AutoMirrored.Filled.ArrowBack, null) },
        )
        FlashcardStudyModule(
            course = course,
            preferencesStore = preferencesStore,
            onEmptyAction = onBack,
            onEmptyActionLabel = "Back",
            onSessionComplete = onSessionComplete,
        )
    }
}

@Composable
fun FlashcardStudyModule(
    course: LanguageCourse,
    preferencesStore: PreferencesStore,
    onEmptyAction: () -> Unit,
    onEmptyActionLabel: String = "Open deck",
    onSessionComplete: (reviews: Int) -> Unit = {},
    modifier: Modifier = Modifier,
    framed: Boolean = true,
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    var queue by remember { mutableStateOf<List<Flashcard>>(emptyList()) }
    var revealed by remember { mutableStateOf(false) }
    var reviews by remember { mutableIntStateOf(0) }
    var loaded by remember { mutableStateOf(false) }
    var tts by remember { mutableStateOf<TextToSpeech?>(null) }
    val localeTag = SampleContent.practicePack(course.id).localeTag

    LaunchedEffect(course.id) {
        FlashcardRepository.ensureDefaultDeck(context, course.id)
        queue = FlashcardRepository.dueCards(context, course.id)
        loaded = true
        revealed = false
        reviews = 0
    }

    DisposableEffect(context, localeTag, preferencesStore.voiceSpeed) {
        lateinit var engine: TextToSpeech
        engine = TextToSpeech(context) {
            if (it == TextToSpeech.SUCCESS) {
                engine.language = Locale.forLanguageTag(localeTag)
                engine.setSpeechRate(preferencesStore.speechRate())
                tts = engine
            }
        }
        onDispose {
            engine.stop()
            engine.shutdown()
        }
    }

    fun rate(rating: FlashcardRating) {
        val card = queue.firstOrNull() ?: return
        scope.launch {
            FlashcardRepository.reviewCard(context, card.id, rating)
            reviews++
            revealed = false
            queue = queue.drop(1)
            if (queue.isEmpty()) {
                onSessionComplete(reviews)
                SoundEffects.playSuccess(context, preferencesStore)
            }
        }
    }

    val body = @Composable {
        Column(
            Modifier
                .fillMaxWidth()
                .padding(if (framed) 20.dp else 2.dp),
            verticalArrangement = Arrangement.spacedBy(if (framed) 16.dp else 10.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            if (framed) {
                Text(
                    text = "Study session",
                    color = TextPrimary,
                    fontWeight = FontWeight.Bold,
                    fontSize = 28.sp,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth(),
                )
            }
            if (!loaded) {
                BodyText(
                    "Loading due cards…",
                    modifier = Modifier.fillMaxWidth(),
                    color = TextMuted,
                )
            } else if (queue.isEmpty()) {
                Badge("Caught up", VistaGreen.copy(alpha = 0.7f))
                Text(
                    text = "You're caught up!",
                    color = TextPrimary,
                    fontWeight = FontWeight.Bold,
                    fontSize = 26.sp,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth(),
                )
                BodyText(
                    "No cards are due right now. Import a deck or sync saved words to add more.",
                    modifier = Modifier.fillMaxWidth(),
                )
                AeroButton(
                    onEmptyActionLabel,
                    onClick = onEmptyAction,
                    color = course.accent,
                    modifier = Modifier.fillMaxWidth(0.7f),
                    contentPadding = PaddingValues(horizontal = 20.dp, vertical = 16.dp),
                    textStyle = MaterialTheme.typography.titleMedium,
                )
            } else {
                val card = queue.first()
                Badge(
                    "${queue.size} due remaining · $reviews reviewed",
                    course.accent.copy(alpha = 0.65f),
                )
                val cardFace = @Composable {
                    Column(
                        Modifier
                            .fillMaxWidth()
                            .padding(
                                horizontal = if (framed) 24.dp else 8.dp,
                                vertical = if (framed) 28.dp else 12.dp,
                            ),
                        verticalArrangement = Arrangement.spacedBy(if (framed) 14.dp else 8.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                    ) {
                        Text(
                            text = "Front",
                            color = TextMuted,
                            fontWeight = FontWeight.SemiBold,
                            fontSize = 18.sp,
                            textAlign = TextAlign.Center,
                            modifier = Modifier.fillMaxWidth(),
                        )
                        Text(
                            text = card.front,
                            color = TextPrimary,
                            fontWeight = FontWeight.Bold,
                            fontSize = if (framed) 36.sp else 30.sp,
                            lineHeight = if (framed) 42.sp else 36.sp,
                            textAlign = TextAlign.Center,
                            modifier = Modifier.fillMaxWidth(),
                        )
                        if (revealed) {
                            Text(
                                text = "Back",
                                color = TextMuted,
                                fontWeight = FontWeight.SemiBold,
                                fontSize = 18.sp,
                                textAlign = TextAlign.Center,
                                modifier = Modifier.fillMaxWidth(),
                            )
                            Text(
                                text = card.back,
                                color = VistaGreen,
                                fontWeight = FontWeight.Bold,
                                fontSize = 34.sp,
                                lineHeight = 40.sp,
                                textAlign = TextAlign.Center,
                                modifier = Modifier.fillMaxWidth(),
                            )
                            if (card.extra.isNotBlank()) {
                                Text(
                                    text = card.extra,
                                    color = TextMuted,
                                    fontSize = 20.sp,
                                    lineHeight = 26.sp,
                                    textAlign = TextAlign.Center,
                                    modifier = Modifier.fillMaxWidth(),
                                )
                            }
                        }
                    }
                }
                if (framed) {
                    GlassTile(Modifier.fillMaxWidth(), color = GlassTileStrong) {
                        cardFace()
                    }
                } else {
                    cardFace()
                }
                val studyButtonPadding = PaddingValues(horizontal = 18.dp, vertical = 16.dp)
                val studyButtonStyle = MaterialTheme.typography.titleMedium
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                ) {
                    AeroButton(
                        "Speak",
                        onClick = {
                            tts?.setSpeechRate(preferencesStore.speechRate())
                            tts?.speak(card.front, TextToSpeech.QUEUE_FLUSH, null, "flash-front")
                        },
                        color = Color(0xFF526777),
                        modifier = Modifier.weight(1f),
                        contentPadding = studyButtonPadding,
                        textStyle = studyButtonStyle,
                        leadingIcon = { Icon(Icons.AutoMirrored.Filled.VolumeUp, null) },
                    )
                    if (!revealed) {
                        AeroButton(
                            "Reveal",
                            onClick = { revealed = true },
                            color = course.accent,
                            modifier = Modifier.weight(1f),
                            contentPadding = studyButtonPadding,
                            textStyle = studyButtonStyle,
                        )
                    }
                }
                if (revealed) {
                    Text(
                        text = "How well did you remember?",
                        color = TextMuted,
                        fontSize = 18.sp,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.fillMaxWidth(),
                    )
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp),
                    ) {
                        AeroButton(
                            "Again",
                            onClick = { rate(FlashcardRating.Again) },
                            color = Color(0xFFB63A3A),
                            modifier = Modifier.weight(1f),
                            contentPadding = studyButtonPadding,
                            textStyle = studyButtonStyle,
                        )
                        AeroButton(
                            "Hard",
                            onClick = { rate(FlashcardRating.Hard) },
                            color = Color(0xFFFF6B1A),
                            modifier = Modifier.weight(1f),
                            contentPadding = studyButtonPadding,
                            textStyle = studyButtonStyle,
                        )
                    }
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp),
                    ) {
                        AeroButton(
                            "Good",
                            onClick = { rate(FlashcardRating.Good) },
                            color = VistaGreen,
                            modifier = Modifier.weight(1f),
                            contentPadding = studyButtonPadding,
                            textStyle = studyButtonStyle,
                        )
                        AeroButton(
                            "Easy",
                            onClick = { rate(FlashcardRating.Easy) },
                            color = Color(0xFF2563EB),
                            modifier = Modifier.weight(1f),
                            contentPadding = studyButtonPadding,
                            textStyle = studyButtonStyle,
                        )
                    }
                }
            }
        }
    }
    if (framed) {
        GlassCard(modifier.fillMaxWidth()) { body() }
    } else {
        Box(modifier.fillMaxWidth()) { body() }
    }
}

@Composable
fun FlashcardBrowseScreen(
    course: LanguageCourse,
    onBack: () -> Unit,
) {
    val context = LocalContext.current
    var cards by remember { mutableStateOf<List<Flashcard>>(emptyList()) }
    var deckName by remember { mutableStateOf("") }

    LaunchedEffect(course.id) {
        val deck = FlashcardRepository.ensureDefaultDeck(context, course.id)
        deckName = deck.name
        cards = FlashcardRepository.cardsInDeck(context, deck.id)
    }

    AeroButton(
        "Back to Flashcards",
        onClick = onBack,
        color = Color(0xFF526777),
        leadingIcon = { Icon(Icons.AutoMirrored.Filled.ArrowBack, null) },
    )

    GlassCard(Modifier.fillMaxWidth()) {
        Column(Modifier.padding(20.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
            CardTitle(deckName.ifBlank { "Deck" })
            BodyText("${cards.size} cards", color = TextMuted)
            if (cards.isEmpty()) {
                BodyText("No cards yet. Import Anki text or sync from saved words.")
            } else {
                cards.forEach { card ->
                    GlassTile(Modifier.fillMaxWidth()) {
                        Column(Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                            Text(card.front, color = course.accent, fontWeight = FontWeight.Bold)
                            BodyText("→ ${card.back}")
                            if (card.extra.isNotBlank()) BodyText(card.extra, color = TextMuted)
                        }
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun FlashcardOcrScreen(
    course: LanguageCourse,
    preferencesStore: PreferencesStore,
    onBack: () -> Unit,
    onComplete: () -> Unit = {},
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val pack = SampleContent.practicePack(course.id)
    var lines by remember { mutableStateOf<List<String>>(emptyList()) }
    var selected by remember { mutableStateOf<Set<String>>(emptySet()) }
    var meaning by remember { mutableStateOf("") }
    var status by remember { mutableStateOf("Pick a photo or take a picture of text to scan.") }
    var busy by remember { mutableStateOf(false) }
    var pendingCameraUri by remember { mutableStateOf<Uri?>(null) }
    var cardsAdded by remember { mutableIntStateOf(0) }

    fun runOcr(uri: Uri) {
        scope.launch {
            busy = true
            status = "Recognizing text…"
            runCatching {
                val bitmap = if (Build.VERSION.SDK_INT >= 28) {
                    val source = ImageDecoder.createSource(context.contentResolver, uri)
                    ImageDecoder.decodeBitmap(source)
                } else {
                    @Suppress("DEPRECATION")
                    MediaStore.Images.Media.getBitmap(context.contentResolver, uri)
                }
                val image = InputImage.fromBitmap(bitmap, 0)
                val recognizer = TextRecognition.getClient(TextRecognizerOptions.DEFAULT_OPTIONS)
                val result = recognizer.process(image).await()
                recognizer.close()
                val found = result.textBlocks
                    .flatMap { block -> block.lines.map { it.text.trim() } }
                    .filter { it.isNotBlank() }
                    .distinct()
                lines = found
                selected = emptySet()
                meaning = ""
                status = if (found.isEmpty()) {
                    "No text found. Try a clearer photo."
                } else {
                    "Select words for the front of your cards, then add a meaning."
                }
            }.onFailure {
                status = "OCR failed: ${it.message ?: "unknown error"}"
            }
            busy = false
        }
    }

    val galleryLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.GetContent(),
    ) { uri -> if (uri != null) runOcr(uri) }

    val takePictureLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.TakePicture(),
    ) { success ->
        val uri = pendingCameraUri
        if (success && uri != null) runOcr(uri)
    }

    val cameraPermission = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission(),
    ) { granted ->
        if (granted) {
            val dir = File(context.cacheDir, "images").also { it.mkdirs() }
            val file = File(dir, "ocr_${System.currentTimeMillis()}.jpg")
            val uri = FileProvider.getUriForFile(context, "${context.packageName}.fileprovider", file)
            pendingCameraUri = uri
            takePictureLauncher.launch(uri)
        } else {
            status = "Camera permission is required to take a photo."
        }
    }

    fun launchCamera() {
        when {
            ContextCompat.checkSelfPermission(context, Manifest.permission.CAMERA) ==
                PackageManager.PERMISSION_GRANTED -> {
                val dir = File(context.cacheDir, "images").also { it.mkdirs() }
                val file = File(dir, "ocr_${System.currentTimeMillis()}.jpg")
                val uri = FileProvider.getUriForFile(context, "${context.packageName}.fileprovider", file)
                pendingCameraUri = uri
                takePictureLauncher.launch(uri)
            }
            else -> cameraPermission.launch(Manifest.permission.CAMERA)
        }
    }

    AeroButton(
        "Back to Flashcards",
        onClick = onBack,
        color = Color(0xFF526777),
        leadingIcon = { Icon(Icons.AutoMirrored.Filled.ArrowBack, null) },
    )

    GlassCard(Modifier.fillMaxWidth()) {
        Column(Modifier.padding(20.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
            CardTitle("OCR flashcards")
            BodyText("Scan text from an image, pick fronts, look up meanings, and add to your deck.")
            Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                AeroButton(
                    "Gallery",
                    onClick = { galleryLauncher.launch("image/*") },
                    color = course.accent,
                    leadingIcon = { Icon(Icons.Filled.PhotoLibrary, null) },
                )
                AeroButton(
                    "Camera",
                    onClick = { launchCamera() },
                    color = Color(0xFFFF6B1A),
                    leadingIcon = { Icon(Icons.Filled.PhotoCamera, null) },
                )
            }
            BodyText(if (busy) "Working…" else status, color = TextMuted)
            if (lines.isNotEmpty()) {
                BodyText("Recognized lines — tap to select", color = TextMuted)
                FlowRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                ) {
                    lines.forEach { line ->
                        val isSelected = line in selected
                        GlassTile(
                            color = if (isSelected) course.accent.copy(alpha = 0.55f) else GlassTileStrong,
                            shape = RoundedCornerShape(10.dp),
                            onClick = {
                                selected = if (isSelected) selected - line else selected + line
                            },
                        ) {
                            Text(
                                line,
                                color = TextPrimary,
                                modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                            )
                        }
                    }
                }
                Text("Meaning / translation", color = TextPrimary, fontWeight = FontWeight.Bold)
                GlassTextField(
                    meaning,
                    { meaning = it },
                    "Enter meaning for selected fronts",
                    Modifier.fillMaxWidth(),
                )
                Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                    AeroButton(
                        "Look up",
                        onClick = {
                            val query = selected.firstOrNull() ?: return@AeroButton
                            scope.launch {
                                status = "Looking up “$query”…"
                                val local = SampleContent.dictionaryWords(course.id)
                                    .firstOrNull { it.term.equals(query, ignoreCase = true) }
                                    ?.meaning
                                val result = local ?: if (preferencesStore.offlineMode) {
                                    null
                                } else {
                                    DictionaryLookupRepository.lookup(
                                        query,
                                        pack.localeTag.substringBefore('-'),
                                    )
                                }
                                if (result != null) {
                                    meaning = result
                                    status = "Found: $query — $result"
                                } else {
                                    status = "No dictionary hit — enter the meaning yourself."
                                }
                            }
                        },
                        color = Color(0xFF526777),
                    )
                    AeroButton(
                        "Add to deck",
                        onClick = {
                            if (selected.isEmpty()) {
                                status = "Select at least one front word."
                                return@AeroButton
                            }
                            if (meaning.isBlank()) {
                                status = "Enter a meaning before adding."
                                return@AeroButton
                            }
                            scope.launch {
                                val deck = FlashcardRepository.ensureDefaultDeck(
                                    context,
                                    course.id,
                                    seed = false,
                                )
                                val triples = selected.map { Triple(it, meaning.trim(), "") }
                                val added = FlashcardRepository.addCards(context, deck.id, triples)
                                cardsAdded += added
                                if (added > 0) {
                                    onComplete()
                                    SoundEffects.playSuccess(context, preferencesStore)
                                }
                                status = "Added $added card(s). Total this session: $cardsAdded."
                                selected = emptySet()
                                meaning = ""
                            }
                        },
                        color = VistaGreen,
                    )
                }
            }
        }
    }
}
