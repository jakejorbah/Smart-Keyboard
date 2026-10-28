package com.example.ui.screens

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Alarm
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.ContentPaste
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.NotificationsActive
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.StarBorder
import androidx.compose.material.icons.filled.Today
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.KeyboardPreferences
import com.example.data.model.MessageTemplate
import com.example.data.model.ReminderStatus
import com.example.data.model.RepeatType
import com.example.data.model.ScheduleRule
import com.example.data.model.ScheduledReminderHistory
import com.example.engine.TemplateEngine
import com.example.ui.theme.ElectricBlue
import com.example.ui.theme.GlassSuccess
import com.example.util.MessageScheduler
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

@Composable
fun MessagePlannerScreen(
    preferences: KeyboardPreferences,
    onOpenMessageReadyForTemplate: ((MessageTemplate) -> Unit)? = null
) {
    val context = LocalContext.current
    val templates by preferences.templates.collectAsState()
    val schedules by preferences.schedules.collectAsState()
    val history by preferences.history.collectAsState()

    var searchQuery by remember { mutableStateOf("") }
    var selectedFilter by remember { mutableStateOf("All") } // All, Today, Upcoming, Repeating, History
    var viewMode by remember { mutableIntStateOf(0) } // 0: Templates & Agenda, 1: History Log

    var editingTemplate by remember { mutableStateOf<MessageTemplate?>(null) }
    var showCreateDialog by remember { mutableStateOf(false) }
    var activeMessageReadyTemplate by remember { mutableStateOf<MessageTemplate?>(null) }

    val currentDateStr = remember {
        SimpleDateFormat("EEEE, MMMM d", Locale.getDefault()).format(Date())
    }
    val currentHour = Calendar.getInstance().get(Calendar.HOUR_OF_DAY)
    val greeting = when (currentHour) {
        in 5..11 -> "Good Morning ☀️"
        in 12..16 -> "Good Afternoon 🌤️"
        in 17..21 -> "Good Evening 🌆"
        else -> "Good Night 🌙"
    }

    val schedulesMap = remember(schedules) {
        schedules.associateBy { it.templateId }
    }

    // Filter templates
    val filteredTemplates = remember(templates, schedulesMap, searchQuery, selectedFilter) {
        var list = templates
        if (searchQuery.isNotBlank()) {
            list = list.filter {
                it.title.contains(searchQuery, ignoreCase = true) ||
                        it.content.contains(searchQuery, ignoreCase = true) ||
                        it.recipientName.contains(searchQuery, ignoreCase = true)
            }
        }
        when (selectedFilter) {
            "Today" -> list.filter {
                val sched = schedulesMap[it.id]
                sched != null && sched.isEnabled && sched.repeatType in listOf(RepeatType.DAILY, RepeatType.WEEKDAYS)
            }
            "Upcoming" -> list.filter {
                val sched = schedulesMap[it.id]
                sched != null && sched.isEnabled && sched.nextTriggerTime > System.currentTimeMillis()
            }
            "Repeating" -> list.filter {
                val sched = schedulesMap[it.id]
                sched != null && sched.isEnabled && sched.repeatType != RepeatType.ONCE
            }
            else -> list
        }
    }

    Scaffold(
        floatingActionButton = {
            FloatingActionButton(
                onClick = { showCreateDialog = true },
                containerColor = ElectricBlue,
                contentColor = Color.White,
                shape = CircleShape
            ) {
                Icon(Icons.Default.Add, contentDescription = "Create Template")
            }
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = 16.dp, vertical = 10.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            // iOS Greeting Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = greeting,
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onBackground
                    )
                    Text(
                        text = currentDateStr,
                        fontSize = 13.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                // Test Reminder 5s button
                Button(
                    onClick = {
                        val testTemplate = templates.firstOrNull() ?: TemplateEngine.StarterTemplates.first()
                        val testRule = ScheduleRule(
                            id = "test_rule",
                            templateId = testTemplate.id,
                            nextTriggerTime = System.currentTimeMillis() + 3000
                        )
                        val resolvedText = TemplateEngine.resolveTemplateVariables(testTemplate.content, testTemplate.recipientName)
                        MessageScheduler.scheduleAlarm(context, testRule, testTemplate, resolvedText)
                        Toast.makeText(context, "Reminder notification will trigger in 3 seconds!", Toast.LENGTH_SHORT).show()
                    },
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = ElectricBlue.copy(alpha = 0.15f)),
                    contentPadding = PaddingValues(horizontal = 10.dp, vertical = 6.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.NotificationsActive,
                        contentDescription = null,
                        tint = ElectricBlue,
                        modifier = Modifier.size(15.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(text = "Test (3s)", fontSize = 11.sp, color = ElectricBlue, fontWeight = FontWeight.Bold)
                }
            }

            // Search Bar
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { searchQuery = it },
                placeholder = { Text("Search templates, messages, or recipients...") },
                leadingIcon = { Icon(Icons.Default.Search, contentDescription = null, tint = MaterialTheme.colorScheme.onSurfaceVariant) },
                trailingIcon = {
                    if (searchQuery.isNotEmpty()) {
                        IconButton(onClick = { searchQuery = "" }) {
                            Icon(Icons.Default.Close, contentDescription = "Clear")
                        }
                    }
                },
                singleLine = true,
                shape = RoundedCornerShape(14.dp),
                modifier = Modifier.fillMaxWidth()
            )

            // Filter Chips Row
            LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                items(listOf("All", "Today", "Upcoming", "Repeating", "History Log")) { filter ->
                    val isSelected = (viewMode == 1 && filter == "History Log") || (viewMode == 0 && selectedFilter == filter)
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(12.dp))
                            .background(
                                if (isSelected) ElectricBlue else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
                            )
                            .clickable {
                                if (filter == "History Log") {
                                    viewMode = 1
                                } else {
                                    viewMode = 0
                                    selectedFilter = filter
                                }
                            }
                            .padding(horizontal = 12.dp, vertical = 6.dp)
                    ) {
                        Text(
                            text = filter,
                            fontSize = 12.sp,
                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                            color = if (isSelected) Color.White else MaterialTheme.colorScheme.onSurface
                        )
                    }
                }
            }

            // Main Content: View 0 (Templates & Agenda) or View 1 (History Log)
            if (viewMode == 0) {
                // Templates List
                if (filteredTemplates.isEmpty()) {
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxWidth(),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "No message templates found.\nTap '+' to create your first scheduled template!",
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            textAlign = androidx.compose.ui.text.style.TextAlign.Center,
                            fontSize = 14.sp
                        )
                    }
                } else {
                    LazyColumn(
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxWidth(),
                        verticalArrangement = Arrangement.spacedBy(10.dp),
                        contentPadding = PaddingValues(bottom = 80.dp)
                    ) {
                        items(filteredTemplates, key = { it.id }) { template ->
                            val schedule = schedulesMap[template.id]

                            TemplateCard(
                                template = template,
                                schedule = schedule,
                                onSendNow = { activeMessageReadyTemplate = template },
                                onEdit = { editingTemplate = template },
                                onDuplicate = { preferences.duplicateTemplate(template.id) },
                                onDelete = { preferences.deleteTemplate(context, template.id) },
                                onToggleSchedule = { isEnabled ->
                                    preferences.toggleScheduleEnabled(context, template.id, isEnabled)
                                },
                                onToggleFavorite = { preferences.toggleFavoriteTemplate(template.id) }
                            )
                        }
                    }
                }
            } else {
                // History Log
                if (history.isEmpty()) {
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxWidth(),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "No message history recorded yet.\nWhen reminders trigger or messages are confirmed sent, they will appear here.",
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            textAlign = androidx.compose.ui.text.style.TextAlign.Center,
                            fontSize = 14.sp
                        )
                    }
                } else {
                    LazyColumn(
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxWidth(),
                        verticalArrangement = Arrangement.spacedBy(8.dp),
                        contentPadding = PaddingValues(bottom = 80.dp)
                    ) {
                        items(history, key = { it.id }) { entry ->
                            HistoryCard(entry = entry)
                        }
                    }
                }
            }
        }
    }

    // Template Creator / Editor Dialog
    if (showCreateDialog) {
        TemplateEditorDialog(
            initialTemplate = null,
            initialSchedule = null,
            onSave = { newTemplate, newSchedule ->
                preferences.addOrUpdateTemplate(newTemplate)
                if (newSchedule != null) {
                    preferences.setScheduleForTemplate(context, newTemplate, newSchedule)
                }
                showCreateDialog = false
            },
            onDismiss = { showCreateDialog = false }
        )
    }

    if (editingTemplate != null) {
        val currTemplate = editingTemplate!!
        TemplateEditorDialog(
            initialTemplate = currTemplate,
            initialSchedule = schedulesMap[currTemplate.id],
            onSave = { updatedTemplate, updatedSchedule ->
                preferences.addOrUpdateTemplate(updatedTemplate)
                if (updatedSchedule != null) {
                    preferences.setScheduleForTemplate(context, updatedTemplate, updatedSchedule)
                }
                editingTemplate = null
            },
            onDismiss = { editingTemplate = null }
        )
    }

    // Message Ready Dialog
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

