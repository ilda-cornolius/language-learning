package com.lingualearn.pro.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
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
import androidx.compose.material.icons.filled.Circle
import androidx.compose.material.icons.filled.Diamond
import androidx.compose.material.icons.filled.Language
import androidx.compose.material.icons.filled.LocalFireDepartment
import androidx.compose.material.icons.filled.Menu
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
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.lingualearn.pro.data.LanguageCourse
import com.lingualearn.pro.data.SampleContent
import com.lingualearn.pro.ui.components.AeroBackground
import com.lingualearn.pro.ui.components.Badge
import com.lingualearn.pro.ui.components.GlassCard
import com.lingualearn.pro.ui.components.SectionLabel
import com.lingualearn.pro.ui.screens.AssistantScreen
import com.lingualearn.pro.ui.screens.ChallengesScreen
import com.lingualearn.pro.ui.screens.ConversationScreen
import com.lingualearn.pro.ui.screens.CourseDashboardScreen
import com.lingualearn.pro.ui.screens.DailyLessonScreen
import com.lingualearn.pro.ui.screens.GoogleToolsScreen
import com.lingualearn.pro.ui.screens.InstagramScreen
import com.lingualearn.pro.ui.screens.LessonScreen
import com.lingualearn.pro.ui.screens.ListeningScreen
import com.lingualearn.pro.ui.screens.PracticeScreen
import com.lingualearn.pro.ui.screens.PreferencesScreen
import com.lingualearn.pro.ui.screens.ProfileScreen
import com.lingualearn.pro.ui.screens.VocabularyScreen
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
import kotlinx.coroutines.launch

@Composable
fun LinguaLearnApp() {
    var current by remember { mutableStateOf(Destination.Lesson) }
    var activeLanguageId by remember { mutableStateOf("spanish") }
    val activeCourse = SampleContent.courseById(activeLanguageId)
    val drawerState = rememberDrawerState(DrawerValue.Closed)
    val scope = rememberCoroutineScope()

    fun navigate(destination: Destination) {
        Destination.courseIdFor(destination)?.let { activeLanguageId = it }
        current = destination
    }

    val screenTitle = Destination.titleFor(current, activeCourse.name)

    AeroBackground {
        BoxWithConstraints(Modifier.fillMaxSize()) {
            // Tablets and unfolded devices get the mockup's three-column desktop
            // layout; phones fall back to a drawer with the widgets inlined.
            val expanded = maxWidth >= 840.dp

            if (expanded) {
                Column(Modifier.safeDrawingPadding()) {
                    TitleBar(
                        title = screenTitle,
                        onMenuClick = null,
                        showLessonActions = current == Destination.Lesson,
                        onLessonSettings = { current = Destination.Preferences },
                    )
                    Row(Modifier.weight(1f)) {
                        Sidebar(
                            current = current,
                            activeLanguageId = activeLanguageId,
                            onSelect = { navigate(it) },
                            modifier = Modifier
                                .width(250.dp)
                                .fillMaxHeight()
                                .background(Color(0x1A000000)),
                        )
                        Column(Modifier.weight(1f)) {
                            LanguageToolbar(current) { navigate(it) }
                            ContentArea(
                                current = current,
                                activeCourse = activeCourse,
                                onNavigate = { navigate(it) },
                                showWidgets = false,
                                modifier = Modifier.weight(1f),
                            )
                        }
                        WidgetsPanel(
                            current = current,
                            modifier = Modifier
                                .width(230.dp)
                                .fillMaxHeight()
                                .verticalScroll(rememberScrollState())
                                .padding(12.dp)
                        )
                    }
                    StatusBar()
                }
            } else {
                ModalNavigationDrawer(
                    drawerState = drawerState,
                    drawerContent = {
                        ModalDrawerSheet(
                            drawerContainerColor = Color(0xE60F3D5C),
                            drawerContentColor = TextPrimary,
                        ) {
                            Sidebar(
                                current = current,
                                activeLanguageId = activeLanguageId,
                                onSelect = {
                                    navigate(it)
                                    scope.launch { drawerState.close() }
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
                        )
                        LanguageToolbar(current) { navigate(it) }
                        ContentArea(
                            current = current,
                            activeCourse = activeCourse,
                            onNavigate = { navigate(it) },
                            showWidgets = true,
                            modifier = Modifier.weight(1f),
                        )
                        StatusBar()
                    }
                }
            }
        }
    }
}

@Composable
private fun TitleBar(
    title: String,
    onMenuClick: (() -> Unit)?,
    showLessonActions: Boolean = false,
    onLessonSettings: () -> Unit = {},
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
            Icon(
                Icons.Filled.Language,
                contentDescription = null,
                tint = TextPrimary,
                modifier = Modifier
                    .padding(horizontal = 6.dp)
                    .size(18.dp),
            )
        }
        Text(
            text = "LinguaLearn Pro",
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
            IconButton(onClick = {}, modifier = Modifier.size(32.dp)) {
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
    onSelect: (Destination) -> Unit,
    modifier: Modifier = Modifier,
) {
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
            Box(
                Modifier
                    .size(56.dp)
                    .background(VistaAccent, CircleShape),
                contentAlignment = Alignment.Center,
            ) {
                Icon(
                    Icons.Filled.Language,
                    contentDescription = null,
                    tint = Color.White,
                    modifier = Modifier.size(28.dp),
                )
            }
        }

        Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
            SectionLabel("My Languages")
            SampleContent.courses.forEach { course ->
                val destination = Destination.forCourse(course.id)
                SidebarRow(
                    label = course.name,
                    selected = course.id == activeLanguageId,
                    onClick = { onSelect(destination) },
                    leading = { Text(course.flag, style = MaterialTheme.typography.bodyMedium) },
                    trailing = {
                        Badge("${course.progress}%", course.accent.copy(alpha = 0.85f))
                    },
                )
            }
        }

        SidebarGroup("Activities", Destination.activities, current, onSelect)
        SidebarGroup("Social Learning", Destination.social, onSelect = onSelect, current = current)
        SidebarGroup("Settings", Destination.settings, current, onSelect)
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
private fun LanguageToolbar(current: Destination, onSelect: (Destination) -> Unit) {
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
                selected = current == destination,
                onClick = { onSelect(destination) },
                icon = destination.icon,
            )
        }
        Spacer(Modifier.width(4.dp))
        StatChip(Icons.Filled.LocalFireDepartment, "5 day streak", VistaAccent)
        StatChip(Icons.Filled.Diamond, "320 points", VistaTeal)
    }
}

