package com.example.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.ContentPaste
import androidx.compose.material.icons.filled.FormatPaint
import androidx.compose.material.icons.filled.KeyboardVoice
import androidx.compose.material.icons.filled.MoreHoriz
import androidx.compose.material.icons.filled.Palette
import androidx.compose.material.icons.filled.PhoneAndroid
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.StayCurrentPortrait
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.KeyboardTheme

@Composable
fun SuggestionStrip(
    suggestions: List<String>,
    theme: KeyboardTheme,
    onSelectSuggestion: (String) -> Unit,
    onOpenSettings: () -> Unit,
    onOpenEmoji: () -> Unit,
    onOpenClipboard: () -> Unit,
    onOpenTemplates: () -> Unit = {},
    onToggleOneHanded: () -> Unit,
    onCycleTheme: () -> Unit,
    onVoiceInput: (() -> Unit)? = null
) {
    var showQuickTools by remember { mutableStateOf(false) }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(44.dp)
            .padding(horizontal = 8.dp, vertical = 3.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        // Left expander icon
        IconButton(
            onClick = { showQuickTools = !showQuickTools },
            modifier = Modifier.size(36.dp)
        ) {
            Icon(
                imageVector = if (showQuickTools) Icons.Default.Close else Icons.Default.MoreHoriz,
                contentDescription = "Quick Tools",
                tint = theme.keyTextColor.copy(alpha = 0.75f),
                modifier = Modifier.size(20.dp)
            )
        }

        if (showQuickTools) {
            // Quick tools row
            Row(
                modifier = Modifier.weight(1f),
                horizontalArrangement = Arrangement.SpaceEvenly,
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = onOpenClipboard, modifier = Modifier.size(34.dp)) {
                    Icon(
                        imageVector = Icons.Default.ContentPaste,
                        contentDescription = "Clipboard",
                        tint = theme.accentColor,
                        modifier = Modifier.size(19.dp)
                    )
                }
                IconButton(onClick = onOpenTemplates, modifier = Modifier.size(34.dp)) {
                    Text(text = "📝", fontSize = 16.sp)
                }
                IconButton(onClick = onCycleTheme, modifier = Modifier.size(34.dp)) {
                    Icon(
                        imageVector = Icons.Default.Palette,
                        contentDescription = "Themes",
                        tint = theme.accentColor,
                        modifier = Modifier.size(19.dp)
                    )
                }
                IconButton(onClick = onToggleOneHanded, modifier = Modifier.size(34.dp)) {
                    Icon(
                        imageVector = Icons.Default.PhoneAndroid,
                        contentDescription = "One Handed Mode",
                        tint = theme.keyTextColor.copy(alpha = 0.8f),
                        modifier = Modifier.size(19.dp)
                    )
                }
                if (onVoiceInput != null) {
                    IconButton(onClick = onVoiceInput, modifier = Modifier.size(34.dp)) {
                        Icon(
                            imageVector = Icons.Default.KeyboardVoice,
                            contentDescription = "Voice Input",
                            tint = theme.keyTextColor.copy(alpha = 0.8f),
                            modifier = Modifier.size(19.dp)
                        )
                    }
                }
                IconButton(onClick = onOpenSettings, modifier = Modifier.size(34.dp)) {
                    Icon(
                        imageVector = Icons.Default.Settings,
                        contentDescription = "Settings",
                        tint = theme.keyTextColor.copy(alpha = 0.8f),
                        modifier = Modifier.size(19.dp)
                    )
                }
            }
        } else {
            // Word suggestions row
            Row(
                modifier = Modifier.weight(1f),
                horizontalArrangement = Arrangement.spacedBy(6.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                val displayList = suggestions.take(3)
                if (displayList.isEmpty()) {
                    Box(
                        modifier = Modifier.weight(1f),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "Crystal Smart Keyboard",
                            color = theme.keyTextColor.copy(alpha = 0.35f),
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Medium
                        )
                    }
                } else {
                    displayList.forEachIndexed { index, suggestion ->
                        val isCenter = index == (displayList.size / 2)
                        val shape = RoundedCornerShape(12.dp)

                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .height(34.dp)
                                .clip(shape)
                                .background(
                                    if (isCenter) theme.accentColor.copy(alpha = 0.22f)
                                    else theme.keyBgColor.copy(alpha = 0.20f)
                                )
                                .border(
                                    width = 1.dp,
                                    color = if (isCenter) theme.accentColor.copy(alpha = 0.65f)
                                    else theme.borderColor.copy(alpha = 0.25f),
                                    shape = shape
                                )
                                .clickable { onSelectSuggestion(suggestion) }
                                .padding(horizontal = 6.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = suggestion,
                                color = if (isCenter) theme.keyTextColor else theme.keyTextColor.copy(alpha = 0.85f),
                                fontSize = 13.sp,
                                fontWeight = if (isCenter) FontWeight.SemiBold else FontWeight.Normal,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis,
                                textAlign = TextAlign.Center
                            )
                        }
                    }
                }
            }
        }

        // Right quick emoji button
        IconButton(
            onClick = onOpenEmoji,
            modifier = Modifier.size(36.dp)
        ) {
            Text(
                text = "😀",
                fontSize = 16.sp
            )
        }
    }
}
