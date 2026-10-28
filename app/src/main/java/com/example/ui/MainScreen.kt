package com.example.ui

import android.widget.Toast
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ContentPaste
import androidx.compose.material.icons.filled.EditCalendar
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Keyboard
import androidx.compose.material.icons.filled.Palette
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.KeyboardPreferences
import com.example.data.model.MessageTemplate
import com.example.data.model.ReminderStatus
import com.example.data.model.ScheduledReminderHistory
import com.example.ui.screens.CustomizeScreen
import com.example.ui.screens.HomeScreen
import com.example.ui.screens.MessagePlannerScreen
import com.example.ui.screens.MessageReadyDialog
import com.example.ui.screens.SettingsScreen
import com.example.ui.screens.ShortcutsScreen
import com.example.ui.screens.TestLabScreen
import com.example.ui.screens.ThemesScreen
import com.example.ui.theme.ElectricBlue

enum class NavTab(
    val title: String,
    val icon: ImageVector
) {
    HOME("Home", Icons.Default.Home),
    PLANNER("Planner", Icons.Default.EditCalendar),
    CUSTOMIZE("Customize", Icons.Default.Tune),
    THEMES("Themes", Icons.Default.Palette),
    TEST_LAB("Test Lab", Icons.Default.Keyboard),
    SETTINGS("Settings", Icons.Default.Settings)
}

@Composable
fun MainScreen(
    preferences: KeyboardPreferences,
    initialTab: NavTab = NavTab.HOME,
    initialMessageReadyTemplateId: String? = null
) {
    val context = LocalContext.current
    var currentTab by remember { mutableStateOf(initialTab) }

    val settings by preferences.settings.collectAsState()
    val shortcuts by preferences.shortcuts.collectAsState()
    val snippets by preferences.snippets.collectAsState()
    val templates by preferences.templates.collectAsState()
    val customWords by preferences.customWords.collectAsState()

    var activeMessageReadyTemplate by remember { mutableStateOf<MessageTemplate?>(null) }

    LaunchedEffect(initialMessageReadyTemplateId, templates) {
        if (!initialMessageReadyTemplateId.isNullOrBlank()) {
            val found = templates.find { it.id == initialMessageReadyTemplateId }
            if (found != null) {
                activeMessageReadyTemplate = found
            }
        }
    }

    Scaffold(
        bottomBar = {
            NavigationBar(
                modifier = Modifier
                    .fillMaxWidth()
                    .navigationBarsPadding(),
                containerColor = MaterialTheme.colorScheme.surface.copy(alpha = 0.95f),
                tonalElevation = 8.dp
            ) {
                NavTab.entries.forEach { tab ->
                    val isSelected = currentTab == tab
                    NavigationBarItem(
                        selected = isSelected,
                        onClick = { currentTab = tab },
                        icon = {
                            Icon(
                                imageVector = tab.icon,
                                contentDescription = tab.title,
                                modifier = Modifier.size(20.dp)
                            )
                        },
                        label = {
                            Text(
                                text = tab.title,
                                fontSize = 10.sp,
                                maxLines = 1
                            )
                        },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = ElectricBlue,
                            selectedTextColor = ElectricBlue,
                            unselectedIconColor = MaterialTheme.colorScheme.onSurfaceVariant,
                            unselectedTextColor = MaterialTheme.colorScheme.onSurfaceVariant,
                            indicatorColor = ElectricBlue.copy(alpha = 0.15f)
                        )
                    )
                }
            }
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            AnimatedContent(
                targetState = currentTab,
                transitionSpec = {
                    fadeIn(animationSpec = tween(200)) togetherWith fadeOut(animationSpec = tween(150))
                },
                label = "navContent"
            ) { tab ->
                when (tab) {
                    NavTab.HOME -> HomeScreen(
                        preferences = preferences,
                        settings = settings,
                        onNavigateToCustomize = { currentTab = NavTab.CUSTOMIZE },
                        onNavigateToThemes = { currentTab = NavTab.THEMES },
                        onNavigateToTestLab = { currentTab = NavTab.TEST_LAB },
                        onNavigateToPlanner = { currentTab = NavTab.PLANNER },
                        onNavigateToSettings = { currentTab = NavTab.SETTINGS }
                    )
                    NavTab.PLANNER -> MessagePlannerScreen(
                        preferences = preferences,
                        onOpenMessageReadyForTemplate = { activeMessageReadyTemplate = it }
                    )
                    NavTab.CUSTOMIZE -> CustomizeScreen(
                        preferences = preferences,
                        settings = settings
                    )
                    NavTab.THEMES -> ThemesScreen(
                        preferences = preferences,
                        settings = settings
                    )
                    NavTab.TEST_LAB -> TestLabScreen(
                        preferences = preferences,
                        settings = settings,
                        shortcuts = shortcuts,
                        snippets = snippets,
                        templates = templates,
                        customWords = customWords,
                        onOpenSettings = { currentTab = NavTab.CUSTOMIZE },
                        onOpenMessagePlanner = { currentTab = NavTab.PLANNER }
                    )
                    NavTab.SETTINGS -> SettingsScreen(
                        preferences = preferences,
                        settings = settings
                    )
                }
            }
        }
    }

    // Message Ready Dialog when notification is opened
    if (activeMessageReadyTemplate != null) {
        val tmpl = activeMessageReadyTemplate!!
        MessageReadyDialog(
            template = tmpl,
            onMarkAsSent = { text ->
                preferences.addHistoryEntry(
                    ScheduledReminderHistory(
                        id = System.currentTimeMillis().toString(),
                        templateId = tmpl.id,
                        templateTitle = tmpl.title,
                        messageContent = text,
                        recipient = tmpl.recipientName,
                        platform = tmpl.platform,
                        scheduledTime = System.currentTimeMillis(),
                        status = ReminderStatus.SENT_CONFIRMED,
                        confirmedSentTime = System.currentTimeMillis(),
                        actionLog = "User confirmed message sent"
                    )
                )
                Toast.makeText(context, "Marked as Sent (User Confirmed)", Toast.LENGTH_SHORT).show()
            },
            onSnooze = {
                preferences.snoozeReminder(context, tmpl.id, System.currentTimeMillis() + 10 * 60 * 1000)
                Toast.makeText(context, "Snoozed for 10 minutes", Toast.LENGTH_SHORT).show()
            },
            onSkip = {
                preferences.addHistoryEntry(
                    ScheduledReminderHistory(
                        id = System.currentTimeMillis().toString(),
                        templateId = tmpl.id,
                        templateTitle = tmpl.title,
                        messageContent = tmpl.content,
                        recipient = tmpl.recipientName,
                        platform = tmpl.platform,
                        scheduledTime = System.currentTimeMillis(),
                        status = ReminderStatus.SKIPPED,
                        actionLog = "Occurrence skipped"
                    )
                )
                Toast.makeText(context, "Occurrence skipped", Toast.LENGTH_SHORT).show()
            },
            onDismiss = { activeMessageReadyTemplate = null }
        )
    }
}
