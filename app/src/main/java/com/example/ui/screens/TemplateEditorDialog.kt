package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.data.model.MessageTemplate
import com.example.data.model.MessagingPlatform
import com.example.data.model.RepeatType
import com.example.data.model.ScheduleRule
import com.example.engine.TemplateEngine
import com.example.ui.theme.ElectricBlue

@Composable
fun TemplateEditorDialog(
    initialTemplate: MessageTemplate? = null,
    initialSchedule: ScheduleRule? = null,
    onSave: (MessageTemplate, ScheduleRule?) -> Unit,
    onDismiss: () -> Unit
) {
    var title by remember { mutableStateOf(initialTemplate?.title ?: "") }
    var content by remember { mutableStateOf(initialTemplate?.content ?: "") }
    var category by remember { mutableStateOf(initialTemplate?.category ?: "Daily Greetings") }
    var recipientName by remember { mutableStateOf(initialTemplate?.recipientName ?: "") }
    var recipientAddress by remember { mutableStateOf(initialTemplate?.recipientAddress ?: "") }
    var selectedPlatform by remember { mutableStateOf(initialTemplate?.platform ?: MessagingPlatform.SMS) }
    var isFavorite by remember { mutableStateOf(initialTemplate?.isFavorite ?: false) }

    // Schedule state
    var scheduleEnabled by remember { mutableStateOf(initialSchedule?.isEnabled ?: (initialSchedule != null)) }
    var repeatType by remember { mutableStateOf(initialSchedule?.repeatType ?: RepeatType.DAILY) }
    var hour by remember { mutableIntStateOf(initialSchedule?.hour ?: 7) }
    var minute by remember { mutableIntStateOf(initialSchedule?.minute ?: 0) }
    var snoozeMinutes by remember { mutableIntStateOf(initialSchedule?.snoozeDurationMinutes ?: 10) }

    val categories = listOf(
        "Daily Greetings", "Lunch Reminders", "Check-Ins", "Work Updates",
        "Evening Greetings", "Celebrations", "Thank You", "General"
    )

    val variableChips = listOf(
        "{name}" to "Name",
        "{nickname}" to "Nickname",
        "{date}" to "Date",
        "{day}" to "Day",
        "{time}" to "Time"
    )

    val resolvedPreview = remember(content, recipientName) {
        TemplateEngine.resolveTemplateVariables(
            templateText = content,
            recipientName = recipientName
        )
    }

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Card(
            modifier = Modifier
                .fillMaxWidth(0.95f)
                .padding(vertical = 16.dp),
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp)
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                // Header
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = if (initialTemplate == null) "Create Message Template" else "Edit Template",
                        fontSize = 19.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    IconButton(onClick = onDismiss, modifier = Modifier.size(28.dp)) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "Close",
                            tint = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }

                // Template Name
                OutlinedTextField(
                    value = title,
                    onValueChange = { title = it },
                    label = { Text("Template Title") },
                    placeholder = { Text("e.g., Morning Check-In") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )

                // Category selector
                Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    Text(
                        text = "Category",
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    LazyRow(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                        items(categories) { cat ->
                            val isSel = cat == category
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(if (isSel) ElectricBlue else MaterialTheme.colorScheme.surfaceVariant)
                                    .clickable { category = cat }
                                    .padding(horizontal = 10.dp, vertical = 6.dp)
                            ) {
                                Text(
                                    text = cat,
                                    fontSize = 12.sp,
                                    color = if (isSel) Color.White else MaterialTheme.colorScheme.onSurface,
                                    fontWeight = if (isSel) FontWeight.Bold else FontWeight.Normal
                                )
                            }
                        }
                    }
                }

                // Platform Selector
                Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    Text(
                        text = "Target Messaging App",
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        items(MessagingPlatform.entries) { plat ->
                            val isSel = plat == selectedPlatform
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(10.dp))
                                    .background(if (isSel) ElectricBlue.copy(alpha = 0.2f) else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
                                    .border(1.dp, if (isSel) ElectricBlue else Color.Transparent, RoundedCornerShape(10.dp))
                                    .clickable { selectedPlatform = plat }
                                    .padding(horizontal = 10.dp, vertical = 7.dp)
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                                ) {
                                    Text(text = plat.iconEmoji, fontSize = 14.sp)
                                    Text(
                                        text = plat.displayName,
                                        fontSize = 12.sp,
                                        color = if (isSel) ElectricBlue else MaterialTheme.colorScheme.onSurface,
                                        fontWeight = if (isSel) FontWeight.Bold else FontWeight.Normal
                                    )
                                }
                            }
                        }
                    }
                }

                // Recipient / Contact details
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    OutlinedTextField(
                        value = recipientName,
                        onValueChange = { recipientName = it },
                        label = { Text("Contact Name / Nickname") },
                        placeholder = { Text("e.g. Alex") },
                        singleLine = true,
                        modifier = Modifier.weight(1f)
                    )
                    OutlinedTextField(
                        value = recipientAddress,
                        onValueChange = { recipientAddress = it },
                        label = { Text("Phone / Email (Opt)") },
                        placeholder = { Text("+1234567890") },
                        singleLine = true,
                        modifier = Modifier.weight(1f)
                    )
                }

                // Message Content & Variable Chips
                Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    Text(
                        text = "Message Content",
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    // Variable insert chips
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(6.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(text = "Insert:", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        LazyRow(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                            items(variableChips) { (tag, label) ->
                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(6.dp))
                                        .background(ElectricBlue.copy(alpha = 0.15f))
                                        .clickable { content = "$content $tag" }
                                        .padding(horizontal = 6.dp, vertical = 3.dp)
                                ) {
                                    Text(text = "+ $label", fontSize = 10.sp, color = ElectricBlue, fontWeight = FontWeight.Bold)
                                }
                            }
                        }
                    }

                    OutlinedTextField(
                        value = content,
                        onValueChange = { content = it },
                        placeholder = { Text("Write your message template here... Use {name}, {day}, etc.") },
                        minLines = 3,
                        maxLines = 6,
                        modifier = Modifier.fillMaxWidth()
                    )
                }

                // Live Resolved Preview Card
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(12.dp))
                        .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
                        .padding(10.dp)
                ) {
                    Column {
                        Text(
                            text = "PREVIEW (RESOLVED VARIABLES)",
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            color = ElectricBlue,
                            letterSpacing = 0.5.sp
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = resolvedPreview.ifBlank { "(Message preview will appear here)" },
                            fontSize = 13.sp,
                            color = MaterialTheme.colorScheme.onSurface,
                            lineHeight = 18.sp
                        )
                    }
                }

                // Schedule Settings Card
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(14.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.35f))
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(12.dp),
                        verticalArrangement = Arrangement.spacedBy(10.dp)
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
                                Icon(Icons.Default.Schedule, contentDescription = null, tint = ElectricBlue, modifier = Modifier.size(18.dp))
                                Text(text = "Schedule Reminders", fontSize = 14.sp, fontWeight = FontWeight.Bold)
                            }
                            Switch(
                                checked = scheduleEnabled,
                                onCheckedChange = { scheduleEnabled = it },
                                colors = SwitchDefaults.colors(checkedThumbColor = Color.White, checkedTrackColor = ElectricBlue)
                            )
                        }

                        if (scheduleEnabled) {
                            // Repeat frequency
                            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                                Text(text = "Repeat Schedule", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                LazyRow(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                                    items(RepeatType.entries) { rep ->
                                        val isSel = rep == repeatType
                                        Box(
                                            modifier = Modifier
                                                .clip(RoundedCornerShape(8.dp))
                                                .background(if (isSel) ElectricBlue else MaterialTheme.colorScheme.surface)
                                                .clickable { repeatType = rep }
                                                .padding(horizontal = 8.dp, vertical = 5.dp)
                                        ) {
                                            Text(
                                                text = rep.label,
                                                fontSize = 11.sp,
                                                color = if (isSel) Color.White else MaterialTheme.colorScheme.onSurface
                                            )
                                        }
                                    }
                                }
                            }

                            // Time Picker (Hour & Minute Selector)
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(text = "Reminder Time", fontSize = 13.sp, fontWeight = FontWeight.Medium)

                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                                ) {
                                    // Hour chip
                                    Box(
                                        modifier = Modifier
                                            .clip(RoundedCornerShape(8.dp))
                                            .background(MaterialTheme.colorScheme.surface)
                                            .clickable {
                                                hour = (hour + 1) % 24
                                            }
                                            .padding(horizontal = 12.dp, vertical = 6.dp)
                                    ) {
                                        val isPm = hour >= 12
                                        val displayHour = when {
                                            hour == 0 -> 12
                                            hour > 12 -> hour - 12
                                            else -> hour
                                        }
                                        Text(
                                            text = String.format("%02d", displayHour),
                                            fontSize = 15.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = ElectricBlue
                                        )
                                    }
                                    Text(text = ":", fontWeight = FontWeight.Bold)
                                    // Minute chip
                                    Box(
                                        modifier = Modifier
                                            .clip(RoundedCornerShape(8.dp))
                                            .background(MaterialTheme.colorScheme.surface)
                                            .clickable {
                                                minute = (minute + 15) % 60
                                            }
                                            .padding(horizontal = 12.dp, vertical = 6.dp)
                                    ) {
                                        Text(
                                            text = String.format("%02d", minute),
                                            fontSize = 15.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = ElectricBlue
                                        )
                                    }
                                    // AM / PM indicator
                                    Box(
                                        modifier = Modifier
                                            .clip(RoundedCornerShape(8.dp))
                                            .background(ElectricBlue.copy(alpha = 0.15f))
                                            .clickable {
                                                hour = if (hour < 12) hour + 12 else hour - 12
                                            }
                                            .padding(horizontal = 8.dp, vertical = 6.dp)
                                    ) {
                                        Text(
                                            text = if (hour >= 12) "PM" else "AM",
                                            fontSize = 13.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = ElectricBlue
                                        )
                                    }
                                }
                            }
                        }
                    }
                }

                // Bottom Action Buttons
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    OutlinedButton(
                        onClick = onDismiss,
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text("Cancel")
                    }
                    Button(
                        onClick = {
                            if (title.isNotBlank() && content.isNotBlank()) {
                                val templateId = initialTemplate?.id ?: System.currentTimeMillis().toString()
                                val newTemplate = MessageTemplate(
                                    id = templateId,
                                    title = title.trim(),
                                    content = content.trim(),
                                    category = category,
                                    recipientName = recipientName.trim(),
                                    recipientAddress = recipientAddress.trim(),
                                    platform = selectedPlatform,
                                    isFavorite = isFavorite,
                                    updatedAt = System.currentTimeMillis()
                                )

                                val newSchedule = if (scheduleEnabled) {
                                    ScheduleRule(
                                        id = initialSchedule?.id ?: "sched_$templateId",
                                        templateId = templateId,
                                        repeatType = repeatType,
                                        hour = hour,
                                        minute = minute,
                                        isEnabled = true,
                                        snoozeDurationMinutes = snoozeMinutes
                                    )
                                } else null

                                onSave(newTemplate, newSchedule)
                            }
                        },
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = ElectricBlue),
                        enabled = title.isNotBlank() && content.isNotBlank()
                    ) {
                        Text("Save Template")
                    }
                }
            }
        }
    }
}