@Composable
private fun TemplateCard(
    template: MessageTemplate,
    schedule: ScheduleRule?,
    onSendNow: () -> Unit,
    onEdit: () -> Unit,
    onDuplicate: () -> Unit,
    onDelete: () -> Unit,
    onToggleSchedule: (Boolean) -> Unit,
    onToggleFavorite: () -> Unit
) {
    val isScheduled = schedule?.isEnabled == true
    val resolvedPreview = TemplateEngine.resolveTemplateVariables(template.content, template.recipientName)

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.45f))
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            // Header Row: Emoji badge, Title, Platform, Favorite & Switch
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.weight(1f)
                ) {
                    Box(
                        modifier = Modifier
                            .size(32.dp)
                            .clip(CircleShape)
                            .background(ElectricBlue.copy(alpha = 0.15f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(text = template.platform.iconEmoji, fontSize = 16.sp)
                    }

                    Column {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Text(
                                text = template.title,
                                fontSize = 15.sp,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            if (template.isFavorite) {
                                Icon(
                                    imageVector = Icons.Default.Star,
                                    contentDescription = "Favorite",
                                    tint = Color(0xFFFBBF24),
                                    modifier = Modifier.size(14.dp)
                                )
                            }
                        }
                        Text(
                            text = "${template.category} • ${template.platform.displayName}",
                            fontSize = 11.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    if (schedule != null) {
                        Switch(
                            checked = isScheduled,
                            onCheckedChange = onToggleSchedule,
                            colors = SwitchDefaults.colors(checkedThumbColor = Color.White, checkedTrackColor = ElectricBlue)
                        )
                    }
                    IconButton(onClick = onToggleFavorite, modifier = Modifier.size(28.dp)) {
                        Icon(
                            imageVector = if (template.isFavorite) Icons.Default.Star else Icons.Default.StarBorder,
                            contentDescription = null,
                            tint = if (template.isFavorite) Color(0xFFFBBF24) else MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }
            }

            // Message Preview
            Text(
                text = resolvedPreview,
                fontSize = 13.sp,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.9f),
                maxLines = 2,
                overflow = TextOverflow.Ellipsis,
                lineHeight = 17.sp
            )

            // Recipient & Schedule Timing Badges
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                if (template.recipientName.isNotBlank()) {
                    Text(
                        text = "To: ${template.recipientName}",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Medium,
                        color = ElectricBlue
                    )
                } else {
                    Spacer(modifier = Modifier.width(1.dp))
                }

                if (schedule != null && isScheduled) {
                    val timeStr = String.format("%02d:%02d", schedule.hour, schedule.minute)
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(6.dp))
                            .background(GlassSuccess.copy(alpha = 0.15f))
                            .padding(horizontal = 6.dp, vertical = 2.dp)
                    ) {
                        Text(
                            text = "${schedule.repeatType.label} at $timeStr",
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            color = GlassSuccess
                        )
                    }
                }
            }

            // Actions Strip
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 4.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                    IconButton(onClick = onEdit, modifier = Modifier.size(28.dp)) {
                        Icon(Icons.Default.Edit, contentDescription = "Edit", tint = MaterialTheme.colorScheme.onSurfaceVariant, modifier = Modifier.size(16.dp))
                    }
                    IconButton(onClick = onDuplicate, modifier = Modifier.size(28.dp)) {
                        Icon(Icons.Default.ContentCopy, contentDescription = "Duplicate", tint = MaterialTheme.colorScheme.onSurfaceVariant, modifier = Modifier.size(16.dp))
                    }
                    IconButton(onClick = onDelete, modifier = Modifier.size(28.dp)) {
                        Icon(Icons.Default.Delete, contentDescription = "Delete", tint = MaterialTheme.colorScheme.onSurfaceVariant, modifier = Modifier.size(16.dp))
                    }
                }

                Button(
                    onClick = onSendNow,
                    shape = RoundedCornerShape(10.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = ElectricBlue),
                    contentPadding = PaddingValues(horizontal = 14.dp, vertical = 6.dp)
                ) {
                    Icon(imageVector = Icons.AutoMirrored.Filled.Send, contentDescription = null, modifier = Modifier.size(13.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Send Now", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}

@Composable
private fun HistoryCard(entry: ScheduledReminderHistory) {
    val dateStr = SimpleDateFormat("MMM d, h:mm a", Locale.getDefault()).format(Date(entry.scheduledTime))
    val isSent = entry.status == ReminderStatus.SENT_CONFIRMED

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.35f))
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Text(text = entry.platform.iconEmoji, fontSize = 14.sp)
                    Text(
                        text = entry.templateTitle,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }

                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(6.dp))
                        .background(if (isSent) GlassSuccess.copy(alpha = 0.15f) else ElectricBlue.copy(alpha = 0.15f))
                        .padding(horizontal = 6.dp, vertical = 2.dp)
                ) {
                    Text(
                        text = if (isSent) "✓ Confirmed Sent" else entry.status.label,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        color = if (isSent) GlassSuccess else ElectricBlue
                    )
                }
            }

            Text(
                text = entry.messageContent,
                fontSize = 12.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = if (entry.recipient.isNotBlank()) "To: ${entry.recipient}" else "",
                    fontSize = 10.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Text(
                    text = dateStr,
                    fontSize = 10.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}
