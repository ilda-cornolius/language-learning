package com.lingualearn.pro.ui

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.speech.tts.TextToSpeech
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Circle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Diamond
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.LocalFireDepartment
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.automirrored.filled.Login
import androidx.compose.material.icons.automirrored.filled.Logout
import androidx.compose.material.icons.automirrored.filled.VolumeUp
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.Text
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.firebase.auth.FirebaseAuth
import com.lingualearn.pro.R
import com.lingualearn.pro.data.CloudWordRepository
import com.lingualearn.pro.data.GoogleSignInHelper
import com.lingualearn.pro.data.DailyReminderScheduler
import com.lingualearn.pro.data.LanguageCourse
import com.lingualearn.pro.data.PreferencesStore
import com.lingualearn.pro.data.ProgressState
import com.lingualearn.pro.data.ProgressStore
import com.lingualearn.pro.data.SampleContent
import com.lingualearn.pro.ui.components.AeroBackground
import com.lingualearn.pro.ui.components.Badge
import com.lingualearn.pro.ui.components.BodyText
import com.lingualearn.pro.ui.components.GlassCard
import com.lingualearn.pro.ui.components.LocalDarkMode
import com.lingualearn.pro.ui.components.SectionLabel
import com.lingualearn.pro.ui.components.selectionSpotlight
import com.lingualearn.pro.ui.screens.AssistantScreen
import com.lingualearn.pro.ui.screens.ChallengesScreen
import com.lingualearn.pro.ui.screens.ConversationScreen
import com.lingualearn.pro.ui.screens.CourseDashboardScreen
import com.lingualearn.pro.ui.screens.LessonsScreen
import com.lingualearn.pro.ui.screens.DailyGrammarLessonScreen
import com.lingualearn.pro.ui.screens.DictationNotebookScreen
import com.lingualearn.pro.ui.screens.FlashcardBrowseScreen
import com.lingualearn.pro.ui.screens.FlashcardOcrScreen
import com.lingualearn.pro.ui.screens.FlashcardStudyScreen
import com.lingualearn.pro.ui.screens.FlashcardsHubScreen
import com.lingualearn.pro.ui.screens.GoogleToolsScreen
import com.lingualearn.pro.ui.screens.GrammarDrillsScreen
import com.lingualearn.pro.ui.screens.InstagramScreen
import com.lingualearn.pro.ui.screens.LanguageOnboardingScreen
import com.lingualearn.pro.ui.screens.GrammarLessonScreen
import com.lingualearn.pro.ui.screens.GrammarSprintScreen
import com.lingualearn.pro.ui.screens.ListeningScreen
import com.lingualearn.pro.ui.screens.MemoryMatchScreen
import com.lingualearn.pro.ui.screens.PracticeHubScreen
import com.lingualearn.pro.ui.screens.PreferencesScreen
import com.lingualearn.pro.ui.screens.ProfileScreen
import com.lingualearn.pro.ui.screens.PronunciationLabScreen
import com.lingualearn.pro.ui.screens.QuickReviewScreen
import com.lingualearn.pro.ui.screens.SpeedRoundScreen
import com.lingualearn.pro.ui.screens.TitleAuthScreen
import com.lingualearn.pro.ui.screens.VocabularyScreen
import com.lingualearn.pro.ui.screens.VocabPinballScreen
import com.lingualearn.pro.ui.screens.WritingScreen
import com.lingualearn.pro.ui.theme.GlassTileStrong
import com.lingualearn.pro.ui.theme.TextMuted
import com.lingualearn.pro.ui.theme.TextPrimary
import com.lingualearn.pro.ui.theme.TextSecondary
import com.lingualearn.pro.ui.theme.VistaAccent
import com.lingualearn.pro.ui.theme.VistaGreen
import com.lingualearn.pro.ui.theme.VistaTeal
import com.lingualearn.pro.ui.widgets.CalendarWidget
import com.lingualearn.pro.ui.widgets.ClockWidget
import com.lingualearn.pro.ui.widgets.DailyProgressWidget
import com.lingualearn.pro.ui.widgets.DestinationWidget
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.util.Locale
import java.util.concurrent.TimeUnit