@Composable
private fun ToolbarChip(
    label: String,
    selected: Boolean,
    onClick: () -> Unit,
    icon: androidx.compose.ui.graphics.vector.ImageVector?,
) {
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
            color = VistaAccent,
            onClick = onClick,
            content = content,
        )
    } else {
        // Unselected toolbar items are plain text — no frosted glass chip.
        Row(
            Modifier
                .clickable(onClick = onClick)
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
    onNavigate: (Destination) -> Unit,
    showWidgets: Boolean,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier
            .fillMaxWidth()
            .verticalScroll(rememberScrollState())
            .padding(start = 16.dp, end = 16.dp, top = 24.dp, bottom = 16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        when (current) {
            Destination.Lesson -> LessonScreen(onCheck = { onNavigate(Destination.Practice) })
            Destination.Spanish, Destination.French, Destination.Japanese -> {
                CourseDashboardScreen(activeCourse)
            }
            Destination.Vocabulary -> VocabularyScreen(activeCourse)
            Destination.Conversation -> ConversationScreen(activeCourse)
            Destination.Listening -> ListeningScreen(activeCourse)
            Destination.Writing -> WritingScreen(activeCourse)
            Destination.Assistant -> AssistantScreen(activeCourse)
            Destination.Instagram -> InstagramScreen()
            Destination.Google -> GoogleToolsScreen()
            Destination.Profile -> ProfileScreen()
            Destination.Preferences -> PreferencesScreen()
            Destination.DailyLesson -> DailyLessonScreen(onStart = { onNavigate(Destination.Lesson) })
            Destination.Practice -> PracticeScreen()
            Destination.Challenges -> ChallengesScreen()
        }

        if (showWidgets) {
            val isCourseDashboard = current == Destination.Spanish ||
                current == Destination.French ||
                current == Destination.Japanese
            val hideCalendarClock = current == Destination.Practice ||
                current == Destination.Challenges ||
                current == Destination.DailyLesson ||
                current == Destination.Assistant ||
                current == Destination.Lesson ||
                current == Destination.Listening ||
                current == Destination.Writing ||
                current == Destination.Preferences ||
                isCourseDashboard
            val hideDailyProgress = current == Destination.Practice ||
                current == Destination.Challenges ||
                current == Destination.DailyLesson ||
                current == Destination.Assistant ||
                current == Destination.Listening ||
                current == Destination.Writing ||
                current == Destination.Preferences ||
                isCourseDashboard
            if (!hideCalendarClock) {
                Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    CalendarWidget(Modifier.weight(1f))
                    ClockWidget(Modifier.weight(1f))
                }
            }
            if (!hideDailyProgress) {
                DailyProgressWidget()
            }
            if (current != Destination.Challenges) {
                DestinationWidget()
            }
        }
    }
}

@Composable
private fun WidgetsPanel(current: Destination, modifier: Modifier = Modifier) {
    val isCourseDashboard = current == Destination.Spanish ||
        current == Destination.French ||
        current == Destination.Japanese
    val hideCalendarClock = current == Destination.Practice ||
        current == Destination.Challenges ||
        current == Destination.DailyLesson ||
        current == Destination.Assistant ||
        current == Destination.Lesson ||
        current == Destination.Listening ||
        current == Destination.Writing ||
        current == Destination.Preferences ||
        isCourseDashboard
    val hideDailyProgress = current == Destination.Practice ||
        current == Destination.Challenges ||
        current == Destination.DailyLesson ||
        current == Destination.Assistant ||
        current == Destination.Listening ||
        current == Destination.Writing ||
        current == Destination.Preferences ||
        isCourseDashboard
    Column(modifier, verticalArrangement = Arrangement.spacedBy(12.dp)) {
        if (!hideCalendarClock) {
            CalendarWidget()
            ClockWidget()
        }
        if (!hideDailyProgress) {
            DailyProgressWidget()
        }
        if (current != Destination.Challenges) {
            DestinationWidget()
        }
    }
}

@Composable
private fun StatusBar() {
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
                tint = VistaGreen,
                modifier = Modifier.size(8.dp),
            )
            StatusText("Online", Modifier.padding(start = 6.dp))
            StatusText("Version 2.3.1", Modifier.padding(start = 14.dp))
        }
        StatusText("Last sync: 2 min ago")
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