package com.example.ui.screens

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Keyboard
import androidx.compose.material.icons.filled.RestartAlt
import androidx.compose.material.icons.filled.TouchApp
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.KeyboardPreferences
import com.example.data.model.ClipboardSnippet
import com.example.data.model.KeyboardSettings
import com.example.data.model.MessageTemplate
import com.example.data.model.TextShortcut
import com.example.ui.components.KeyboardLayout
import com.example.ui.theme.ElectricBlue
import com.example.util.KeyboardManager

@Composable
fun TestLabScreen(
    preferences: KeyboardPreferences,
    settings: KeyboardSettings,
    shortcuts: List<TextShortcut>,
    snippets: List<ClipboardSnippet>,
    templates: List<MessageTemplate> = emptyList(),
    customWords: Set<String>,
    onOpenSettings: () -> Unit,
    onOpenMessagePlanner: () -> Unit = onOpenSettings
) {
    val context = LocalContext.current
    var typedText by remember { mutableStateOf("Type anything here to test Crystal Keyboard...") }
    var selectedWallpaperIdx by remember { mutableIntStateOf(1) } // macOS dark default

    val isKeyboardActive = remember { KeyboardManager.isKeyboardSelected(context) }

    val charCount = typedText.length
    val wordCount = if (typedText.isBlank()) 0 else typedText.trim().split(Regex("\\s+")).size

    fun copyToClipboard() {
        val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as? ClipboardManager
        val clip = ClipData.newPlainText("Crystal Keyboard Test", typedText)
        clipboard?.setPrimaryClip(clip)
        Toast.makeText(context, "Copied to clipboard!", Toast.LENGTH_SHORT).show()
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(WallpaperPresets[selectedWallpaperIdx].brush)
    ) {
        // Top Toolbar
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 10.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "Keyboard Test Lab",
                        fontSize = 22.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                    Text(
                        text = "Interactive typing arena & metrics",
                        fontSize = 12.sp,
                        color = Color.White.copy(alpha = 0.75f)
                    )
                }

                // Wallpaper switcher dots
                Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    WallpaperPresets.forEachIndexed { i, p ->
                        Box(
                            modifier = Modifier
                                .size(22.dp)
                                .clip(CircleShape)
                                .background(p.brush)
                                .border(
                                    width = if (selectedWallpaperIdx == i) 2.dp else 1.dp,
                                    color = if (selectedWallpaperIdx == i) ElectricBlue else Color.White.copy(alpha = 0.4f),
                                    shape = CircleShape
                                )
                                .clickable { selectedWallpaperIdx = i }
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // IME Status Chip
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(10.dp))
                    .background(Color.Black.copy(alpha = 0.35f))
                    .padding(horizontal = 10.dp, vertical = 6.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Info,
                        contentDescription = null,
                        tint = ElectricBlue,
                        modifier = Modifier.size(14.dp)
                    )
                    Text(
                        text = if (isKeyboardActive) "Crystal IME is system-active" else "Testing in in-app simulation mode",
                        fontSize = 11.sp,
                        color = Color.White.copy(alpha = 0.9f)
                    )
                }

                if (!isKeyboardActive) {
                    Text(
                        text = "Activate IME →",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = ElectricBlue,
                        modifier = Modifier.clickable { KeyboardManager.showInputMethodPicker(context) }
                    )
                }
            }
        }

        // Editable Text Box Area
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
                .padding(horizontal = 12.dp, vertical = 4.dp),
            shape = RoundedCornerShape(18.dp),
            colors = CardDefaults.cardColors(containerColor = Color.Black.copy(alpha = 0.45f))
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(14.dp)
            ) {
                // Actions & metrics row
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "$charCount chars • $wordCount words",
                        fontSize = 12.sp,
                        color = Color.White.copy(alpha = 0.6f)
                    )

                    Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                        IconButton(
                            onClick = { copyToClipboard() },
                            modifier = Modifier.size(30.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.ContentCopy,
                                contentDescription = "Copy Text",
                                tint = Color.White.copy(alpha = 0.8f),
                                modifier = Modifier.size(16.dp)
                            )
                        }
                        IconButton(
                            onClick = { typedText = "" },
                            modifier = Modifier.size(30.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Clear,
                                contentDescription = "Clear Text",
                                tint = Color.White.copy(alpha = 0.8f),
                                modifier = Modifier.size(16.dp)
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(6.dp))

                // Typed Text Display
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .verticalScroll(rememberScrollState())
                ) {
                    Text(
                        text = if (typedText.isEmpty()) "Tap on any key below to start typing..." else typedText,
                        color = if (typedText.isEmpty()) Color.White.copy(alpha = 0.4f) else Color.White,
                        fontSize = 16.sp,
                        lineHeight = 24.sp
                    )
                }
            }
        }

        // Integrated In-App Crystal Keyboard
        KeyboardLayout(
            settings = settings,
            shortcuts = shortcuts,
            snippets = snippets,
            templates = templates,
            customWords = customWords,
            currentTextBeforeCursor = typedText,
            actionLabel = "Done",
            onTextInput = { str ->
                typedText += str
            },
            onDelete = {
                if (typedText.isNotEmpty()) {
                    typedText = typedText.dropLast(1)
                }
            },
            onEnter = {
                typedText += "\n"
            },
            onOpenSettings = onOpenSettings,
            onOpenMessagePlanner = onOpenMessagePlanner,
            onAddSnippet = { preferences.addSnippet(it) },
            onDeleteSnippet = { preferences.deleteSnippet(it) },
            onUpdateOneHandedMode = { preferences.updateSettings(settings.copy(oneHandedMode = it)) },
            onCycleTheme = {
                val all = com.example.data.model.KeyboardTheme.AllThemes
                val idx = all.indexOfFirst { it.id == settings.themeId }
                val next = all[(if (idx >= 0) idx + 1 else 0) % all.size]
                preferences.selectTheme(next.id)
            }
        )
    }
}