@Composable
fun LinguaLearnApp() {
    val context = LocalContext.current
    val progressStore = remember(context) { ProgressStore(context.applicationContext) }
    val preferencesStore = remember(context) { PreferencesStore(context.applicationContext) }
    var signedIn by remember { mutableStateOf(CloudWordRepository.isSignedIn) }
    var sessionUid by remember { mutableStateOf(CloudWordRepository.uid) }
    var cloudDirty by remember { mutableStateOf(0) }
    var progressBound by remember { mutableStateOf(false) }

    DisposableEffect(Unit) {
        val firebaseAuth = FirebaseAuth.getInstance()
        val listener = FirebaseAuth.AuthStateListener { auth ->
            signedIn = auth.currentUser != null
            sessionUid = auth.currentUser?.uid
            if (auth.currentUser == null) progressBound = false
        }
        firebaseAuth.addAuthStateListener(listener)
        onDispose { firebaseAuth.removeAuthStateListener(listener) }
    }

    DisposableEffect(progressStore) {
        progressStore.onChanged = { cloudDirty += 1 }
        onDispose { progressStore.onChanged = null }
    }

    LaunchedEffect(sessionUid) {
        val userId = sessionUid ?: return@LaunchedEffect
        progressStore.bindUser(userId)
        runCatching { CloudWordRepository.loadProgress() }
            .onSuccess { snap -> progressStore.replaceState(snap.state, snap.awardIds) }
        progressBound = true
    }

    LaunchedEffect(cloudDirty, signedIn, progressBound) {
        if (!signedIn || !progressBound || cloudDirty == 0) return@LaunchedEffect
        delay(800)
        val (state, awards) = progressStore.snapshot()
        runCatching { CloudWordRepository.saveProgress(state, awards) }
    }

    val stage = when {
        !signedIn -> AppStage.Title
        !preferencesStore.onboardingComplete -> AppStage.Onboarding
        else -> AppStage.App
    }

    Box(Modifier.fillMaxSize().background(Color.Black)) {
        AnimatedContent(
            targetState = stage,
            modifier = Modifier.fillMaxSize(),
            transitionSpec = {
                fadeIn(animationSpec = tween(1000, easing = FastOutSlowInEasing)) togetherWith
                    fadeOut(animationSpec = tween(800, easing = FastOutSlowInEasing))
            },
            label = "lumina-stage",
        ) { current ->
            when (current) {
                AppStage.Title -> TitleAuthScreen(onSignedIn = { signedIn = true })
                AppStage.Onboarding -> LanguageOnboardingScreen(
                    onFinished = { ids -> preferencesStore.completeOnboarding(ids) },
                )
                AppStage.App -> LinguaLearnSignedInShell(
                    progressStore = progressStore,
                    preferencesStore = preferencesStore,
                )
            }
        }
    }
}

private enum class AppStage { Title, Onboarding, App }

@Composable
private fun LinguaLearnSignedInShell(
    progressStore: ProgressStore,
    preferencesStore: PreferencesStore,
) {
    val context = LocalContext.current
    val progress = progressStore.state
    var current by rememberSaveable { mutableStateOf(Destination.forCourse(preferencesStore.activeLanguageId)) }
    var activeLanguageId by rememberSaveable { mutableStateOf(preferencesStore.activeLanguageId) }
    val activeCourse = SampleContent.courseById(activeLanguageId)
    val drawerState = rememberDrawerState(DrawerValue.Closed)
    val scope = rememberCoroutineScope()
    var lessonTts by remember { mutableStateOf<TextToSpeech?>(null) }
    val localeTag = SampleContent.practicePack(activeCourse.id).localeTag
    val googleSignInClient = remember(context) {
        GoogleSignIn.getClient(context, GoogleSignInHelper.options(context))
    }
    val logInLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.StartActivityForResult(),
    ) { result ->
        val idToken = runCatching { GoogleSignInHelper.idTokenFromResult(result.data) }
            .getOrNull() ?: return@rememberLauncherForActivityResult
        scope.launch {
            runCatching { CloudWordRepository.signInWithGoogle(idToken) }
        }
    }

    fun logIn() {
        googleSignInClient.signOut().addOnCompleteListener {
            logInLauncher.launch(googleSignInClient.signInIntent)
        }
    }

    fun logOut() {
        googleSignInClient.signOut().addOnCompleteListener {
            CloudWordRepository.signOut()
        }
    }

    LaunchedEffect(Unit) {
        if (preferencesStore.dailyReminders) {
            DailyReminderScheduler.schedule(context)
        }
    }

    DisposableEffect(context, localeTag, preferencesStore.voiceSpeed) {
        lateinit var engine: TextToSpeech
        engine = TextToSpeech(context) {
            if (it == TextToSpeech.SUCCESS) {
                engine.language = Locale.forLanguageTag(localeTag)
                engine.setSpeechRate(preferencesStore.speechRate())
                lessonTts = engine
            }
        }
        onDispose {
            engine.stop()
            engine.shutdown()
        }
    }

    fun navigate(destination: Destination) {
        Destination.courseIdFor(destination)?.let { courseId ->
            activeLanguageId = courseId
            preferencesStore.updateActiveLanguageId(courseId)
        }
        current = destination
    }

    fun playLessonAudio() {
        val lesson = SampleContent.dailyGrammarLesson(activeCourse.id)
        val example = lesson.examples.firstOrNull()?.phrase.orEmpty()
        val spoken = buildString {
            append(lesson.concept)
            if (example.isNotBlank()) {
                append(". ")
                append(example)
            }
        }
        lessonTts?.setSpeechRate(preferencesStore.speechRate())
        lessonTts?.speak(spoken, TextToSpeech.QUEUE_FLUSH, null, "lesson-audio")
    }

    val screenTitle = Destination.titleFor(current, activeCourse.name)
    val darkMode = preferencesStore.darkMode

    CompositionLocalProvider(LocalDarkMode provides darkMode) {
    AeroBackground {
        BoxWithConstraints(Modifier.fillMaxSize()) {
            // Adaptive width classes for phones, foldables (Galaxy Fold), and tablets.
            // Compact: cover display / phone. Medium: unfolded Fold. Expanded: tablet / wide Fold landscape.
            val widthClass = when {
                maxWidth < 600.dp -> AppWidthClass.Compact
                maxWidth < 840.dp -> AppWidthClass.Medium
                else -> AppWidthClass.Expanded
            }

            when (widthClass) {
                AppWidthClass.Expanded -> {
                    AdaptiveWideShell(
                        screenTitle = screenTitle,
                        current = current,
                        activeLanguageId = activeLanguageId,
                        activeCourse = activeCourse,
                        progress = progress,
                        progressStore = progressStore,
                        preferencesStore = preferencesStore,
                        showWidgetsColumn = true,
                        sidebarWidth = 250.dp,
                        onNavigate = { navigate(it) },
                        onLessonSettings = { current = Destination.Preferences },
                        onLessonAudio = { playLessonAudio() },
                        onLogIn = ::logIn,
                        onLogOut = ::logOut,
                        onSelectLanguage = { course ->
                            activeLanguageId = course.id
                            preferencesStore.updateActiveLanguageId(course.id)
                            navigate(Destination.forCourse(course.id))
                        },
                    )
                }
                AppWidthClass.Medium -> {
                    // Unfolded foldables: keep a persistent sidebar, put widgets inline.
                    AdaptiveWideShell(
                        screenTitle = screenTitle,
                        current = current,
                        activeLanguageId = activeLanguageId,
                        activeCourse = activeCourse,
                        progress = progress,
                        progressStore = progressStore,
                        preferencesStore = preferencesStore,
                        showWidgetsColumn = false,
                        sidebarWidth = 220.dp,
                        onNavigate = { navigate(it) },
                        onLessonSettings = { current = Destination.Preferences },
                        onLessonAudio = { playLessonAudio() },
                        onLogIn = ::logIn,
                        onLogOut = ::logOut,
                        onSelectLanguage = { course ->
                            activeLanguageId = course.id
                            preferencesStore.updateActiveLanguageId(course.id)
                            navigate(Destination.forCourse(course.id))
                        },
                    )
                }
                AppWidthClass.Compact -> {
                    ModalNavigationDrawer(
                        drawerState = drawerState,
                        drawerContent = {
                            ModalDrawerSheet(
                                drawerContainerColor = if (darkMode) Color(0xF2080C14) else Color(0xE60F3D5C),
                                drawerContentColor = TextPrimary,
                            ) {
                                Sidebar(
                                    current = current,
                                    activeLanguageId = activeLanguageId,
                                    progressStore = progressStore,
                                    preferencesStore = preferencesStore,
                                    onSelect = {
                                        navigate(it)
                                        scope.launch { drawerState.close() }
                                    },
                                    onSelectLanguage = { course ->
                                        activeLanguageId = course.id
                                        preferencesStore.updateActiveLanguageId(course.id)
                                        navigate(Destination.forCourse(course.id))
                                        scope.launch { drawerState.close() }
                                    },
                                    onLogIn = {
                                        scope.launch { drawerState.close() }
                                        logIn()
                                    },
                                    onLogOut = {
                                        scope.launch { drawerState.close() }
                                        logOut()
                                    },
                                    modifier = Modifier
                                        .fillMaxHeight()
                                        .verticalScroll(rememberScrollState()),
                                )
                            }
                        },
                    ) {
                        Column(Modifier.safeDrawingPadding()) {
                            TitleBar(
                                title = screenTitle,
                                onMenuClick = { scope.launch { drawerState.open() } },
                                showLessonActions = current == Destination.Lesson,
                                onLessonSettings = { current = Destination.Preferences },
                                onLessonAudio = { playLessonAudio() },
                            )
                            if (Destination.showsLanguageToolbar(current)) {
                                LanguageToolbar(current, progress) { navigate(it) }
                            }
                            ContentArea(
                                current = current,
                                activeCourse = activeCourse,
                                progressStore = progressStore,
                                preferencesStore = preferencesStore,
                                onNavigate = { navigate(it) },
                                showWidgets = true,
                                onLogIn = ::logIn,
                                onLogOut = ::logOut,
                                modifier = Modifier.weight(1f),
                            )
                            StatusBar()
                        }
                    }
                }
            }
        }
    }
    }
}

private enum class AppWidthClass { Compact, Medium, Expanded }

@Composable
private fun AdaptiveWideShell(
    screenTitle: String,
    current: Destination,
    activeLanguageId: String,
    activeCourse: LanguageCourse,
    progress: ProgressState,
    progressStore: ProgressStore,
    preferencesStore: PreferencesStore,
    showWidgetsColumn: Boolean,
    sidebarWidth: Dp,
    onNavigate: (Destination) -> Unit,
    onLessonSettings: () -> Unit,
    onLessonAudio: () -> Unit,
    onLogIn: () -> Unit,
    onLogOut: () -> Unit,
    onSelectLanguage: (LanguageCourse) -> Unit,
) {
    Column(Modifier.safeDrawingPadding()) {
        TitleBar(
            title = screenTitle,
            onMenuClick = null,
            showLessonActions = current == Destination.Lesson,
            onLessonSettings = onLessonSettings,
            onLessonAudio = onLessonAudio,
        )
        Row(Modifier.weight(1f)) {
            Sidebar(
                current = current,
                activeLanguageId = activeLanguageId,
                progressStore = progressStore,
                preferencesStore = preferencesStore,
                onSelect = onNavigate,
                onSelectLanguage = onSelectLanguage,
                onLogIn = onLogIn,
                onLogOut = onLogOut,
                modifier = Modifier
                    .width(sidebarWidth)
                    .fillMaxHeight()
                    .background(Color(0x1A000000))
                    .verticalScroll(rememberScrollState()),
            )
            Column(Modifier.weight(1f)) {
                if (Destination.showsLanguageToolbar(current)) {
                    LanguageToolbar(current, progress, onNavigate)
                }
                ContentArea(
                    current = current,
                    activeCourse = activeCourse,
                    progressStore = progressStore,
                    preferencesStore = preferencesStore,
                    onNavigate = onNavigate,
                    showWidgets = !showWidgetsColumn,
                    onLogIn = onLogIn,
                    onLogOut = onLogOut,
                    modifier = Modifier.weight(1f),
                )
            }
            if (showWidgetsColumn) {
                WidgetsPanel(
                    current = current,
                    progress = progress,
                    modifier = Modifier
                        .width(230.dp)
                        .fillMaxHeight()
                        .padding(12.dp),
                )
            }
        }
        StatusBar()
    }
}

@Composable
private fun TitleBar(
    title: String,
    onMenuClick: (() -> Unit)?,
    showLessonActions: Boolean = false,
    onLessonSettings: () -> Unit = {},
    onLessonAudio: () -> Unit = {},
) {
    Row(
        Modifier
            .fillMaxWidth()
            .background(Color(0x33000000))
            .padding(horizontal = 8.dp, vertical = 6.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        if (onMenuClick != null) {
            IconButton(onClick = onMenuClick, modifier = Modifier.size(32.dp)) {
                Icon(Icons.Filled.Menu, contentDescription = "Open menu", tint = TextPrimary)
            }
            Spacer(Modifier.width(4.dp))
        } else {
            Image(
                painter = painterResource(R.drawable.ic_lumina_icon),
                contentDescription = "Lumina",
                modifier = Modifier
                    .padding(horizontal = 6.dp)
                    .size(22.dp),
                contentScale = ContentScale.Fit,
            )
        }
        Text(
            text = "Lumina",
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.Medium,
            color = TextPrimary,
        )
        Text(
            text = "  ·  ",
            style = MaterialTheme.typography.bodyMedium,
            color = TextMuted,
        )
        Text(
            text = title,
            style = MaterialTheme.typography.bodyMedium,
            color = TextSecondary,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            modifier = Modifier.weight(1f),
        )
        if (showLessonActions) {
            IconButton(onClick = onLessonAudio, modifier = Modifier.size(32.dp)) {
                Icon(
                    Icons.AutoMirrored.Filled.VolumeUp,
                    contentDescription = "Play lesson audio",
                    tint = TextPrimary,
                    modifier = Modifier.size(18.dp),
                )
            }
            IconButton(onClick = onLessonSettings, modifier = Modifier.size(32.dp)) {
                Icon(
                    Icons.Filled.Settings,
                    contentDescription = "Lesson settings",
                    tint = TextPrimary,
                    modifier = Modifier.size(18.dp),
                )
            }
        }
    }
}

@Composable
private fun Sidebar(
    current: Destination,
    activeLanguageId: String,
    progressStore: ProgressStore,
    preferencesStore: PreferencesStore,
    onSelect: (Destination) -> Unit,
    onSelectLanguage: (LanguageCourse) -> Unit,
    onLogIn: () -> Unit,
    onLogOut: () -> Unit,
    modifier: Modifier = Modifier,
) {
    var addingLanguage by remember { mutableStateOf(false) }
    val visibleCourses = SampleContent.visibleCourses(preferencesStore.selectedLanguageIds)
    val availableExtras = SampleContent.allCourses.filter {
        it.id !in preferencesStore.selectedLanguageIds
    }

    Column(
        modifier.padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(18.dp),
    ) {
        Box(
            Modifier
                .fillMaxWidth()
                .padding(bottom = 4.dp),
            contentAlignment = Alignment.Center,
        ) {
            Image(
                painter = painterResource(R.drawable.ic_lumina_icon),
                contentDescription = "Lumina",
                modifier = Modifier.size(56.dp),
                contentScale = ContentScale.Fit,
            )
        }

        val homeDestination = Destination.forCourse(activeLanguageId)
        val onHome = current == Destination.Spanish ||
            current == Destination.French ||
            current == Destination.Japanese ||
            current == Destination.Dashboard
        SidebarRow(
            label = "Home",
            selected = onHome,
            onClick = { onSelect(homeDestination) },
            leading = {
                Icon(
                    Icons.Filled.Home,
                    contentDescription = null,
                    modifier = Modifier.size(18.dp),
                )
            },
        )

        Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
            SectionLabel("My Languages")
            visibleCourses.forEach { course ->
                val pct = progressStore.courseProgress(course.id)
                SidebarRow(
                    label = course.name,
                    selected = course.id == activeLanguageId,
                    onClick = { onSelectLanguage(course) },
                    leading = { Text(course.flag, style = MaterialTheme.typography.bodyMedium) },
                    trailing = {
                        Badge("$pct%", course.accent.copy(alpha = 0.85f))
                    },
                )
            }
            SidebarRow(
                label = if (addingLanguage) "Close" else "Add language",
                selected = addingLanguage,
                onClick = { addingLanguage = !addingLanguage },
                leading = {
                    Icon(
                        if (addingLanguage) Icons.Filled.Close else Icons.Filled.Add,
                        contentDescription = null,
                        modifier = Modifier.size(18.dp),
                    )
                },
            )
            if (addingLanguage) {
                if (availableExtras.isEmpty()) {
                    BodyText("All languages are already added.", color = TextMuted)
                } else {
                    availableExtras.forEach { course ->
                        SidebarRow(
                            label = course.name,
                            selected = false,
                            onClick = {
                                preferencesStore.addLanguage(course.id)
                                onSelectLanguage(course)
                                addingLanguage = false
                            },
                            leading = { Text(course.flag, style = MaterialTheme.typography.bodyMedium) },
                        )
                    }
                }
                if (visibleCourses.size > 1) {
                    BodyText("Remove language", color = TextMuted)
                    visibleCourses.forEach { course ->
                        SidebarRow(
                            label = "Remove ${course.name}",
                            selected = false,
                            onClick = {
                                preferencesStore.removeLanguage(course.id)
                            },
                            leading = { Text(course.flag, style = MaterialTheme.typography.bodyMedium) },
                        )
                    }
                }
            }
        }

        SidebarGroup("Activities", Destination.activities, current, onSelect)
        SidebarGroup("Social Learning", Destination.social, onSelect = onSelect, current = current)
        SidebarGroup("Settings", Destination.settings, current, onSelect)
        Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
            SectionLabel("Account")
            SidebarRow(
                label = "Log in",
                selected = false,
                onClick = onLogIn,
                leading = {
                    Icon(
                        Icons.AutoMirrored.Filled.Login,
                        contentDescription = null,
                        modifier = Modifier.size(18.dp),
                    )
                },
            )
            SidebarRow(
                label = "Log out",
                selected = false,
                onClick = onLogOut,
                leading = {
                    Icon(
                        Icons.AutoMirrored.Filled.Logout,
                        contentDescription = null,
                        modifier = Modifier.size(18.dp),
                    )
                },
            )
        }
    }
}

@Composable
private fun SidebarGroup(
    label: String,
    items: List<Destination>,
    current: Destination,
    onSelect: (Destination) -> Unit,
) {
    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
        SectionLabel(label)
        items.forEach { destination ->
            SidebarRow(
                label = destination.shortLabel,
                selected = current == destination,
                onClick = { onSelect(destination) },
                leading = {
                    destination.icon?.let {
                        Icon(it, contentDescription = null, modifier = Modifier.size(18.dp))
                    }
                },
            )
        }
    }
}

@Composable
private fun SidebarRow(
    label: String,
    selected: Boolean,
    onClick: () -> Unit,
    leading: @Composable () -> Unit,
    trailing: (@Composable () -> Unit)? = null,
) {
    GlassCard(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(8.dp),
        color = if (selected) GlassTileStrong else Color.Transparent,
        onClick = onClick,
        selected = selected,
    ) {
        Row(
            Modifier.padding(horizontal = 10.dp, vertical = 9.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Box(Modifier.size(22.dp), contentAlignment = Alignment.Center) { leading() }
            Text(
                text = label,
                style = MaterialTheme.typography.bodyMedium,
                color = if (selected) TextPrimary else TextSecondary,
                modifier = Modifier
                    .weight(1f)
                    .padding(start = 8.dp),
            )
            trailing?.invoke()
        }
    }
}

@Composable
private fun LanguageToolbar(
    current: Destination,
    progress: ProgressState,
    onSelect: (Destination) -> Unit,
) {
    Row(
        Modifier
            .fillMaxWidth()
            .horizontalScroll(rememberScrollState())
            .padding(start = 12.dp, end = 12.dp, top = 24.dp, bottom = 12.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Destination.toolbar.forEach { destination ->
            ToolbarChip(
                label = destination.shortLabel,
                selected = current == destination ||
                    (destination == Destination.Challenges && Destination.isQuestDestination(current)),
                onClick = { onSelect(destination) },
                icon = destination.icon,
            )
        }
        Spacer(Modifier.width(4.dp))
        val onHome = current == Destination.Spanish ||
            current == Destination.French ||
            current == Destination.Japanese ||
            current == Destination.Dashboard
        if (!onHome) {
            StatChip(Icons.Filled.LocalFireDepartment, "${progress.currentStreak} day streak", VistaAccent)
            StatChip(Icons.Filled.Diamond, "${progress.totalXp} XP · Lv ${progress.level}", VistaTeal)
        }
    }
}

@Composable
private fun ToolbarChip(
    label: String,
    selected: Boolean,
    onClick: () -> Unit,
    icon: androidx.compose.ui.graphics.vector.ImageVector?,
) {
    val interactionSource = remember { MutableInteractionSource() }
    val content = @Composable {
        Row(
            Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            icon?.let {
                Icon(it, contentDescription = null, modifier = Modifier.size(16.dp), tint = Color.White)
                Spacer(Modifier.width(6.dp))
            }
            Text(
                text = label,
                style = MaterialTheme.typography.bodyMedium,
                color = Color.White,
            )
        }
    }

    if (selected) {
        GlassCard(
            shape = RoundedCornerShape(8.dp),
            color = if (LocalDarkMode.current) {
                Color(0x3322D3EE)
            } else {
                VistaAccent.copy(alpha = 0.42f)
            },
            onClick = onClick,
            selected = true,
            content = content,
        )
    } else {
        Row(
            Modifier
                .clip(RoundedCornerShape(8.dp))
                .clickable(
                    interactionSource = interactionSource,
                    indication = null,
                    onClick = onClick,
                )
                .selectionSpotlight(selected = false, interactionSource = interactionSource)
                .padding(horizontal = 4.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            content()
        }
    }
}

@Composable
private fun StatChip(icon: androidx.compose.ui.graphics.vector.ImageVector, label: String, tint: Color) {
    GlassCard(shape = CircleShape, color = GlassTileStrong) {
        Row(
            Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Icon(icon, contentDescription = null, tint = tint, modifier = Modifier.size(14.dp))
            Text(
                text = label,
                style = MaterialTheme.typography.bodySmall,
                color = TextPrimary,
                modifier = Modifier.padding(start = 6.dp),
            )
        }
    }
}

@Composable
private fun ContentArea(
    current: Destination,
    activeCourse: LanguageCourse,
    progressStore: ProgressStore,
    preferencesStore: PreferencesStore,
    onNavigate: (Destination) -> Unit,
    showWidgets: Boolean,
    onLogIn: () -> Unit,
    onLogOut: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val today = ProgressStore.todayKey()
    val courseId = activeCourse.id

    fun awardDaily(activity: String, xp: Int, lessonCompleted: Boolean = false) {
        val awarded = progressStore.awardOnce(
            awardId = "activity:$today:$courseId:$activity",
            xp = xp,
            lessonCompleted = lessonCompleted,
            courseId = courseId,
        )
        if (awarded || lessonCompleted) progressStore.markDayActive()
    }

    val isCourseDashboard = current == Destination.Spanish ||
        current == Destination.French ||
        current == Destination.Japanese ||
        current == Destination.Dashboard
    val showDestinationPhoto = showWidgets && showsDestinationPhoto(current)

    Column(modifier.fillMaxWidth()) {
        if (Destination.isArcadePlay(current)) {
            VocabPinballScreen(
                course = activeCourse,
                progressStore = progressStore,
                onBack = { onNavigate(Destination.Challenges) },
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
                    .padding(start = 12.dp, end = 12.dp, top = 12.dp, bottom = 8.dp),
            )
        } else {
        Column(
            Modifier
                .weight(1f)
                .fillMaxWidth()
                .verticalScroll(rememberScrollState())
                .padding(start = 16.dp, end = 16.dp, top = 24.dp, bottom = 16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
        ) {
        when (current) {
            Destination.Lesson -> GrammarLessonScreen(
                course = activeCourse,
                onComplete = {
                    progressStore.awardOnce(
                        "lesson:$today:$courseId:grammar",
                        100,
                        lessonCompleted = true,
                        courseId = courseId,
                    )
                    onNavigate(Destination.Practice)
                },
            )
            Destination.Spanish, Destination.French, Destination.Japanese, Destination.Dashboard -> {
                CourseDashboardScreen(
                    course = activeCourse,
                    progress = progressStore.state,
                    displayName = CloudWordRepository.userDisplayName ?: preferencesStore.displayName,
                    photoUrl = CloudWordRepository.userPhotoUrl,
                    onNavigate = onNavigate,
                )
            }
            Destination.Vocabulary -> VocabularyScreen(
                course = activeCourse,
                preferencesStore = preferencesStore,
                onComplete = { awardDaily("vocabulary", 30) },
            )
            Destination.Lessons -> LessonsScreen(
                course = activeCourse,
                onOpenGrammarLesson = { onNavigate(Destination.Lesson) },
                onOpenDailyLesson = { onNavigate(Destination.DailyLesson) },
            )
            Destination.Conversation -> ConversationScreen(
                course = activeCourse,
                onComplete = { awardDaily("conversation", 50) },
            )
            Destination.Listening -> ListeningScreen(
                course = activeCourse,
                preferencesStore = preferencesStore,
                onComplete = { awardDaily("listening", 40) },
            )
            Destination.Writing -> WritingScreen(
                course = activeCourse,
                preferencesStore = preferencesStore,
                onComplete = { awardDaily("writing", 60) },
            )
            Destination.Assistant -> AssistantScreen(
                course = activeCourse,
                onFirstReply = { awardDaily("assistant", 40) },
            )
            Destination.Instagram -> InstagramScreen(activeCourse)
            Destination.Google -> GoogleToolsScreen(
                course = activeCourse,
                onVoiceComplete = { awardDaily("google-voice", 20) },
            )
            Destination.Profile -> ProfileScreen(
                progress = progressStore.state,
                progressStore = progressStore,
                preferencesStore = preferencesStore,
                courseName = activeCourse.name,
                onLogIn = onLogIn,
                onLogOut = onLogOut,
            )
            Destination.Preferences -> PreferencesScreen(preferencesStore)
            Destination.DailyLesson -> DailyGrammarLessonScreen(
                course = activeCourse,
                onStart = { onNavigate(Destination.Lesson) },
            )
            Destination.Practice -> PracticeHubScreen(
                onQuickReview = { onNavigate(Destination.QuickReview) },
                onGrammarDrills = { onNavigate(Destination.GrammarDrills) },
                onPronunciationLab = { onNavigate(Destination.PronunciationLab) },
                onDictationNotebook = { onNavigate(Destination.DictationNotebook) },
                onFlashcards = { onNavigate(Destination.Flashcards) },
            )
            Destination.Flashcards -> FlashcardsHubScreen(
                course = activeCourse,
                onStudy = { onNavigate(Destination.FlashcardStudy) },
                onBrowse = { onNavigate(Destination.FlashcardBrowse) },
                onOcr = { onNavigate(Destination.FlashcardOcr) },
                onBack = { onNavigate(Destination.Practice) },
            )
            Destination.FlashcardStudy -> FlashcardStudyScreen(
                course = activeCourse,
                preferencesStore = preferencesStore,
                onBack = { onNavigate(Destination.Flashcards) },
                onSessionComplete = { reviews ->
                    if (reviews >= 5) awardDaily("flashcards", 40)
                },
            )
            Destination.FlashcardBrowse -> FlashcardBrowseScreen(
                course = activeCourse,
                onBack = { onNavigate(Destination.Flashcards) },
            )
            Destination.FlashcardOcr -> FlashcardOcrScreen(
                course = activeCourse,
                preferencesStore = preferencesStore,
                onBack = { onNavigate(Destination.Flashcards) },
                onComplete = { awardDaily("flashcard-ocr", 25) },
            )
            Destination.QuickReview -> QuickReviewScreen(
                course = activeCourse,
                onBack = { onNavigate(Destination.Practice) },
                onComplete = { score, total ->
                    val prefix = "activity:$today:$courseId:quick-review"
                    if (progressStore.awardOnce(prefix, 50, courseId = courseId)) {
                        progressStore.markDayActive()
                    }
                    if (score == total) progressStore.awardOnce("$prefix:perfect", 25, courseId = courseId)
                },
            )
            Destination.GrammarDrills -> GrammarDrillsScreen(
                course = activeCourse,
                onBack = { onNavigate(Destination.Practice) },
                onComplete = { score, total ->
                    val prefix = "activity:$today:$courseId:grammar-drills"
                    if (progressStore.awardOnce(prefix, 50, courseId = courseId)) {
                        progressStore.markDayActive()
                    }
                    if (score == total) progressStore.awardOnce("$prefix:perfect", 25, courseId = courseId)
                    onNavigate(Destination.Practice)
                },
            )
            Destination.PronunciationLab -> PronunciationLabScreen(
                course = activeCourse,
                preferencesStore = preferencesStore,
                onBack = { onNavigate(Destination.Practice) },
                onComplete = { awardDaily("pronunciation", 40) },
            )
            Destination.DictationNotebook -> DictationNotebookScreen(
                course = activeCourse,
                preferencesStore = preferencesStore,
                onBack = { onNavigate(Destination.Practice) },
                onReview = { onNavigate(Destination.QuickReview) },
                onWordSaved = { awardDaily("dictation", 25) },
            )
            Destination.Challenges -> ChallengesScreen(
                progressStore = progressStore,
                onSpeedRound = { onNavigate(Destination.SpeedRound) },
                onMemoryMatch = { onNavigate(Destination.MemoryMatch) },
                onGrammarSprint = { onNavigate(Destination.GrammarSprint) },
                onPinball = { onNavigate(Destination.VocabPinball) },
            )
            Destination.SpeedRound -> SpeedRoundScreen(
                activeCourse,
                progressStore,
                onBack = { onNavigate(Destination.Challenges) },
            )
            Destination.MemoryMatch -> MemoryMatchScreen(
                activeCourse,
                progressStore,
                onBack = { onNavigate(Destination.Challenges) },
            )
            Destination.GrammarSprint -> GrammarSprintScreen(
                activeCourse,
                progressStore,
                onBack = { onNavigate(Destination.Challenges) },
            )
            Destination.VocabPinball -> Unit
        }

        if (showWidgets) {
            val hideCalendarClock = current == Destination.Practice ||
                current == Destination.QuickReview ||
                current == Destination.GrammarDrills ||
                current == Destination.PronunciationLab ||
                current == Destination.DictationNotebook ||
                current == Destination.Flashcards ||
                current == Destination.FlashcardStudy ||
                current == Destination.FlashcardBrowse ||
                current == Destination.FlashcardOcr ||
                current == Destination.Challenges ||
                current == Destination.SpeedRound ||
                current == Destination.MemoryMatch ||
                current == Destination.GrammarSprint ||
                current == Destination.VocabPinball ||
                current == Destination.DailyLesson ||
                current == Destination.Assistant ||
                current == Destination.Lesson ||
                current == Destination.Listening ||
                current == Destination.Writing ||
                current == Destination.Preferences ||
                current == Destination.Vocabulary ||
                current == Destination.Lessons ||
                current == Destination.Conversation ||
                current == Destination.Instagram ||
                current == Destination.Google ||
                current == Destination.Profile ||
                isCourseDashboard
            val hideDailyProgress = current == Destination.Practice ||
                current == Destination.QuickReview ||
                current == Destination.GrammarDrills ||
                current == Destination.PronunciationLab ||
                current == Destination.DictationNotebook ||
                current == Destination.Flashcards ||
                current == Destination.FlashcardStudy ||
                current == Destination.FlashcardBrowse ||
                current == Destination.FlashcardOcr ||
                current == Destination.Challenges ||
                current == Destination.SpeedRound ||
                current == Destination.MemoryMatch ||
                current == Destination.GrammarSprint ||
                current == Destination.VocabPinball ||
                current == Destination.DailyLesson ||
                current == Destination.Assistant ||
                current == Destination.Lesson ||
                current == Destination.Listening ||
                current == Destination.Writing ||
                current == Destination.Preferences ||
                current == Destination.Vocabulary ||
                current == Destination.Lessons ||
                current == Destination.Conversation ||
                current == Destination.Instagram ||
                current == Destination.Google ||
                current == Destination.Profile ||
                isCourseDashboard
            if (!hideCalendarClock) {
                Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    CalendarWidget(Modifier.weight(1f))
                    ClockWidget(Modifier.weight(1f))
                }
            }
            if (!hideDailyProgress) {
                DailyProgressWidget(progressStore.state)
            }
            if (showDestinationPhoto && !isCourseDashboard) {
                DestinationWidget()
            }
        }
        }
        }

        if (showDestinationPhoto && isCourseDashboard) {
            DestinationWidget(
                Modifier.padding(start = 16.dp, end = 16.dp, bottom = 16.dp),
            )
        }
    }
}

@Composable
private fun WidgetsPanel(current: Destination, progress: ProgressState, modifier: Modifier = Modifier) {
    val isCourseDashboard = current == Destination.Spanish ||
        current == Destination.French ||
        current == Destination.Japanese ||
        current == Destination.Dashboard
    val hideCalendarClock = current == Destination.Practice ||
        current == Destination.QuickReview ||
        current == Destination.GrammarDrills ||
        current == Destination.PronunciationLab ||
        current == Destination.DictationNotebook ||
        current == Destination.Flashcards ||
        current == Destination.FlashcardStudy ||
        current == Destination.FlashcardBrowse ||
        current == Destination.FlashcardOcr ||
        current == Destination.Challenges ||
        current == Destination.SpeedRound ||
        current == Destination.MemoryMatch ||
        current == Destination.GrammarSprint ||
        current == Destination.VocabPinball ||
        current == Destination.DailyLesson ||
        current == Destination.Assistant ||
        current == Destination.Lesson ||
        current == Destination.Listening ||
        current == Destination.Writing ||
        current == Destination.Preferences ||
        current == Destination.Vocabulary ||
        current == Destination.Lessons ||
        current == Destination.Conversation ||
        current == Destination.Instagram ||
        current == Destination.Google ||
        current == Destination.Profile ||
        isCourseDashboard
    val hideDailyProgress = current == Destination.Practice ||
        current == Destination.QuickReview ||
        current == Destination.GrammarDrills ||
        current == Destination.PronunciationLab ||
        current == Destination.DictationNotebook ||
        current == Destination.Flashcards ||
        current == Destination.FlashcardStudy ||
        current == Destination.FlashcardBrowse ||
        current == Destination.FlashcardOcr ||
        current == Destination.Challenges ||
        current == Destination.SpeedRound ||
        current == Destination.MemoryMatch ||
        current == Destination.GrammarSprint ||
        current == Destination.VocabPinball ||
        current == Destination.DailyLesson ||
        current == Destination.Assistant ||
        current == Destination.Lesson ||
        current == Destination.Listening ||
        current == Destination.Writing ||
        current == Destination.Preferences ||
        current == Destination.Vocabulary ||
        current == Destination.Lessons ||
        current == Destination.Conversation ||
        current == Destination.Instagram ||
        current == Destination.Google ||
        current == Destination.Profile ||
        isCourseDashboard
    Column(modifier) {
        Column(
            Modifier
                .weight(1f)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            if (!hideCalendarClock) {
                CalendarWidget()
                ClockWidget()
            }
            if (!hideDailyProgress) {
                DailyProgressWidget(progress)
            }
        }
        if (showsDestinationPhoto(current)) {
            DestinationWidget(Modifier.padding(top = 12.dp))
        }
    }
}

private fun showsDestinationPhoto(current: Destination): Boolean =
    current != Destination.Challenges &&
        current != Destination.SpeedRound &&
        current != Destination.MemoryMatch &&
        current != Destination.GrammarSprint &&
        current != Destination.VocabPinball &&
        current != Destination.Flashcards &&
        current != Destination.FlashcardStudy &&
        current != Destination.FlashcardBrowse &&
        current != Destination.FlashcardOcr

@Composable
private fun StatusBar() {
    val context = LocalContext.current
    var online by remember { mutableStateOf(isNetworkOnline(context)) }
    var lastSyncLabel by remember { mutableStateOf(formatLastSync(CloudWordRepository.lastSyncAtMs)) }

    LaunchedEffect(Unit) {
        while (true) {
            online = isNetworkOnline(context)
            lastSyncLabel = formatLastSync(CloudWordRepository.lastSyncAtMs)
            delay(5_000)
        }
    }

    Row(
        Modifier
            .fillMaxWidth()
            .background(Color(0x4D000000))
            .padding(horizontal = 12.dp, vertical = 4.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(
                Icons.Filled.Circle,
                contentDescription = null,
                tint = if (online) VistaGreen else Color(0xFFB63A3A),
                modifier = Modifier.size(8.dp),
            )
            StatusText(if (online) "Online" else "Offline", Modifier.padding(start = 6.dp))
            StatusText("Version 2.3.1", Modifier.padding(start = 14.dp))
        }
        StatusText(lastSyncLabel)
    }
}

private fun isNetworkOnline(context: Context): Boolean {
    val cm = context.getSystemService(Context.CONNECTIVITY_SERVICE) as? ConnectivityManager
        ?: return false
    val network = cm.activeNetwork ?: return false
    val caps = cm.getNetworkCapabilities(network) ?: return false
    return caps.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
}

private fun formatLastSync(atMs: Long?): String {
    if (atMs == null) return "Not synced yet"
    val minutes = TimeUnit.MILLISECONDS.toMinutes(System.currentTimeMillis() - atMs)
    return when {
        minutes < 1 -> "Last sync: just now"
        minutes < 60 -> "Last sync: $minutes min ago"
        else -> "Last sync: ${minutes / 60}h ago"
    }
}

@Composable
private fun StatusText(text: String, modifier: Modifier = Modifier) {
    Text(
        text = text,
        style = MaterialTheme.typography.bodySmall,
        color = TextMuted,
        modifier = modifier,
    )
}